package kdg.be.riskbackend.lobby.services;

import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.repositories.LobbyRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LobbyServiceTest {
    Lobby lobby;
    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private LobbyRepository lobbyRepository;

    @BeforeEach
    void setup() {
        lobbyRepository.deleteAll();
        lobby = lobbyService.startLobby(new CreateLobbyDto("KdgUser1", 3, 60));
        lobbyService.addRandomAiPlayer(lobby.getLobbyId());
        lobbyService.addRandomAiPlayer(lobby.getLobbyId());
        lobbyService.createLobby(lobby);
    }

    @AfterEach
    void tearDown() {
        lobbyRepository.delete(lobby);
    }

    @Test
    void addRandomAiPlayer() {
        lobby = lobbyService.getLobbyByIdWithPlayers(lobby.getLobbyId());
        Assertions.assertEquals(3, lobby.getPlayers().size());
        Assertions.assertEquals(2, lobby.getPlayers().stream().filter(Player::isAi).count());
        List<String> playerNames = new ArrayList<>(List.of("Napoleon",
                "Julius Caesar", "Sun Tzu", "Shaka", "Stalin", "Kitler", "The Boss Baby"));
        Assertions.assertEquals(2, lobby.getPlayers().stream().filter(p -> playerNames.contains(p.getUsername())).count());
    }


    @Test
    void getOpenLobbies() {
        var lobbies = lobbyService.getOpenLobbies(1);
        Assertions.assertEquals(1, lobbies.size());
        Assertions.assertEquals(lobby.getLobbyId(), lobbies.get(0).getLobbyId());
    }

    @Test
    void getLobbiesOfPlayer() {
        var lobbies = lobbyService.getLobbiesOfPlayer("KdgUser1");
        Assertions.assertEquals(1, lobbies.size());
        Assertions.assertEquals(lobby.getLobbyId(), lobbies.get(0).getLobbyId());
    }
}