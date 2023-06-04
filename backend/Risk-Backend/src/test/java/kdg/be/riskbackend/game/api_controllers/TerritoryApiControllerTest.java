package kdg.be.riskbackend.game.api_controllers;

import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.repositories.GameRepository;
import kdg.be.riskbackend.game.repositories.TerritoryRepository;
import kdg.be.riskbackend.game.services.GameService;
import kdg.be.riskbackend.game.services.TerritoryService;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.repositories.LobbyRepository;
import kdg.be.riskbackend.lobby.services.LobbyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TerritoryApiControllerTest {
    @Autowired
    TerritoryRepository territoryRepository;
    @Autowired
    GameRepository gameRepository;
    @Autowired
    GameService gameService;
    @Autowired
    TerritoryService territoryService;
    Lobby lobby;
    Game game;
    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private LobbyRepository lobbyRepository;
    @Autowired
    private MockMvc mockMvc;

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
    public void clean() {
        lobbyRepository.delete(lobby);
    }

    @Test
    @WithMockUser(username = "blablabla@student.kdg.be")
    void placeTroops() throws Exception {
        var territory = territoryService.getRandomTerritory(game.getGameId());
        var amountOfTroops = 2;
        territoryService.setOwnersRemainingTroopsToReinforce(territory.getTerritoryId(), amountOfTroops);
        mockMvc.perform(put("/api/territory/" + territory.getTerritoryId() + "/placeTroops/" + amountOfTroops)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "blablabla@student.kdg.be")
    void placeTroopsFailsWithInvalidTerritoryId() throws Exception {
        var territory = territoryService.getRandomTerritory(game.getGameId());
        var amountOfTroops = 2;
        territoryService.setOwnersRemainingTroopsToReinforce(territory.getTerritoryId(), amountOfTroops);
        mockMvc.perform(put("/api/territory/0/placeTroops/" + amountOfTroops)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username = "blablabla@student.kdg.be")
    void getAllTerritoriesWithNeighbors() throws Exception {
        mockMvc.perform(get("/api/territory/game/" + game.getGameId() + "/neighbors")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "blablabla@student.kdg.be")
    void getAllTerritoriesWithNeighborsFailsWithInvalidGame() throws Exception {
        mockMvc.perform(get("/api/territory/game/0/neighbors")
                        .accept
                                (MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


}
