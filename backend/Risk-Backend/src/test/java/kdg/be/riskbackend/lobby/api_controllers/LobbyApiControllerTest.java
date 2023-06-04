package kdg.be.riskbackend.lobby.api_controllers;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LobbyApiControllerTest {
    Lobby lobby;
    @Autowired
    private LobbyService lobbyService;
    @Autowired

    private LobbyRepository lobbyRepository;
    @Autowired

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        lobby = lobbyService.startLobby(new CreateLobbyDto("KdgUser1", 4, 60));
    }

    @AfterEach
    void cleanup() {
        lobbyRepository.delete(lobby);
    }

    @Test
    @WithMockUser(username = "KdgUser1")
    void gettingLobbyWorks() throws Exception {
        mockMvc.perform(get("/api/lobby/"+lobby.getLobbyId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "KdgUser1")
    void gettingLobbyFailsWithInvalidUser() throws Exception {
        mockMvc.perform(get("/api/lobby/0")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    @WithMockUser(username = "KdgUser1")
    void createLobby() throws Exception {
        mockMvc.perform(post("/api/lobby/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"KdgUser1\",\"maxPlayers\":4,\"timer\":60}"))
                .andDo(print())
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(username = "KdgUser1")
    void createLobbyFailsWithWrongUser() throws Exception {
        mockMvc.perform(post("/api/lobby/create")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"chingchong\",\"maxPlayers\":4,\"timer\":60}"))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "KdgUser1")
    void addAiPlayerWorks() throws Exception {
        mockMvc.perform(put("/api/lobby/" + lobby.getLobbyId() + "/addAi")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
  @Test
    @WithMockUser(username = "KdgUser1")
    void addAiPlayerFailsWithWrongLobbyId() throws Exception {
        mockMvc.perform(put("/api/lobby/0/addAi")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser(username = "KdgUser1")
    void joinLobbyFailsIfUserIsAlreadyInLobby() throws Exception {
        mockMvc.perform(put("/api/lobby/joinLobby/"+lobby.getLobbyId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser(username = "KdgUser2")
    void joinLobbyWorks() throws Exception {
        mockMvc.perform(put("/api/lobby/joinLobby/"+lobby.getLobbyId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }


    @Test
    @WithMockUser(username = "KdgUser1")
    void getOpenLobbies() throws Exception {
        mockMvc.perform(get("/api/lobby/openLobbies/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    } @Test
    @WithMockUser(username = "chingchong")
    void getOpenLobbiesFailsWithWrongUser() throws Exception {
        mockMvc.perform(get("/api/lobby/openLobbies/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "KdgUser1")
    void getJoinedNotStartedLobbiesWorks() throws Exception {
        mockMvc.perform(get("/api/lobby/joinedNotStartedLobbies")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "chingchong")
    void getJoinedNotStartedLobbiesFailsWithWrongUser() throws Exception {
        mockMvc.perform(get("/api/lobby/joinedNotStartedLobbies")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "User")
    void GetAmountOpenLobbies() throws Exception {
        mockMvc.perform(get("/api/lobby/monitoring/amountOpen")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "User")
    void GetAmountClosedLobbies() throws Exception {
        mockMvc.perform(get("/api/lobby/monitoring/amountClosed")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}