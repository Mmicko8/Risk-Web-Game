package kdg.be.riskbackend.game.services;

import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.exceptions.GameException;
import kdg.be.riskbackend.game.repositories.GameRepository;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.services.LobbyService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameHistoryTests {
    Lobby lobby;
    Game game;
    @Autowired
    private GameService gameService;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private LobbyService lobbyService;



    @BeforeAll
    public void setup() {
        gameRepository.deleteAll();
        lobby = lobbyService.startLobby(new CreateLobbyDto("KdgUser1", 4, 60));
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser2");
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser3");
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser4");
        lobby = lobbyService.getLobbyByIdWithPlayers(lobby.getLobbyId());
        game = gameService.startGame(lobby);
        game = gameRepository.findLatestGame().orElseThrow(() -> new GameException("No game found"));
        gameService.endGame(game.getGameId());
    }
    @AfterAll
    public void tearDown() {
        gameRepository.delete(game);
    }
    @Test
    void historyOfPlayerGivesNoGameIfDateIsExpired() {
        game.setEndTime(LocalDateTime.now().minusDays(40));
        gameService.saveGame(game);
        var games = gameService.historyOfPlayer("KdgUser1");
        Assertions.assertTrue(games.isEmpty());
    }

    @Test
    void historyOfPlayerWorksIfDateEndedIsNotExpired() {
        game.setEndTime(LocalDateTime.now().minusDays(20));
        gameService.saveGame(game);
        var games = gameService.historyOfPlayer("KdgUser1");
        Assertions.assertEquals(1, games.size());
    }


}
