package kdg.be.riskbackend.lobby.api_controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdg.be.riskbackend.lobby.domain.Invite;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.dtos.InviteWithEmail;
import kdg.be.riskbackend.lobby.dtos.InviteWithUsername;
import kdg.be.riskbackend.lobby.repositories.InviteRepository;
import kdg.be.riskbackend.lobby.repositories.LobbyRepository;
import kdg.be.riskbackend.lobby.services.InviteService;
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
class InviteApiControllerTest {
    @Autowired
    MockMvc mockMvc;
    Lobby lobby;
    Invite invite;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private LobbyRepository lobbyRepository;
    @Autowired
    private InviteRepository inviteRepository;
    @Autowired
    private InviteService inviteService;

    @BeforeEach
    void setUp() {
        lobby = lobbyService.startLobby(new CreateLobbyDto("KdgUser1", 3, 60));
        invite = inviteService.createInviteByUsername("KdgUser2", lobby.getLobbyId(), "KdgUser1");
    }

    @AfterEach
    void tearDown() {
        inviteRepository.deleteAll();
        lobbyRepository.delete(lobby);
    }

    @Test
    @WithMockUser(username = "KdgUser1")
    void createInviteByUsername() throws Exception {
        mockMvc.perform(post("/api/invite/emailInviteWithUsername")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new InviteWithUsername("KdgUser3", lobby.getLobbyId()))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
    }
    @Test
    @WithMockUser(username = "KdgUser1")
    void createInviteByUsernameFailsWithWrongUsername() throws Exception {
        mockMvc.perform(post("/api/invite/emailInviteWithUsername")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new InviteWithUsername("chingchong", lobby.getLobbyId()))))
                .andDo(print())
                .andExpect(status().isConflict())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "KdgUser1")
    void createInviteByEmail() throws Exception {
        mockMvc.perform(post("/api/invite/emailInviteWithEmail")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new InviteWithEmail("kdgUser3@student.kdg.be", lobby.getLobbyId()))))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @WithMockUser(username = "KdgUser2")
    void declineInvite() throws Exception {
        mockMvc.perform(delete("/api/invite/" + invite.getId() + "/decline")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
    @Test
    @WithMockUser(username = "KdgUser2")
    void declineInviteFailsWithWrongInviteId() throws Exception {
        mockMvc.perform(delete("/api/invite/0/decline")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    @WithMockUser(username = "KdgUser3")
    void getAllInvitesOfUser() throws Exception {
        mockMvc.perform(get("/api/invite/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @WithMockUser(username = "ching chong")
    void getAllInvitesOfUserFailsWithInvalidUser() throws Exception {
        mockMvc.perform(get("/api/invite/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


}