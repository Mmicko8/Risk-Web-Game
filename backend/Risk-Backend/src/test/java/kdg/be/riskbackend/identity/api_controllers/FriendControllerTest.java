package kdg.be.riskbackend.identity.api_controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdg.be.riskbackend.identity.dtos.registration.RegistrationRequest;
import kdg.be.riskbackend.identity.repositories.ConfirmationTokenRepository;
import kdg.be.riskbackend.identity.services.FriendService;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.identity.services.RegistrationService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FriendControllerTest {

    @Autowired
    private PlayerService playerService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FriendService friendService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        var token = registrationService.register(new RegistrationRequest("pieter", "pieter@gmail.com", "Password", false));
        registrationService.confirmToken(token);
        token = registrationService.register(new RegistrationRequest("marieke", "marieke@gmail.com", "Password", false));
        registrationService.confirmToken(token);

    }

    @AfterEach
    void cleanup() {
        confirmationTokenRepository.deleteAll();
        playerService.deletePlayerByUsername("pieter");
        playerService.deletePlayerByUsername("marieke");

    }

    @Test
    @WithMockUser(username = "pieter")
    void sendInviteWorks() {
        try {
            mockMvc.perform(put("/api/friend/send/marieke")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());
            var friendRequests = friendService.getFriendRequestsOfPlayer("marieke");
            Assertions.assertEquals(1, friendRequests.size());
            Assertions.assertEquals("pieter", friendRequests.get(0).getUsername());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Test
    @WithMockUser(username = "pieter")
    void sendInviteFailsWhenUserDoesNotExist() {
        try {
            mockMvc.perform(put("/api/friend/send/chingChong")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @WithMockUser(username = "marieke")
    void declineInviteWorks() {
        try {
            friendService.sendFriendRequest("pieter", "marieke");
            mockMvc.perform(put("/api/friend/decline/pieter")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());
            var friendRequests = friendService.getFriendRequestsOfPlayer("marieke");
            Assertions.assertEquals(0, friendRequests.size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Test
    @WithMockUser(username = "marieke")
    void declineInviteFailsIfUsernameDidntSendFriendRequest() {
        try {
            friendService.sendFriendRequest("pieter", "marieke");
            mockMvc.perform(put("/api/friend/decline/KdgUser3")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Test
    @WithMockUser(username = "marieke")
    void checkIfGetFriendRequestsWorks() {
        try {
            friendService.sendFriendRequest("pieter", "marieke");
            mockMvc.perform(get("/api/friend/requests")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Test
    @WithMockUser(username = "hans")
    void checkIfGetFriendRequestsFailsIfUserIsUnknown() {
        try {
            mockMvc.perform(get("/api/friend/requests")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @WithMockUser(username = "marieke")
    void acceptInviteWorks() {
        try {
            friendService.sendFriendRequest("pieter", "marieke");
            mockMvc.perform(put("/api/friend/accept/pieter")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());
            var friendRequests = friendService.getFriendRequestsOfPlayer("marieke");
            Assertions.assertEquals(0, friendRequests.size());
            var friendsUser1 = friendService.getFriends("pieter");
            var friendsUser2 = friendService.getFriends("marieke");
            Assertions.assertTrue(friendsUser1.stream().anyMatch(f -> f.getUsername().equals("marieke")));
            Assertions.assertTrue(friendsUser2.stream().anyMatch(f -> f.getUsername().equals("pieter")));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Test
    @WithMockUser(username = "pieter")
    void acceptInviteFails() {
        try {
            friendService.sendFriendRequest("pieter", "marieke");
            mockMvc.perform(put("/api/friend/accept/janneke")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @WithMockUser(username = "pieter")
    void removingFriendWorks() {
        try {
            friendService.sendFriendRequest("pieter", "marieke");
            friendService.acceptFriendRequest("marieke", "pieter");
            mockMvc.perform(delete("/api/friend/remove/marieke")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNoContent());
            var friendsUser1 = friendService.getFriends("pieter");
            var friendsUser2 = friendService.getFriends("marieke");
            Assertions.assertEquals(0, friendsUser1.size());
            Assertions.assertEquals(0, friendsUser2.size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Test
    @WithMockUser(username = "pieter")
    void removingFriendFailsWhenFriendDoesNotExist() {
        try {
            mockMvc.perform(delete("/api/friend/remove/ChingChong")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @WithMockUser(username = "pieter")
    void getAllFriends() {
        try {
            friendService.sendFriendRequest("pieter", "marieke");
            friendService.acceptFriendRequest("marieke", "pieter");
            mockMvc.perform(get("/api/friend")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON.toString())))
                    .andExpect(jsonPath("$.[0].username", equalTo("marieke")))
                    .andExpect(jsonPath("$.[0].email", equalTo("marieke@gmail.com")));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Test
    @WithMockUser(username = "chingchong")
    void getAllFriendsFailsWithWrongPlayer() {
        try {
            mockMvc.perform(get("/api/friend")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print()).andExpect(status().isNotFound());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


}