package kdg.be.riskbackend.game.services;

import kdg.be.riskbackend.game.domain.game.PlayerInGame;
import kdg.be.riskbackend.game.domain.map.Neighbor;
import kdg.be.riskbackend.game.domain.map.Territory;
import kdg.be.riskbackend.game.dtos.phases.TroopsLost;
import kdg.be.riskbackend.game.exceptions.InvalidTroopException;
import kdg.be.riskbackend.game.exceptions.PhaseException;
import kdg.be.riskbackend.game.exceptions.TerritoryException;
import kdg.be.riskbackend.game.repositories.TerritoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * handles the logic of the territories
 */
@Service
@AllArgsConstructor
public class TerritoryService {
    private final TerritoryRepository territoryRepository;
    private final NeighborService neighborService;
    private final PlayerInGameService playerInGameService;

    /**
     * places troops on a territory
     *
     * @param territoryId the id of the territory
     * @param troopAmount the amount of troops to place
     */
    public void placeTroops(long territoryId, int troopAmount) {
        Territory territory = territoryRepository.findById(territoryId)
                .orElseThrow(() -> new TerritoryException("territory not found"));
        var phase = territoryRepository.getPhaseByTerritoryId(territoryId);
        if (!phase.equals("REINFORCEMENT")) throw new PhaseException("the game is not in the fortification phase");
        territory.setTroops(territory.getTroops() + troopAmount);
        territoryRepository.save(territory);
    }

    /**
     * generates all the territories
     *
     * @param countries the names of the countries of the game
     * @return a list of territories
     */
    public List<Territory> generateTerritories(Map<String, List<String>> countries) {
        List<Territory> territories = new ArrayList<>();
        countries.forEach((territoryName, surroundingTerritoryNames) -> {
            List<Neighbor> neighbors = neighborService.generateNeighbors(surroundingTerritoryNames);
            territories.add(new Territory(territoryName, neighbors));
        });
        return territories;
    }

    /**
     * gets all the territories of a game
     *
     * @param gameId the id of the game
     * @return a list of territories
     */
    public List<Territory> getAllTerritoriesOfGame(long gameId) {
        return territoryRepository.findByGame(gameId);
    }

    /**
     * gets all the territories of a game with their surrounding territories (neighbors)
     *
     * @param gameId the id of the game
     * @return a list of territories with each containing a list of surrounding territories
     */
    public List<Territory> getAllTerritoriesOfGameWithNeighbors(long gameId) {
        return territoryRepository.findAllByGameWithNeighbors(gameId);
    }

    /**
     * gets the neighbors of a territory
     *
     * @param territory the name of the territory
     * @param gameId    the id of the game
     * @return a list of neighbors
     */
    public List<Neighbor> getTerritoryNeighborsByTerritoryNameAndGameId(String territory,
                                                                        long gameId) {
        return territoryRepository.findTerritoryNeighborsByTerritoryNameAndGameId(territory, gameId);
    }

    /**
     * fortify a territory with troops
     *
     * @param continentFrom  the name of the continent the troops are coming from
     * @param continentTo    the name of the continent the troops are going to
     * @param amountOfTroops the amount of troops to fortify
     */
    @Transactional
    public void fortify(Territory continentFrom, Territory continentTo, int amountOfTroops) {
        if ((continentFrom.getTroops() - 1) < amountOfTroops) {
            throw new InvalidTroopException("not enough troops");
        }
        continentFrom.setTroops(continentFrom.getTroops() - amountOfTroops);
        continentTo.setTroops(continentTo.getTroops() + amountOfTroops);
        territoryRepository.save(continentFrom);
        territoryRepository.save(continentTo);
    }

    /**
     * gets a territory
     *
     * @param attackerTerritoryId the id of the territory
     * @return a territory
     */
    public Territory getTerritoryById(long attackerTerritoryId) throws TerritoryException {
        return territoryRepository.findById(attackerTerritoryId)
                .orElseThrow(() -> new TerritoryException("territory not found"));
    }

    /**
     * gets a territory
     *
     * @param territoryName the name of the territory
     * @param gameId        the id of the game
     * @return a territory
     */
    public Territory getTerritoryByNameAndGame(String territoryName, long gameId) {

        return territoryRepository.findByNameAndGameId(territoryName, gameId);

    }

    /**
     * gets a territory with neighbors and his owner
     *
     * @param territoryName the name of the territory
     * @param gameId        the id of the game
     * @return a territory
     */
    public Territory getTerritoryByNameAndGameWithNeighborsAndOwner(String territoryName, long gameId) {
        return territoryRepository.findByNameAndGameIdWithNeighbors(territoryName, gameId);
    }

    /**
     * updates the troops of a territory after an attack
     *
     * @param attackerDices     the amount of dices the attacker rolled
     * @param defenderDices     the amount of dices the defender rolled
     * @param attackerTerritory the territory of the attacker
     * @param defenderTerritory the territory of the defender
     * @return the troops that de attacker and de defender lost
     */
    @Transactional
    public TroopsLost updateTerritoriesAfterAttack(List<Integer> attackerDices, List<Integer> defenderDices,
                                                   Territory attackerTerritory, Territory defenderTerritory) {
        int attackerTroopsLost = 0;
        int defenderTroopsLost = 0;
        int minimumDiceAmount = Math.min(defenderDices.size(), attackerDices.size());
        for (int i = 0; i < minimumDiceAmount; i++) {
            if (attackerDices.get(i) > defenderDices.get(i)) {
                defenderTerritory.loseTroop();
                defenderTroopsLost++;
            } else {
                attackerTerritory.loseTroop();
                attackerTroopsLost++;
            }
        }
        //if the defender has no more troops, the territory loses its owner | edit: attacker becomes owner
        if (defenderTerritory.getTroops() < 1) {
            handleIfPlayerLostTerritory(attackerTerritory, defenderTerritory, attackerTerritory.getOwner(), defenderTerritory.getOwner());
        }
        territoryRepository.save(attackerTerritory);
        territoryRepository.save(defenderTerritory);
        return new TroopsLost(attackerTroopsLost, defenderTroopsLost);
    }

    /**
     * handles if a player lost a territory
     *
     * @param attackerTerritory the territory of the attacker
     * @param defenderTerritory the territory of the defender
     */
    private void handleIfPlayerLostTerritory(Territory attackerTerritory, Territory defenderTerritory, PlayerInGame attacker, PlayerInGame defender) {
        //gets the player and defender territory id
        var playerId = defenderTerritory.getOwner().getPlayer().getPlayerId();
        var defenderTerritoryId = defenderTerritory.getContinent().getGame().getGameId();
        //change owner to the player who attacked
        defenderTerritory.setOwner(attackerTerritory.getOwner());
        //if the player lost all his territories, he loses the game and the current player gets his cards
        var territories = territoryRepository.getTerritoriesByOwnerAndGame(playerId, defenderTerritoryId);
        if (territories.size() == 0) {
            playerInGameService.transferCards(defender, attacker);
        }
        playerInGameService.setCurrentPlayerConqueredATerritoryInHisTurn(attackerTerritory.getOwner(), true);
    }

    /**
     * calculates the amount of remaining troops the current player has to reinforce
     *
     * @param territoryId the id of the territory
     * @param troopAmount the amount of troops to reinforce
     * @return the number of remaining troops to reinforce
     */
    public int calculateRemainingTroops(long territoryId, int troopAmount) {
        var territory = territoryRepository.findById(territoryId)
                .orElseThrow(() -> new TerritoryException("territory not found"));
        var troopsOver = territory.getOwner().getRemainingTroopsToReinforce() - troopAmount;
        if (troopsOver < 0) throw new InvalidTroopException("not enough troops");
        setOwnersRemainingTroopsToReinforce(territoryId, troopsOver);
        return troopsOver;
    }

    /**
     * sets the remaining troops of the owner of a territory
     *
     * @param territoryId    the id of the territory
     * @param amountOfTroops the amount of troops to set
     */
    public void setOwnersRemainingTroopsToReinforce(long territoryId, int amountOfTroops) {
        var territory = territoryRepository.findByIdWithOwner(territoryId)
                .orElseThrow(() -> new TerritoryException("territory not found"));
        playerInGameService.setOwnersRemainingTroopsToReinforce(
                territory.getOwner().getPlayerInGameId(), amountOfTroops);
    }

    /**
     * gets a random territory of a game
     *
     * @param gameId the id of the game
     * @return a random territory
     */
    public Territory getRandomTerritory(long gameId) {
        var territories = territoryRepository.findByGame(gameId);
        return territories.get(new Random().nextInt(territories.size()));

    }

    /**
     * saves a territory
     *
     * @param territory the territory to save
     * @return the saved territory
     */
    public Territory save(Territory territory) {
        return territoryRepository.save(territory);
    }

    /**
     * checks if a player has no territories left
     *
     * @param gameId the id of the game
     */
    public void setPlayersWithNoTerritoriesToLost(long gameId) {
        var players = playerInGameService.getPlayersInGame(gameId);
        var territories = territoryRepository.findAllByGameIdWithOwner(gameId);
        for (var player : players) {
            if (territories.stream().noneMatch(territory -> territory.getOwner().getPlayerInGameId() == player.getPlayerInGameId())) {
                playerInGameService.setPlayerLost(player.getPlayerInGameId());
            }
        }
    }

    public int getMaxAmountOfDefenders(int troops) {
        final int MAX_DEFENDERS = 2;
        if (troops < 1) throw new InvalidTroopException("Defender has no troops");
        return Math.min(troops, MAX_DEFENDERS);
    }
}
