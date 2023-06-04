package kdg.be.riskbackend.achievements.services;

import kdg.be.riskbackend.achievements.Exceptions.AchievementException;
import kdg.be.riskbackend.achievements.domain.Achievement;
import kdg.be.riskbackend.achievements.repositories.AchievementRepository;
import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.domain.game.Phase;
import kdg.be.riskbackend.game.domain.game.PlayerInGame;
import kdg.be.riskbackend.game.repositories.GameRepository;
import kdg.be.riskbackend.game.services.GameService;
import kdg.be.riskbackend.game.services.PlayerInGameService;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.repositories.LobbyRepository;
import kdg.be.riskbackend.lobby.services.LobbyService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AchievementServiceTest {
    Achievement achievement1 = new Achievement("First win", "Win your first game", 10);
    Lobby lobby;
    Game game;
    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private GameService gameService;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private PlayerInGameService playerInGameService;
    @Autowired
    private LobbyRepository lobbyRepository;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private AchievementRepository achievementRepository;
    @Autowired
    private AchievementService achievementService;


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
        lobbyService.addRandomAiPlayer(lobby.getLobbyId());
        lobby = lobbyService.getLobbyByIdWithPlayers(lobby.getLobbyId());
        game = gameService.startGame(lobby);
        game = gameService.getGameState(game.getGameId());
        achievementRepository.save(achievement1);
    }

    @AfterEach
    void cleanup() {
        gameRepository.delete(game);
        lobbyRepository.delete(lobby);
        achievementRepository.delete(achievement1);
    }

    @Test
    void WinningYourFirstGameGivesYouABadge() {
        var playerInGame = playerInGameService.getCurrentPlayerInGame(game);
        var player = playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId());
        var amountOfAchievements = player.getAchievements().size();
        player.setGamesWon(0);
        player.setGamesWonAgainstAi(1);
        player = playerService.save(player);
        playerInGame.setWinner(true);
        playerInGame.setPlayer(player);
        playerInGame = playerInGameService.savePlayerInGame(playerInGame);
        game.getPlayersInGame().set(game.getCurrentPlayerIndex(), playerInGame);
        gameService.saveGame(game);

        game = gameService.getGameState(game.getGameId());
        playerInGameService.updatePlayerStatisticsAfterGame(game.getPlayersInGame());
        player = playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId());
        Assertions.assertEquals(amountOfAchievements + 1, player.getAchievements().size());
    }

    @Test
    void WinningYourFifthGameGivesYouABadge() {
        var playerInGame = playerInGameService.getCurrentPlayerInGame(game);
        var player = playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId());
        var amountOfAchievements = player.getAchievements().size();
        player.setGamesWon(4);
        player.setGamesWonAgainstAi(1);
        player = playerService.save(player);
        playerInGame.setWinner(true);
        playerInGame.setPlayer(player);
        playerInGame = playerInGameService.savePlayerInGame(playerInGame);
        game.getPlayersInGame().set(game.getCurrentPlayerIndex(), playerInGame);
        gameService.saveGame(game);
        game = gameService.getGameState(game.getGameId());
        playerInGameService.updatePlayerStatisticsAfterGame(game.getPlayersInGame());
        Assertions.assertEquals(amountOfAchievements + 1, playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId()).getAchievements().size());
    }

    @Test
    void WinningYourTenthGameGivesYouABadge() {
        var playerInGame = playerInGameService.getCurrentPlayerInGame(game);
        var player = playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId());
        var amountOfAchievements = player.getAchievements().size();
        player.setGamesWon(9);
        player.setGamesWonAgainstAi(1);
        player = playerService.save(player);
        playerInGame.setWinner(true);
        playerInGame.setPlayer(player);
        playerInGame = playerInGameService.savePlayerInGame(playerInGame);
        game.getPlayersInGame().set(game.getCurrentPlayerIndex(), playerInGame);
        gameService.saveGame(game);
        game = gameService.getGameState(game.getGameId());
        playerInGameService.updatePlayerStatisticsAfterGame(game.getPlayersInGame());
        Assertions.assertEquals(amountOfAchievements + 1, playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId()).getAchievements().size());
    }

    @Test
    void WinningYourFifteenthGameGivesYouABadge() {
        var playerInGame = playerInGameService.getCurrentPlayerInGame(game);
        var player = playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId());
        var amountOfAchievements = player.getAchievements().size();
        player.setGamesWon(49);
        player.setGamesWonAgainstAi(1);
        player = playerService.save(player);
        playerInGame.setWinner(true);
        playerInGame.setPlayer(player);
        playerInGame = playerInGameService.savePlayerInGame(playerInGame);
        game.getPlayersInGame().set(game.getCurrentPlayerIndex(), playerInGame);
        gameService.saveGame(game);
        game = gameService.getGameState(game.getGameId());
        playerInGameService.updatePlayerStatisticsAfterGame(game.getPlayersInGame());
        Assertions.assertEquals(amountOfAchievements + 1, playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId()).getAchievements().size());
    }

    @Test
    void LosingYourFifteenthGameGivesYouABadge() {
        var playerInGame = playerInGameService.getCurrentPlayerInGame(game);
        var player = playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId());
        var amountOfAchievements = player.getAchievements().size();
        player.setGamesLost(49);
        player = playerService.save(player);
        playerInGame.setWinner(false);
        playerInGame.setPlayer(player);
        playerInGame = playerInGameService.savePlayerInGame(playerInGame);
        game.getPlayersInGame().set(game.getCurrentPlayerIndex(), playerInGame);
        gameService.saveGame(game);
        game = gameService.getGameState(game.getGameId());
        playerInGameService.updatePlayerStatisticsAfterGame(game.getPlayersInGame());
        Assertions.assertEquals(amountOfAchievements + 1, playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId()).getAchievements().size());
    }

    @Test
    void WinningYourFirstGameAgainstAnAiGivesYouABadge() {
        PlayerInGame playerInGame = playerInGameService.getCurrentPlayerInGame(game);
        while (playerInGame.getPlayer().isAi()) {
            //skip ai
            game.setPhase(Phase.FORTIFICATION);
            game = gameService.saveGame(game);
            gameService.getNextTurnPlayerOfGame(game.getGameId());
            //get the updated game state
            game = gameService.getGameState(game.getGameId());
            //get current player
            playerInGame = playerInGameService.getCurrentPlayerInGame(game);
        }

        var player = playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId());
        var amountOfAchievements = player.getAchievements().size();
        player.setGamesWon(1);
        player.setGamesWonAgainstAi(0);
        player = playerService.save(player);
        playerInGame.setWinner(true);
        playerInGame.setPlayer(player);
        playerInGame = playerInGameService.savePlayerInGame(playerInGame);
        game.getPlayersInGame().set(game.getCurrentPlayerIndex(), playerInGame);
        gameService.saveGame(game);
        game = gameService.getGameState(game.getGameId());
        playerInGameService.updatePlayerStatisticsAfterGame(game.getPlayersInGame());
        Assertions.assertEquals(amountOfAchievements + 1, playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId()).getAchievements().size());
    }

    @Test
    void WinningYourFifthGameAgainstAnAiGivesYouABadge() {
        PlayerInGame playerInGame = playerInGameService.getCurrentPlayerInGame(game);
        while (playerInGame.getPlayer().isAi()) {
            //skip ai
            game.setPhase(Phase.FORTIFICATION);
            game = gameService.saveGame(game);
            gameService.getNextTurnPlayerOfGame(game.getGameId());
            //get the updated game state
            game = gameService.getGameState(game.getGameId());
            //get current player
            playerInGame = playerInGameService.getCurrentPlayerInGame(game);
        }

        var player = playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId());
        var amountOfAchievements = player.getAchievements().size();
        player.setGamesWon(1);
        player.setGamesWonAgainstAi(4);
        player = playerService.save(player);
        playerInGame.setWinner(true);
        playerInGame.setPlayer(player);
        playerInGame = playerInGameService.savePlayerInGame(playerInGame);
        game.getPlayersInGame().set(game.getCurrentPlayerIndex(), playerInGame);
        gameService.saveGame(game);
        game = gameService.getGameState(game.getGameId());
        playerInGameService.updatePlayerStatisticsAfterGame(game.getPlayersInGame());
        Assertions.assertEquals(amountOfAchievements + 1, playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId()).getAchievements().size());
    }

    @Test
    void WinningYourTenthGameAgainstAnAiGivesYouABadge() {
        PlayerInGame playerInGame = playerInGameService.getCurrentPlayerInGame(game);
        while (playerInGame.getPlayer().isAi()) {
            //skip ai
            game.setPhase(Phase.FORTIFICATION);
            game = gameService.saveGame(game);
            gameService.getNextTurnPlayerOfGame(game.getGameId());
            //get the updated game state
            game = gameService.getGameState(game.getGameId());
            //get current player
            playerInGame = playerInGameService.getCurrentPlayerInGame(game);
        }

        var player = playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId());
        var amountOfAchievements = player.getAchievements().size();
        player.setGamesWon(1);
        player.setGamesWonAgainstAi(9);
        player = playerService.save(player);
        playerInGame.setWinner(true);
        playerInGame.setPlayer(player);
        playerInGame = playerInGameService.savePlayerInGame(playerInGame);
        game.getPlayersInGame().set(game.getCurrentPlayerIndex(), playerInGame);
        gameService.saveGame(game);
        game = gameService.getGameState(game.getGameId());
        playerInGameService.updatePlayerStatisticsAfterGame(game.getPlayersInGame());
        Assertions.assertEquals(amountOfAchievements + 1, playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId()).getAchievements().size());
    }

    @Test
    void WinningYourFifteenthGameAgainstAnAiGivesYouABadge() {
        PlayerInGame playerInGame = playerInGameService.getCurrentPlayerInGame(game);
        while (playerInGame.getPlayer().isAi()) {
            //skip ai
            game.setPhase(Phase.FORTIFICATION);
            game = gameService.saveGame(game);
            gameService.getNextTurnPlayerOfGame(game.getGameId());
            //get the updated game state
            game = gameService.getGameState(game.getGameId());
            //get current player
            playerInGame = playerInGameService.getCurrentPlayerInGame(game);
        }

        var player = playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId());
        var amountOfAchievements = player.getAchievements().size();
        player.setGamesWon(1);
        player.setGamesWonAgainstAi(49);
        player = playerService.save(player);
        playerInGame.setWinner(true);
        playerInGame.setPlayer(player);
        playerInGame = playerInGameService.savePlayerInGame(playerInGame);
        game.getPlayersInGame().set(game.getCurrentPlayerIndex(), playerInGame);
        gameService.saveGame(game);
        game = gameService.getGameState(game.getGameId());
        playerInGameService.updatePlayerStatisticsAfterGame(game.getPlayersInGame());
        Assertions.assertEquals(amountOfAchievements + 1, playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId()).getAchievements().size());
    }

    @Test
    void testIfAchievementDoesNotExist() {
        Assertions.assertThrows(AchievementException.class, () -> achievementService.getAchievementById(0));
    }
}