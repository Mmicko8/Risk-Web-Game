package kdg.be.riskbackend.game.api_controllers;

import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.repositories.GameRepository;
import kdg.be.riskbackend.game.services.GameService;
import kdg.be.riskbackend.game.services.TerritoryService;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.repositories.LobbyRepository;
import kdg.be.riskbackend.lobby.services.LobbyService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class NeighborApiControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired

    private LobbyService lobbyService;
    @Autowired

    private LobbyRepository lobbyRepository;

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameService gameService;
    @Autowired
    private TerritoryService territoryService;

    Lobby lobby;
    Game game;

    @BeforeEach
    public void setup() {
        lobby = lobbyService.startLobby(new CreateLobbyDto("KdgUser1", 5, 60));
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser2");
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser3");
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser4");
        lobby = lobbyService.getLobbyByIdWithPlayers(lobby.getLobbyId());
        game = gameService.startGame(lobby);
    }

    @AfterEach
    void cleanup() {
        gameRepository.delete(game);
        lobbyRepository.delete(lobby);
    }

    @Test
    @WithMockUser(username = "KdgUser1")
    void getAllNeighborsOfTerritory() throws Exception {
        var territories = territoryService.getAllTerritoriesOfGame(game.getGameId());
        mockMvc.perform(get("/api/neighbour/territory/"+territories.get(0).getTerritoryId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "KdgUser1")
    void getAllNeighborsOfTerritoryFailsWhenGivenWrongId() throws Exception {
        mockMvc.perform(get("/api/neighbour/territory/0")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}