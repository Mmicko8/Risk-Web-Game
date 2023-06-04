package kdg.be.riskbackend.game.services;

import kdg.be.riskbackend.ai.services.AiApiService;
import kdg.be.riskbackend.game.domain.card.GameCard;
import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.domain.game.Phase;
import kdg.be.riskbackend.game.domain.game.PlayerInGame;
import kdg.be.riskbackend.game.domain.map.Continent;
import kdg.be.riskbackend.game.domain.map.Territory;
import kdg.be.riskbackend.game.dtos.card.ExchangeCardsDto;
import kdg.be.riskbackend.game.dtos.phases.AttackDto;
import kdg.be.riskbackend.game.dtos.phases.AttackResponse;
import kdg.be.riskbackend.game.dtos.phases.FortifyDto;
import kdg.be.riskbackend.game.dtos.phases.TroopsLost;
import kdg.be.riskbackend.game.exceptions.InvalidTroopException;
import kdg.be.riskbackend.game.exceptions.NotAdjacentTerritoryException;
import kdg.be.riskbackend.game.exceptions.PhaseException;
import kdg.be.riskbackend.game.repositories.GameRepository;
import kdg.be.riskbackend.game.util.Dice;
import kdg.be.riskbackend.identity.exceptions.AttackException;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.exceptions.InvalidIdException;
import kdg.be.riskbackend.lobby.exceptions.NotEnoughPlayersException;
import kdg.be.riskbackend.lobby.services.LobbyService;
import kdg.be.riskbackend.shop.service.LoyaltyPointService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class GameService {
    public final FortificationTerritoryFinder fortificationTerritoryFinder;
    private final GameRepository gameRepository;
    private final CardService cardService;
    private final ContinentService continentService;
    private final TerritoryService territoryService;
    private final PlayerInGameService playerInGameService;
    private final AiApiService aiApiService;
    private final LoyaltyPointService loyaltyPointService;
    private final LobbyService lobbyService;
    private final PlayerService playerService;

    /**
     * Starts a new game
     *
     * @param lobby the lobby for which the game needs to start
     * @return the game
     */
    @Transactional
    public Game startGame(Lobby lobby) {
        if (lobby.getPlayers().size() < 2)
            throw new NotEnoughPlayersException("Must have at least 2 players in the lobby to start a game");
        List<Continent> continents = continentService.generateContinents();
        Game game = new Game(LocalDateTime.now(), lobby.getTimer());
        Game persistedGame = saveGame(game);
        //continents
        Game finalPersistedGame = persistedGame;
        continents.forEach(continent -> continent.setGame(finalPersistedGame));
        persistedGame.setContinents(continents);
        //players in game
        persistedGame = playerInGameService.addPlayersToGame(persistedGame, lobby.getPlayers());
        cardService.generateCards(game);
        //cards in game
        persistedGame = gameRepository.save(persistedGame);
        lobbyService.closeLobby(lobby);
        game = randomSeeding(persistedGame);
        if (playerInGameService.getCurrentPlayerInGame(game).getPlayer().isAi()) {
            aiApiService.getMove(game);
        }
        return game;
    }

    /**
     * Randomly assigns the territories to the players
     *
     * @param game the game
     * @return the game
     */
    @Transactional
    public Game randomSeeding(Game game) {
        //random seeding
        var continents = continentService.fillBoardRandomly(game.getGameId(), game.getContinents(),
                game.getPlayersInGame());
        calculateReinforceTroops(game.getGameId());
        game.setContinents(continents);
        return saveGame(game);
    }

    /**
     * Gets the next player of the game
     *
     * @param gameId the id of the game
     * @return the next player of the game
     */
    public PlayerInGame getNextTurnPlayerOfGame(long gameId) {
        //get the game
        var game = gameRepository.findGameByIdWithPlayers(gameId)
                .orElseThrow(() -> new InvalidIdException("Game with given id " + gameId + " does not exist"));
        //check if game is in fortification
        if (Phase.FORTIFICATION != game.getPhase()) throw new PhaseException("Game is not in fortification");
        //set to next player
        game = goToNextTurnInGame(game);
        game.setAfkThreshold(LocalDateTime.now().plusSeconds(game.getTimer()));
        game.setPhase(Phase.REINFORCEMENT);
        game.addTurn();
        saveGame(game);
        calculateReinforceTroops(gameId);
        game = gameRepository.findGameByIdWithPlayers(gameId)
                .orElseThrow(() -> new InvalidIdException("Game with given id " + gameId + " does not exist"));
        if (playerInGameService.getCurrentPlayerInGame(game).getPlayer().isAi()) {
            game = getGameState(gameId);
            aiApiService.getMove(game);
        }
        game = gameRepository.findGameByIdWithPlayers(gameId)
                .orElseThrow(() -> new InvalidIdException("Game with given id " + gameId + " does not exist"));
        return playerInGameService.getCurrentPlayerInGame(game);
    }

    /**
     * Gets the next player of the game if the player has lost he gets skipped
     *
     * @param game the game where you want to go to the next turn
     * @return the next player of the game
     */
    Game goToNextTurnInGame(Game game) {
        if (game.getCurrentPlayerIndex() == (game.getPlayersInGame().size() - 1)) {
            game.setCurrentPlayerIndex(0);
        } else {
            game.nextPlayer();
        }
        if (playerInGameService.getCurrentPlayerInGame(game).isHasLost()) goToNextTurnInGame(game);
        return game;
    }

    /**
     * Gets the next phase of the game
     *
     * @param gameId the id of the game
     * @return the next phase the game is in
     */
    public Phase nextPhase(long gameId) {
        var game = gameRepository.findGameByIdWithPlayers(gameId).orElseThrow(() -> new InvalidIdException("Game not found"));
        if (game.getPhase().equals(Phase.REINFORCEMENT)) {
            game.setPhase(Phase.ATTACK);
        } else if (game.getPhase().equals(Phase.ATTACK)) {
            cardService.giveCurrentPlayerACard(game);
            // because game was updated in card service it should get the new state otherwise it overwrites old state
            game = gameRepository.findGameByIdWithPlayers(gameId).orElseThrow(() -> new InvalidIdException("Game not found"));
            game.setPhase(Phase.FORTIFICATION);
        } else if (game.getPhase().equals(Phase.FORTIFICATION)) {
            throw new PhaseException("You can't go to the next phase");
        }
        game.setAfkThreshold(LocalDateTime.now().plusSeconds(game.getTimer()));
        saveGame(game);
        return game.getPhase();
    }

    /**
     * attacks a territory of the game
     *
     * @param attackDto the attackDto
     * @return the updated attack information
     */
    @Transactional
    public AttackResponse attack(@Valid AttackDto attackDto) {
        var game = gameRepository.findGameByIdWithPlayers(attackDto.getGameId()).orElseThrow(() -> new InvalidIdException("Game not found"));
        if (Phase.ATTACK != game.getPhase()) throw new PhaseException("Game is not in attack phase");
        Territory attackerTerritory = territoryService.getTerritoryByNameAndGame(attackDto.getAttackerTerritoryName(), attackDto.getGameId());
        Territory defenderTerritory = territoryService.getTerritoryByNameAndGame(attackDto.getDefenderTerritoryName(), attackDto.getGameId());
        if ((attackerTerritory.getTroops() - 1) < attackDto.getAmountOfAttackers())
            throw new InvalidTroopException("Invalid amount of troops");
        if (attackerTerritory.getOwner().getPlayerInGameId() == defenderTerritory.getOwner().getPlayerInGameId())
            throw new AttackException("You can't attack your self");
        //attacker and defender roll dices
        var attackerDices = rollDices(attackDto.getAmountOfAttackers());
        final int amountOfDefenders = territoryService.getMaxAmountOfDefenders(defenderTerritory.getTroops());
        var defenderDices = rollDices(amountOfDefenders);
        //order dices
        attackerDices.sort(Collections.reverseOrder());
        defenderDices.sort(Collections.reverseOrder());
        //compare dices
        TroopsLost troopsLost = territoryService.updateTerritoriesAfterAttack(attackerDices, defenderDices, attackerTerritory, defenderTerritory);
        //check if defender has lost
        territoryService.setPlayersWithNoTerritoriesToLost(attackDto.getGameId());
        //check if player has won
        if (checkIfCurrentPlayerHasWon(attackDto.getGameId()))
            return new AttackResponse(attackDto.getGameId(), attackerDices, defenderDices, true);
        int attackerSurvivedTroops = attackDto.getAmountOfAttackers() - troopsLost.getAmountOfTroopsLostAttacker();
        int defenderSurvivedTroops = amountOfDefenders - troopsLost.getAmountOfTroopsLostDefender();
        game = gameRepository.findGameByIdWithPlayers(attackDto.getGameId()).orElseThrow(() -> new InvalidIdException("Game not found"));
        game.setAfkThreshold(LocalDateTime.now().plusSeconds(game.getTimer()));
        saveGame(game);
        return new AttackResponse(attackerSurvivedTroops, defenderSurvivedTroops, attackDto.getGameId(), attackerDices, defenderDices, false);
    }

    /**
     * rolls dices for attacker or defender
     *
     * @param amountOfDices the amount of dices to roll
     * @return a list of numbers between 1 and 6
     */
    private List<Integer> rollDices(int amountOfDices) {
        List<Integer> dicesRolled = new ArrayList<>();
        for (int i = 0; i < amountOfDices; i++) {
            dicesRolled.add(Dice.roll());
        }
        return dicesRolled;
    }

    /**
     * Moves armies from one territory to another (that you own)
     *
     * @param fortifyDto the dto with the information to do a fortification
     */
    public void fortify(@Valid FortifyDto fortifyDto) {
        var game = gameRepository.findById(fortifyDto.getGameId()).orElseThrow(() -> new InvalidIdException("Game not found"));
        if (game.getPhase() == Phase.REINFORCEMENT)
            throw new PhaseException("Game is not in the fortification or attack phase");
        var territoryFrom = territoryService.getTerritoryByNameAndGameWithNeighborsAndOwner(fortifyDto.getTerritoryFrom(), fortifyDto.getGameId());
        var fortifiableTerritories = fortificationTerritoryFinder.getAllFortifiableTerritories(fortifyDto.getTerritoryFrom(), fortifyDto.getGameId());
        if (!fortifiableTerritories.contains(fortifyDto.getTerritoryTo())) {
            throw new NotAdjacentTerritoryException("Territory is not possible to fortify");
        }
        var territoryTo = territoryService.getTerritoryByNameAndGameWithNeighborsAndOwner(fortifyDto.getTerritoryTo(), fortifyDto.getGameId());
        game.setAfkThreshold(LocalDateTime.now().plusSeconds(game.getTimer()));
        territoryService.fortify(territoryFrom, territoryTo, fortifyDto.getTroops());
        saveGame(game);
    }

    /**
     * Gets the current state of the game
     *
     * @param gameId the id of the game
     * @return the game
     */
    public Game getGameState(long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new InvalidIdException("Game not found"));
        List<PlayerInGame> playersInGameWithCards = playerInGameService.getPlayersInGameWithCards(gameId);
        List<Continent> continents = continentService.getContinentsWithTerritories(gameId);
        List<GameCard> cards = cardService.getGameCards(gameId);
        game.setPlayersInGame(playersInGameWithCards);
        game.setContinents(continents);
        game.setGameCards(cards);
        return game;
    }

    /**
     * Gets a random player of the game that is nog the current player
     *
     * @param gameId the id of the game
     * @return a random player that is not the current player
     */
    public PlayerInGame getRandomPlayerExceptCurrentPlayer(long gameId) {
        var game = gameRepository.findGameByIdWithPlayers(gameId).orElseThrow(() -> new InvalidIdException("Game not found"));
        return playerInGameService.getRandomPlayerExceptCurrentPlayer(game);
    }

    /**
     * calculates the amount of troops a player gets at the beginning of his turn
     *
     * @param gameId the id of the game
     */
    public Game calculateReinforceTroops(long gameId) {
        var game = getGameState(gameId);
        var currentPlayer = playerInGameService.getCurrentPlayerInGame(game);
        var amountOfOwnedTerritories = 0;
        var continentBonusTroops = 0;
        for (var continent : game.getContinents()) {
            var allTerritoriesOfContinentOwnedByPlayer = true;
            for (var territory : continent.getTerritories()) {
                if (territory.getOwner() != null && (territory.getOwner().getPlayerInGameId() == currentPlayer.getPlayerInGameId())) {
                    amountOfOwnedTerritories++;
                } else {
                    allTerritoriesOfContinentOwnedByPlayer = false;
                }
            }
            if (allTerritoriesOfContinentOwnedByPlayer) {
                continentBonusTroops += continent.getBonusTroops();
            }
        }
        var baseTroops = Math.max(amountOfOwnedTerritories / 3, 3);
        currentPlayer.addRemainingTroopsToReinforce(baseTroops + continentBonusTroops);
        game.getPlayersInGame().set(game.getCurrentPlayerIndex(), currentPlayer);
        return saveGame(game);
    }

    /**
     * Gets the history of games of a player
     *
     * @param username the username of the player
     * @return a list of games
     */
    public List<Game> historyOfPlayer(String username) {
        //check if player exists
        playerService.loadUserByUsername(username);
        return gameRepository.findGamesAfterEndDateWithUsername(LocalDateTime.now().minusDays(30), username);
    }

    /*
     *  Sets the end date of the game to the current date
     *
     *  @param gameId the id of the game
     */
    @Transactional
    public void endGame(long gameId) {
        var game = gameRepository.findGameByIdWithPlayers(gameId).orElseThrow(() -> new InvalidIdException("Game not found"));
        game.setEndTime(LocalDateTime.now());
        loyaltyPointService.addLoyaltyPoints(game.getPlayersInGame(), game.getStartTime(), game.getEndTime());
        playerInGameService.updatePlayerStatisticsAfterGame(game.getPlayersInGame());
        saveGame(game);
    }

    /**
     * Checks if the current player has won the game
     *
     * @param gameId the id of the game
     */
    public boolean checkIfCurrentPlayerHasWon(long gameId) {
        var game = gameRepository.findGameByIdWithPlayers(gameId).orElseThrow(() -> new InvalidIdException("Game not found"));
        PlayerInGame playerInGame = playerInGameService.getCurrentPlayerInGame(game);
        PlayerInGame playerWon = continentService.checkIfAllContinentsAreOwnedByOnePlayer(playerInGame, gameId);
        if (playerWon != null) {
            endGame(gameId);
            return true;
        }
        return false;
    }

    /**
     * gets the current game with players and cards
     *
     * @param gameId the id of the game
     * @return game
     */
    private Game getGameWithPlayersAndCards(Long gameId) {
        var game = gameRepository.findGameByIdWithPlayers(gameId)
                .orElseThrow(() -> new InvalidIdException("Game not found"));

        game.setGameCards(cardService.getGameCards(gameId));
        return game;
    }

    /**
     * Handles the card exchange
     *
     * @param exchangeCardsDto the data needed to exchange cards
     */
    public void handleExchangeCards(ExchangeCardsDto exchangeCardsDto) {
        var game = getGameWithPlayersAndCards(exchangeCardsDto.getGameId());
        if (Phase.REINFORCEMENT != game.getPhase()) throw new PhaseException("Game is not in the reinforcement phase");
        var currentPlayerInGame = playerInGameService.getCurrentPlayerInGameWithCards(game);
        game = cardService.exchangeCards(game, currentPlayerInGame, exchangeCardsDto.getCardNames());
        saveGame(game);
    }

    /**
     * Gets active games with usernames from a specific user
     *
     * @param username the username of the user
     */
    public List<Game> getActiveGamesWithUsernamesFromUser(String username) {
        //checks if user exists
        playerService.loadUserByUsername(username);
        return gameRepository.findActiveGamesWithUsernamesFromUser(username);
    }

    public List<Game> getAllActiveGamesWithPlayers() {
        return gameRepository.findAllActiveGamesWithPlayers();
    }

    public int getAmountActiveGames() {
        return gameRepository.countActiveGames();
    }

    public int getAmountFinishedGames() {
        return gameRepository.countFinishedGames();
    }

    /**
     * saves the game
     *
     * @param game the game
     * @return the game
     */
    public Game saveGame(@Valid Game game) {
        return gameRepository.save(game);
    }
}
