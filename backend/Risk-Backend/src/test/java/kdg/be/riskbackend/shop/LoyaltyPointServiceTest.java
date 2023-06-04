package kdg.be.riskbackend.shop;

import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.domain.game.PlayerInGame;
import kdg.be.riskbackend.game.exceptions.GameException;
import kdg.be.riskbackend.game.repositories.GameRepository;
import kdg.be.riskbackend.game.services.GameService;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.services.LobbyService;
import kdg.be.riskbackend.shop.service.LoyaltyPointService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoyaltyPointServiceTest {

    Lobby lobby;
    Game game;
    @Autowired
    private LoyaltyPointService loyaltyPointService;
    @Autowired
    private GameService gameService;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private PlayerService playerService;

    @BeforeEach
    public void setup() {
        lobby = lobbyService.startLobby(new CreateLobbyDto("KdgUser1", 4, 60));
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser2");
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser3");
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser4");
        game = gameRepository.findLatestGame()
                .orElseThrow(() -> new GameException("No game found"));
    }

    @Test
    void addLoyaltyPoints() {
        gameService.endGame(game.getGameId());
        game.setEndTime(game.getStartTime().plusMinutes(20));
        game = gameService.saveGame(game);
        var gameWithPlayers = gameRepository.findGameByIdWithPlayers(game.getGameId())
                .orElseThrow(() -> new GameException("No game found"));
        for (PlayerInGame playerInGame : gameWithPlayers.getPlayersInGame()) {
            playerInGame.getPlayer().setLoyaltyPoints(0);
            playerService.save(playerInGame.getPlayer());
        }
        gameWithPlayers = gameRepository.findGameByIdWithPlayers(game.getGameId())
                .orElseThrow(() -> new GameException("No game found"));

        loyaltyPointService.addLoyaltyPoints(gameWithPlayers.getPlayersInGame(),
                gameWithPlayers.getStartTime(), gameWithPlayers.getEndTime());

        gameWithPlayers.getPlayersInGame().forEach(
                player -> Assertions.assertEquals(20, player.getPlayer().getLoyaltyPoints()));
    }

    @AfterEach
    public void tearDown() {
        gameRepository.delete(game);
    }
}