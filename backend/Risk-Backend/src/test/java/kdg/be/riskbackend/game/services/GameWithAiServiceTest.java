package kdg.be.riskbackend.game.services;

import kdg.be.riskbackend.identity.domain.user.AppUserRole;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.services.LobbyService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameWithAiServiceTest {
    Player player1 = new Player("KdgUser1", "kdgUser1@student.kdg.be", "Password", AppUserRole.USER, true, "coolProfilePic");
    Lobby lobby;

    @Autowired

    private LobbyService lobbyService;


    @BeforeAll
    public void setup() {
        lobby = lobbyService.startLobby(new CreateLobbyDto(player1.getUsername(), 4, 60));
        lobbyService.addRandomAiPlayer(lobby.getLobbyId());
        lobbyService.addRandomAiPlayer(lobby.getLobbyId());
        lobbyService.addRandomAiPlayer(lobby.getLobbyId());
    }


    @Test
    public void testGetGameWithAiPlayers() {
        /*
        var phase = gameService.nextPhase(game.getGameId());
        Assertions.assertEquals(Phase.ATTACK, phase);
        phase = gameService.nextPhase(game.getGameId());
        Assertions.assertEquals(Phase.FORTIFICATION, phase);
        //naar volgende speler
        gameService.getNextTurnPlayerOfGame(game.getGameId());
        var playerInGame = gameService.getCurrentPlayerOfGame(game.getGameId());
        List<String> playerNames = new ArrayList<>(List.of("Napoleon",
                "Julius Caesar", "Sun Tsu", "Shaka", "Stalin", "Kitler", "The Boss Baby"));
        Assertions.assertTrue(playerNames.contains(playerInGame.getPlayer().getUsername()));
   */
    }


}
