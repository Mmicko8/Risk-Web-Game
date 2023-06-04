package kdg.be.riskbackend.game.services;

import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.domain.game.Phase;
import kdg.be.riskbackend.game.domain.map.Territory;
import kdg.be.riskbackend.game.dtos.card.ExchangeCardsDto;
import kdg.be.riskbackend.game.dtos.phases.AttackDto;
import kdg.be.riskbackend.game.dtos.phases.AttackResponse;
import kdg.be.riskbackend.game.dtos.phases.FortifyDto;
import kdg.be.riskbackend.game.exceptions.InvalidTroopException;
import kdg.be.riskbackend.game.exceptions.NotAdjacentTerritoryException;
import kdg.be.riskbackend.game.exceptions.PhaseException;
import kdg.be.riskbackend.game.repositories.GameRepository;
import kdg.be.riskbackend.game.repositories.NeighborRepository;
import kdg.be.riskbackend.game.repositories.TerritoryRepository;
import kdg.be.riskbackend.identity.domain.user.AppUserRole;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.exceptions.AttackException;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.repositories.LobbyRepository;
import kdg.be.riskbackend.lobby.services.LobbyService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameServiceTest {
    Player player1 = new Player("KdgUser1", "kdgUser1@student.kdg.be", "Password", AppUserRole.USER, true, "coolProfilePic");
    Player player2 = new Player("KdgUser2", "kdgUser2@student.kdg.be", "Password", AppUserRole.USER, true, "coolProfilePic");
    Player player3 = new Player("KdgUser3", "kdgUser3@student.kdg.be", "Password", AppUserRole.USER, true, "coolProfilePic");
    Player player4 = new Player("KdgUser4", "kdgUser4@student.kdg.be", "Password", AppUserRole.USER, true, "coolProfilePic");
    Lobby lobby;
    Game game;
    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private GameService gameService;
    @Autowired
    private CardService cardService;
    @Autowired
    private TerritoryService territoryService;
    @Autowired
    private TerritoryRepository territoryRepository;
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerInGameService playerInGameService;
    @Autowired
    private LobbyRepository lobbyRepository;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private NeighborRepository neighborRepository;

    @BeforeEach
    void startup() {
        //set games played to 0
        Player p1 = playerService.loadUserByUsername("KdgUser1");
        Player p2 = playerService.loadUserByUsername("KdgUser2");
        Player p3 = playerService.loadUserByUsername("KdgUser3");
        Player p4 = playerService.loadUserByUsername("KdgUser4");
        Player[] players = {p1, p2, p3, p4};
        for (Player player : players) {
            player.setGamesPlayed(0);
            playerService.save(player);
        }
        //create game
        lobby = lobbyService.startLobby(new CreateLobbyDto("KdgUser1", 5, 60));
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser2");
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser3");
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser4");
        lobby = lobbyService.getLobbyByIdWithPlayers(lobby.getLobbyId());
        game = gameService.startGame(lobby);
        game = gameService.getGameState(game.getGameId());
    }

    @AfterEach
    void cleanup() {
        gameRepository.delete(game);
        lobbyRepository.delete(lobby);
    }

    @Test
    void startGameWorksAutomatically() {
        Assertions.assertTrue(game.getStartTime().minusSeconds(10).isBefore(LocalDateTime.now()) ||
                game.getStartTime().plusSeconds(10).isAfter(LocalDateTime.now()));
    }

    @Test
    void gettingNextTurnOfGameWorks() {
        //all player turns
        List<String> playerEmail = new ArrayList<>();
        var amountOfNextTurns = 8;
        for (int i = 0; i < amountOfNextTurns; i++) {
            gameService.nextPhase(game.getGameId());
            gameService.nextPhase(game.getGameId());
            playerEmail.add(gameService.getNextTurnPlayerOfGame(game.getGameId()).getPlayer().getEmail());
        }
        //list of all players
        var playerEmails = new String[]{player1.getEmail(), player2.getEmail(), player3.getEmail(), player4.getEmail()};
        //check if all players are in the list
        Assertions.assertTrue(Arrays.asList(playerEmails).contains(playerEmail.get(0)));
        Assertions.assertTrue(Arrays.asList(playerEmails).contains(playerEmail.get(1)));
        Assertions.assertTrue(Arrays.asList(playerEmails).contains(playerEmail.get(2)));
        Assertions.assertTrue(Arrays.asList(playerEmails).contains(playerEmail.get(3)));
        //check if the order is correct
        Assertions.assertEquals(playerEmail.get(0), playerEmail.get(4));
        Assertions.assertEquals(playerEmail.get(1), playerEmail.get(5));
        Assertions.assertEquals(playerEmail.get(2), playerEmail.get(6));
        Assertions.assertEquals(playerEmail.get(3), playerEmail.get(7));
        //turn is updated
        Assertions.assertEquals(game.getTurn() + amountOfNextTurns, gameService.getGameState(game.getGameId()).getTurn());
    }

    @Test
    void gettingNextTurnSkipsOverLostPlayers() {
        //all player turns
        List<String> actualEmails = new ArrayList<>();
        var amountOfNextTurns = 8;
        //set 2 players to lost
        var playerInGame1 = game.getPlayersInGame().get(0);
        var playerInGame2 = game.getPlayersInGame().get(3);
        playerInGame1.setHasLost(true);
        playerInGame2.setHasLost(true);
        playerInGameService.savePlayerInGame(playerInGame1);
        playerInGameService.savePlayerInGame(playerInGame2);
        //add emails to list in order of turns
        for (int i = 0; i < amountOfNextTurns; i++) {
            gameService.nextPhase(game.getGameId());
            gameService.nextPhase(game.getGameId());
            actualEmails.add(gameService.getNextTurnPlayerOfGame(game.getGameId()).getPlayer().getEmail());
        }
        //all players
        var playerEmails = game.getPlayersInGame().stream().map(pig -> pig.getPlayer().getEmail()).toList();
        //check if all players are in the list first one and last one are skipped
        Assertions.assertSame(playerEmails.get(1), actualEmails.get(0));
        Assertions.assertSame(playerEmails.get(2), actualEmails.get(1));
        Assertions.assertSame(playerEmails.get(1), actualEmails.get(2));
        Assertions.assertSame(playerEmails.get(2), actualEmails.get(3));
        //turn is updated
        Assertions.assertEquals(game.getTurn() + amountOfNextTurns, gameService.getGameState(game.getGameId()).getTurn());
    }

    @Test
    void nextPhaseTesting() {
        Assertions.assertEquals(Phase.REINFORCEMENT, game.getPhase());
        Phase phase = gameService.nextPhase(game.getGameId());
        Assertions.assertEquals(Phase.ATTACK, phase);
        phase = gameService.nextPhase(game.getGameId());
        Assertions.assertEquals(Phase.FORTIFICATION, phase);
        Assertions.assertThrows(PhaseException.class, () -> gameService.nextPhase(game.getGameId()));
    }

    @Test
    void movingTroopsDuringFortificationPhaseWorksWithNeighboringCountries() {
        //set game phase to fortification
        game.setPhase(Phase.FORTIFICATION);
        game =  gameService.saveGame(game);
        //get territory
        var territory1 = territoryRepository.findByNameAndGameId("Ural", game.getGameId());
        //set a neighboring country to current owner
        var territoryWithNeighbors = neighborRepository.findNeighborsOfTerritory(territoryRepository.findByNameAndGameId("Ural", game.getGameId()).getTerritoryId());
        var neighborTerritory = territoryRepository.findByNameAndGameId(territoryWithNeighbors.getNeighbors().get(0).getName(), game.getGameId());
        neighborTerritory.setOwner(territory1.getOwner());
        territoryRepository.save(neighborTerritory);
        //set troops
        territory1.setTroops(10);
        territoryRepository.save(territory1);
        neighborTerritory.setTroops(10);
        territoryRepository.save(neighborTerritory);
        //fortify from country 1 to country 2
        gameService.fortify(new FortifyDto(game.getGameId(), territory1.getName(), neighborTerritory.getName(), 5));
        //check if troops are moved
        Assertions.assertEquals(5, territoryService.getTerritoryByNameAndGame(territory1.getName(), game.getGameId()).getTroops());
        Assertions.assertEquals(15, territoryService.getTerritoryByNameAndGame(neighborTerritory.getName(), game.getGameId()).getTroops());
    }

    @Test
    void attackSucceeds() {

        var gameId = game.getGameId();
        var game = gameService.getGameState(gameId);
        //set game to attack
        game.setPhase(Phase.ATTACK);
         gameService.saveGame(game);
        //get player in game
        var playerInGame = playerInGameService.getCurrentPlayerInGame(game);
        //decide the attacking territory
        var attackerTerritory = territoryService.getAllTerritoriesOfGame(gameId).get(0);
        attackerTerritory.setOwner(playerInGame);
        var attackerTerritoryName = attackerTerritory.getName();
        //get a neighboring territory of the attacker
        var defenderTerritoryName = territoryService.getTerritoryNeighborsByTerritoryNameAndGameId(attackerTerritoryName, gameId).get(0).getName();
        var player2 = gameService.getRandomPlayerExceptCurrentPlayer(gameId);
        //set both territories to different players
        var territory1 = territoryService.getTerritoryByNameAndGame(attackerTerritoryName, gameId);
        territory1.setTroops(10);
        territory1.setOwner(playerInGame);
        var territory2 = territoryService.getTerritoryByNameAndGame(defenderTerritoryName, gameId);
        territory2.setTroops(10);
        territory2.setOwner(player2);
        territoryRepository.save(territory1);
        territoryRepository.save(territory2);
        //do the attack
        AttackResponse attackResponse;
        attackResponse = gameService.attack(new AttackDto(gameId, attackerTerritoryName, defenderTerritoryName, 3));
        //checks if the attack is successful
        Assertions.assertEquals(10 - (3 - attackResponse.getAmountOfSurvivingTroopsAttacker()), territoryService.getTerritoryByNameAndGame(attackerTerritoryName, gameId).getTroops());
        Assertions.assertEquals(10 - (2 - attackResponse.getAmountOfSurvivingTroopsDefender()), territoryService.getTerritoryByNameAndGame(defenderTerritoryName, gameId).getTroops());
    }

    /**
     * Tests whether the attacker annexes the territory of the defender if the defending territory loses all of its troops.
     */
    @Test
    void attackAnnexation() {
        // setup board state by selecting random territory and making sure the to be attacked neighbouring territory has a different owner
        // also makes sure that the attacker has a lot more troops than the defender so the chance of the test failing is < 0.001%
        //set game to attack phase
        var gameId = game.getGameId();
        game.setPhase(Phase.ATTACK);
        game =  gameService.saveGame(game);
        //set current player as attacker and get territory for attacker
        var currentPlayerInGame = playerInGameService.getCurrentPlayerInGame(game);
        var attackerTerritory = territoryService.getAllTerritoriesOfGame(gameId).get(0);
        //get find a neighboring territory of the attacker that is not owned by the attacker
        var defenderTerritoryAsNeighbor = territoryService.getTerritoryNeighborsByTerritoryNameAndGameId(attackerTerritory.getName(), gameId).get(0);
        var defenderTerritory = territoryService.getTerritoryByNameAndGame(defenderTerritoryAsNeighbor.getName(), gameId);
        var player2 = gameService.getRandomPlayerExceptCurrentPlayer(gameId);
        //set both territories to different players and give the attacker a lot more troops
        attackerTerritory.setTroops(50);
        attackerTerritory.setOwner(currentPlayerInGame);
        defenderTerritory.setTroops(1);
        defenderTerritory.setOwner(player2);
        territoryRepository.save(attackerTerritory);
        territoryRepository.save(defenderTerritory);

        // keep attacking until defender gets annexed
        AttackResponse attackResponse;
        do {
            attackResponse = gameService.attack(new AttackDto(gameId, attackerTerritory.getName(), defenderTerritory.getName(), 3));
        } while (attackResponse.getAmountOfSurvivingTroopsDefender() > 0);
        var defenderTerritoryUpdated = territoryService.getTerritoryById(defenderTerritory.getTerritoryId());

        Assertions.assertEquals(attackerTerritory.getOwner().getPlayerInGameId(), defenderTerritoryUpdated.getOwner().getPlayerInGameId());
        Assertions.assertEquals(0, attackResponse.getAmountOfSurvivingTroopsDefender());
    }
    @Test
    void attackingYourselfFailsWithAttackException() {
        //set game to attack phase
        var gameId = game.getGameId();
        game.setPhase(Phase.ATTACK);
        game =  gameService.saveGame(game);
        //set current player as attacker and get territory for attacker
        var currentPlayerInGame = playerInGameService.getCurrentPlayerInGame(game);
        var attackerTerritory = territoryService.getAllTerritoriesOfGame(gameId).get(0);
        //get find a neighboring territory of the attacker that is not owned by the attacker
        var defenderTerritoryAsNeighbor = territoryService.getTerritoryNeighborsByTerritoryNameAndGameId(attackerTerritory.getName(), gameId).get(0);
        var defenderTerritory = territoryService.getTerritoryByNameAndGame(defenderTerritoryAsNeighbor.getName(), gameId);
        //set both territories to different players and give the attacker a lot more troops
        attackerTerritory.setTroops(50);
        attackerTerritory.setOwner(currentPlayerInGame);
        defenderTerritory.setTroops(1);
        defenderTerritory.setOwner(currentPlayerInGame);
        territoryRepository.save(attackerTerritory);
        territoryRepository.save(defenderTerritory);
        //check if it fails
        Assertions.assertThrows(AttackException.class,()-> gameService.attack(new AttackDto(gameId, attackerTerritory.getName(), defenderTerritory.getName(), 3)));
    }
    @Test
    void attackingYourselfFailsWithNotEnoughTroops() {
        //set game to attack phase
        var gameId = game.getGameId();
        game.setPhase(Phase.ATTACK);
        game =  gameService.saveGame(game);
        //set current player as attacker and get territory for attacker
        var currentPlayerInGame = playerInGameService.getCurrentPlayerInGame(game);
        var attackerTerritory = territoryService.getAllTerritoriesOfGame(gameId).get(0);
        //get find a neighboring territory of the attacker that is not owned by the attacker
        var defenderTerritoryAsNeighbor = territoryService.getTerritoryNeighborsByTerritoryNameAndGameId(attackerTerritory.getName(), gameId).get(0);
        var defenderTerritory = territoryService.getTerritoryByNameAndGame(defenderTerritoryAsNeighbor.getName(), gameId);
        //set both territories to different players and give the attacker a lot more troops
        attackerTerritory.setTroops(1);
        attackerTerritory.setOwner(currentPlayerInGame);
        defenderTerritory.setTroops(1);
        defenderTerritory.setOwner(currentPlayerInGame);
        territoryRepository.save(attackerTerritory);
        territoryRepository.save(defenderTerritory);
        //check if it fails
        Assertions.assertThrows(InvalidTroopException.class,()-> gameService.attack(new AttackDto(gameId, attackerTerritory.getName(), defenderTerritory.getName(), 3)));
    }

    @Test
    void movingTroopsDuringFortificationPhaseFailsWithToMuchTroops() {
        //set game phase to attack
        game.setPhase(Phase.FORTIFICATION);
        game =  gameService.saveGame(game);
        var gameId = game.getGameId();
        var player = playerInGameService.getCurrentPlayerInGame(game);
        var territory1 = territoryRepository.findByNameAndGameId("Ural", gameId);
        //find a neighboring territory of the first territory
        var territoryWithNeighbors = neighborRepository.findNeighborsOfTerritory(territory1.getTerritoryId());
        var neighborTerritory = territoryRepository.findByNameAndGameId(territoryWithNeighbors.getNeighbors().get(0).getName(), gameId);
        //change owner of the territories and give them troops
        neighborTerritory.setOwner(player);
        neighborTerritory.setTroops(10);
        territory1.setOwner(player);
        territory1.setTroops(10);
        territoryRepository.save(territory1);
        territoryRepository.save(neighborTerritory);
        //assertions
        Assertions.assertThrows(InvalidTroopException.class, () -> gameService.fortify(new FortifyDto(game.getGameId(), territory1.getName(), neighborTerritory.getName(), 15)));
        Assertions.assertThrows(InvalidTroopException.class, () -> gameService.fortify(new FortifyDto(game.getGameId(), territory1.getName(), neighborTerritory.getName(), 10)));
    }

    @Test
    void movingTroopsDuringFortificationPhaseFailsWithNonNeighboringTerritory() {
        //set game phase to attack
        game.setPhase(Phase.FORTIFICATION);
        game =  gameService.saveGame(game);
        var gameId = game.getGameId();
        var player = playerInGameService.getCurrentPlayerInGame(game);
        var territory1 = territoryRepository.findByNameAndGameId("Ural", gameId);
        //find a neighboring territory of the first territory
        var nonNeighborTerritory=territoryService.getAllTerritoriesOfGame(gameId).get(0);
        //change owner of the territories and give them troops
        nonNeighborTerritory.setOwner(player);
        nonNeighborTerritory.setTroops(10);
        territory1.setOwner(player);
        territory1.setTroops(10);
        territoryRepository.save(territory1);
        territoryRepository.save(nonNeighborTerritory);
        //assertions
        Assertions.assertThrows(NotAdjacentTerritoryException.class, () -> gameService.fortify(new FortifyDto(game.getGameId(), territory1.getName(), "Siam", 1)));
        Assertions.assertThrows(NotAdjacentTerritoryException.class, () -> gameService.fortify(new FortifyDto(game.getGameId(), nonNeighborTerritory.getName(), "Mongolia", 1)));
    }

    @Test
    @WithMockUser(username = "currentUser")
    void conqueringATerritoryGivesACardToTheCurrentPlayer() {
        var gameId = game.getGameId();
        var player = playerInGameService.getCurrentPlayerInGameWithCards(game);
        var amountOfCards = player.getPlayerCards().size();
        playerInGameService.setCurrentPlayerConqueredATerritoryInHisTurn(player, true);
        game.setPhase(Phase.ATTACK);
        game.getPlayersInGame().set(game.getCurrentPlayerIndex(), player);
        game =  gameService.saveGame(game);
        gameService.nextPhase(gameId);
        Assertions.assertEquals(amountOfCards + 1, playerInGameService.getCurrentPlayerInGameWithCards(game).getPlayerCards().size());
    }

    @Test
    @WithMockUser(username = "currentUser")
    void losingAllAttacksDoesNotGiveACardToTheCurrentPlayer() {
        var gameId = game.getGameId();
        var player = playerInGameService.getCurrentPlayerInGameWithCards(game);
        var amountOfCards = player.getPlayerCards().size();
        playerInGameService.setCurrentPlayerConqueredATerritoryInHisTurn(player, false);
        game.setPhase(Phase.ATTACK);
        game.getPlayersInGame().set(game.getCurrentPlayerIndex(), player);
        game =  gameService.saveGame(game);
        gameService.nextPhase(gameId);
        Assertions.assertEquals(amountOfCards, playerInGameService.getCurrentPlayerInGameWithCards(game).getPlayerCards().size());
    }

    @Test
    void exchangingCardsWorks() {
        var gameCards = cardService.getGameCards(game.getGameId());
        game.setGameCards(gameCards);
        var gameId = game.getGameId();
        var player = playerInGameService.getCurrentPlayerInGameWithCards(game);
        for (int i = 0; i < 3; i++) {
            cardService.addTopCardToPlayer(game, player);
            //noinspection OptionalGetWithoutIsPresent
            game = gameRepository.findGameWithGameCards(gameId).get();
        }
        player = playerInGameService.getCurrentPlayerInGameWithCards(game);
        var cardNames = player.getPlayerCards().stream().map(card -> card.getCard().getName())
                .collect(Collectors.toCollection(ArrayList::new));
        var stars = player.getPlayerCards().stream().mapToInt(card -> card.getCard().getStars()).sum();
        var amountOfTroops = cardService.calculateTroopsFromStars(stars) + player.getRemainingTroopsToReinforce();

        var exchangeCardsDto = new ExchangeCardsDto(gameId, cardNames);
        gameService.handleExchangeCards(exchangeCardsDto);
        Assertions.assertEquals(amountOfTroops, playerInGameService.getCurrentPlayerInGame(game).getRemainingTroopsToReinforce());
    }

    @Test
    void statisticsWhenStartingGameGetSaved() {
        Game gameToCheck = gameService.getGameState(game.getGameId());
        Assertions.assertEquals(1, gameToCheck.getPlayersInGame().get(0).getPlayer().getGamesPlayed());
        Assertions.assertEquals(1, gameToCheck.getPlayersInGame().get(1).getPlayer().getGamesPlayed());
        Assertions.assertEquals(1, gameToCheck.getPlayersInGame().get(2).getPlayer().getGamesPlayed());
        Assertions.assertEquals(1, gameToCheck.getPlayersInGame().get(3).getPlayer().getGamesPlayed());
    }

    @Test
    void ifPlayerPlayerGetsWipedOutAttackingPlayerGetsHisCards() {
        //set game phase to attack
        game.setPhase(Phase.ATTACK);
        game =  gameService.saveGame(game);
        game = gameService.getGameState(game.getGameId());
        //set all territories to territories of the first player
        territoryService.getAllTerritoriesOfGameWithNeighbors(game.getGameId()).forEach(territory -> {
            territory.setTroops(10);
            territory.setOwner(game.getPlayersInGame().get(0));
            territoryService.save(territory);
        });
        //sets the first territory to be owned by the first player
        Territory territory1 = territoryService.getAllTerritoriesOfGameWithNeighbors(game.getGameId()).get(0);
        territory1.setTroops(1);
        territory1.setOwner(game.getPlayersInGame().get(1));
        //sets the second territory to be owned by the second player
        Territory territory2 = territoryService.getAllTerritoriesOfGameWithNeighbors(game.getGameId()).get(18);
        territory2.setOwner(game.getPlayersInGame().get(2));
        territory1 = territoryService.save(territory1);
        territory1 = territoryService.getTerritoryByNameAndGameWithNeighborsAndOwner(territory1.getName(), game.getGameId());
        //set cards
        cardService.addTopCardToPlayer(game, game.getPlayersInGame().get(0));
        cardService.addTopCardToPlayer(game, game.getPlayersInGame().get(0));
        cardService.addTopCardToPlayer(game, game.getPlayersInGame().get(1));
        cardService.addTopCardToPlayer(game, game.getPlayersInGame().get(1));
        //do action
        var attackTerritory = territory1.getNeighbors().get(0).getName();
        while (territory1.getTroops() > 0) {
            gameService.attack(new AttackDto(game.getGameId(), attackTerritory, territory1.getName(), 3));
            territory1 = territoryService.getTerritoryById(territory1.getTerritoryId());
        }

        game = gameService.getGameState(game.getGameId());
        Assertions.assertTrue(game.getPlayersInGame().get(1).isHasLost());
        Assertions.assertEquals(4, game.getPlayersInGame().get(0).getPlayerCards().size());
        Assertions.assertEquals(0, game.getPlayersInGame().get(1).getPlayerCards().size());
    }
}