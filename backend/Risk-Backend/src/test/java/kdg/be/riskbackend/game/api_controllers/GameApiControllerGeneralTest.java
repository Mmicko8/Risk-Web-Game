package kdg.be.riskbackend.game.api_controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.repositories.GameRepository;
import kdg.be.riskbackend.game.services.GameService;
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


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameApiControllerGeneralTest {
    Lobby lobby;
    Game game;
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
    @WithMockUser(username = "currentUser@student.kdg.be")
    void startGameWorksWithRightLobbyId() throws Exception {
        mockMvc.perform(post("/api/game/startGame/lobby/" + lobby.getLobbyId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "currentUser@student.kdg.be")
    void startGameFails() throws Exception {
        mockMvc.perform(post("/api/game/startGame/lobby/0")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }


    @Test
    @WithMockUser(username = "currentUser@student.kdg.be")
    void getGameState() throws Exception {
        mockMvc.perform(get("/api/game/" + game.getGameId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").exists())
                .andExpect(jsonPath("$.continents").exists())
                .andExpect(jsonPath("$.phase").exists())
                .andExpect(jsonPath("$.startTime").exists())
                .andExpect(jsonPath("$.turn").exists())
                .andExpect(jsonPath("$.currentPlayerIndex").exists());
    }

    @Test
    @WithMockUser(username = "currentUser@student.kdg.be")
    void getGameStateFails() throws Exception {
        mockMvc.perform(get("/api/game/" + 0)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "KdgUser1")
    void gameHistoryWorksWithValidUser() throws Exception {
        gameService.endGame(game.getGameId());
        mockMvc.perform(get("/api/game/history")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "Napoleon")
    void gameHistoryGivesNoContentWhenUserHasNoGamesPlayed() throws Exception {
        gameService.endGame(game.getGameId());
        mockMvc.perform(get("/api/game/history")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "ching chong")
    void gameHistoryFailsWhenUserIsUnknown() throws Exception {
        gameService.endGame(game.getGameId());
        mockMvc.perform(get("/api/game/history")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username = "KdgUser1")
    void getActiveGamesFromPlayer() throws Exception {
        mockMvc.perform(get("/api/game/activeOfPlayer")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "ching chong")
    void gettingActiveGamesFailsWithUnknownUser() throws Exception {
        mockMvc.perform(get("/api/game/activeOfPlayer")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}