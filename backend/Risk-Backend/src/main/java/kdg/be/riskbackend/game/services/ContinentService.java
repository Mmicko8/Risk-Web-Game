package kdg.be.riskbackend.game.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.transaction.Transactional;
import kdg.be.riskbackend.game.domain.game.PlayerInGame;
import kdg.be.riskbackend.game.domain.map.Continent;
import kdg.be.riskbackend.game.domain.map.Territory;
import kdg.be.riskbackend.game.repositories.ContinentRepository;
import kdg.be.riskbackend.game.repositories.TerritoryRepository;
import kdg.be.riskbackend.game.util.ContinentName;
import kdg.be.riskbackend.game.util.TerritoryUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Handles the logic for the continents
 */
@Service
@Slf4j
@AllArgsConstructor
public class ContinentService {
    private final TerritoryRepository territoryRepository;
    private final TerritoryService territoryService;
    private final ContinentRepository continentRepository;

    /**
     * generates continents
     *
     * @return a list of continents
     */
    public List<Continent> generateContinents() {
        List<Continent> continents = new ArrayList<>();
        TerritoryUtil.generateAllContinentsMap().forEach((continentName, territory) -> {
            List<Territory> territories = territoryService.generateTerritories(territory);
            int bonusTroops = getBonusTroopsOfContinentName(continentName);
            continents.add(new Continent(territories, continentName.getName(), bonusTroops));
        });
        return continents;
    }

    /**
     * gets the bonus troops that a player earns if he owns the hole continent
     *
     * @param continentName the name of the continent
     * @return the bonus troops of the continent
     */
    private int getBonusTroopsOfContinentName(ContinentName continentName) {
        return switch (continentName) {
            case AFRICA -> 3;
            case ASIA -> 7;
            case AUSTRALIA, SOUTH_AMERICA -> 2;
            case EUROPE, NORTH_AMERICA -> 5;
        };
    }

    /**
     * gets the amount of troops each player can place on the board at the start of the game
     *
     * @param playerCount the amount of players in the game
     * @return the amount of troops each player can place on the board at the start of the game
     */
    private int getTroopsToPlacePerPlayerAtStart(int playerCount) {
        int troopsToPlacePerPlayer;
        switch (playerCount) {
            case 2 -> troopsToPlacePerPlayer = 40;
            case 3 -> troopsToPlacePerPlayer = 35;
            case 4 -> troopsToPlacePerPlayer = 30;
            case 5 -> troopsToPlacePerPlayer = 25;
            default -> troopsToPlacePerPlayer = 20;
        }
        return troopsToPlacePerPlayer;
    }

    /**
     * fills the board randomly with troops (1 troop per territory)
     *
     * @param continents    the continents of the game
     * @param playersInGame the players in the game
     * @return a list of continents of the game with the troops placed on the board
     */
    public List<Continent> fillBoardRandomly(long gameId, List<Continent> continents,
                                             List<PlayerInGame> playersInGame) {
        int troopsToPlacePerPlayer = getTroopsToPlacePerPlayerAtStart(playersInGame.size());
        Map<PlayerInGame, Integer> troopsForEachPlayer = new HashMap<>();
        playersInGame.forEach(p -> troopsForEachPlayer.put(p, troopsToPlacePerPlayer));
        int turnCounter = 0;
        do {
            turnCounter = placeTroopsOnContinents(gameId, playersInGame, troopsForEachPlayer,
                    turnCounter);
        } while (continents.stream()
                .anyMatch(c -> c.getTerritories().stream().anyMatch(t -> t.getTroops() == 0))
                || troopsForEachPlayer.values().stream().anyMatch(t -> t > 0));

        return continents;
    }

    /**
     * places troops on the continents
     *
     * @param playersInGame       the players in the game
     * @param troopsForEachPlayer the amount of troops each player has to place
     * @param turnCounter         the index of the player how is placing troops
     * @return the index of the player how is placing troops
     */
    private int placeTroopsOnContinents(long gameId, List<PlayerInGame> playersInGame,
                                        Map<PlayerInGame, Integer> troopsForEachPlayer,
                                        int turnCounter) {
        for (var territory : territoryService.getAllTerritoriesOfGame(gameId)) {
            var updatedTerritory = placeTroop(playersInGame, troopsForEachPlayer, territory,
                    turnCounter % playersInGame.size());
            territoryRepository.save(updatedTerritory);
            turnCounter++;
        }
        return turnCounter;
    }

    /**
     * places a troop on a territory
     *
     * @param playersInGame       the players in the game
     * @param troopsForEachPlayer the amount of troops each player has to place
     * @param territory           the territory where the troop will be placed
     * @param currentPlayer       the index of the player how is placing troops
     * @return the territory with the troop placed on it
     */
    @Transactional
    public Territory placeTroop(List<PlayerInGame> playersInGame,
                                Map<PlayerInGame, Integer> troopsForEachPlayer, Territory territory,
                                int currentPlayer) {
        var playerInGame = playersInGame.get(currentPlayer);
        var troopsPlayerCanPlace = troopsForEachPlayer.get(playerInGame);
        if (troopsPlayerCanPlace != 0) {
            if (territory.getOwner() == null) {
                territory.setOwner(playerInGame);
                log.info("set owner of territory {} to {}", territory.getName(),
                        playerInGame.getPlayer().getUsername());
            }
            if (Objects.equals(playerInGame.getPlayer().getUsername(),
                    territory.getOwner().getPlayer().getUsername())) {
                territory.setTroops(territory.getTroops() + 1);
                troopsForEachPlayer.put(playerInGame, (troopsPlayerCanPlace - 1));
                log.info("Placed troop on territory: " + territory.getName() + " of player: " +
                        playerInGame.getPlayer().getUsername());
            }
        }
        return territory;
    }

    /**
     * get all continents of the game
     *
     * @param gameId the id of the game
     * @return a list of continents
     */
    public List<Continent> getContinentsWithTerritories(long gameId) {
        return continentRepository.findContinentWithTerritories(gameId);
    }

    /**
     * check if every continent is owned by one player
     *
     * @param playerInGame the player in the game
     * @param gameId the id of the game
     * @return the player who owns all continents if there is one, null otherwise
     */
    public PlayerInGame checkIfAllContinentsAreOwnedByOnePlayer(PlayerInGame playerInGame,
                                                                long gameId) {
        List<Continent> continents = getContinentsWithTerritories(gameId);
        List<Continent> continentsOwnedByPlayer = new ArrayList<>();
        //check if every continent is owned by one player
        for (var continent : continents) {
            if (continent.getTerritories().stream()
                    .allMatch(territory -> territory.getOwner().equals(playerInGame))) {
                continentsOwnedByPlayer.add(continent);
            }
        }
        //check if the amount of continents owned is the same as the amount of continents in the game
        if (continentsOwnedByPlayer.size() == continents.size()) {
            playerInGame.setWinner(true);
            return playerInGame;
        }
        return null;
    }
}
