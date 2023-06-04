package kdg.be.riskbackend.identity.api_controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.dtos.login.LoginRequest;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.identity.util.SsidFinder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlayerControllerGeneralTests {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    SsidFinder ssidFinder;
    @Autowired
    private MockMvc mockMvc;


    @Test
    @WithMockUser(username = "KdgUser1")
    void getRecentlyPlayedWith() {
        try {
            mockMvc.perform(get("/api/player/recentlyPlayedWith")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Test
    @WithMockUser(username = "chingchong")
    void getRecentlyPlayedWithFailsWithWrongUser() {
        try {
            mockMvc.perform(get("/api/player/recentlyPlayedWith")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Test
    @WithMockUser(username = "KdgUser1")
    void logoutWorksWithValidUser() {
        try {
            mockMvc.perform(put("/api/player/logout")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isNoContent());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Test
    @WithMockUser(username = "chingchong")
    void logoutFailsWithInvalidUser() {
        try {
            mockMvc.perform(put("/api/player/logout")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isUnauthorized());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    @Test
    @WithMockUser(username = "KdgUser1")
    void getLocalFriendsWorks() {
        try {
            given(ssidFinder.getSsid()).willReturn("netwerk123");
            //login player 1
            mockMvc.perform(post("/api/player/login")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new LoginRequest("KdgUser1", "password"))))
                    .andDo(print())
                    .andExpect(status().isOk());
            //login player 2
            mockMvc.perform(post("/api/player/login")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new LoginRequest("KdgUser2", "password"))))
                    .andDo(print())
                    .andExpect(status().isOk());
            //player one asks for local friends
            mockMvc.perform(get("/api/player/getLocalPlayers")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(result -> {
                        var content = result.getResponse().getContentAsString();
                        var players = objectMapper.readValue(content, Player[].class);
                        Assertions.assertEquals(1, players.length);
                        Assertions.assertEquals("KdgUser2", players[0].getUsername());
                    });
            playerService.logout("KdgUser1");
            playerService.logout("KdgUser2");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Test
    @WithMockUser(username = "chingchong")
    void gettingLocalPlayersWithUnknownUserFails() {
        try {
            given(ssidFinder.getSsid()).willReturn("netwerk123");
            //login player 1
            mockMvc.perform(post("/api/player/login")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new LoginRequest("KdgUser1", "password"))))
                    .andDo(print())
                    .andExpect(status().isOk());
            //login player 2
            mockMvc.perform(post("/api/player/login")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new LoginRequest("KdgUser2", "password"))))
                    .andDo(print())
                    .andExpect(status().isOk());
            //player one asks for local friends
            mockMvc.perform(get("/api/player/getLocalPlayers")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isNotFound());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
