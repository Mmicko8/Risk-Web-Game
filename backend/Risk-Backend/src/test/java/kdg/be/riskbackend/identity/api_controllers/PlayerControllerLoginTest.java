package kdg.be.riskbackend.identity.api_controllers;

import kdg.be.riskbackend.identity.domain.user.AppUserRole;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.dtos.registration.RegistrationRequest;
import kdg.be.riskbackend.identity.repositories.ConfirmationTokenRepository;
import kdg.be.riskbackend.identity.repositories.PlayerRepository;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.identity.services.RegistrationService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlayerControllerLoginTest {
    Player player = new Player("KdgUser", "kdgUser@student.kdg.be", "Password", AppUserRole.USER, true, "coolProfilePic");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private RegistrationService registrationService;

    @BeforeAll
    public void setup() {
        String token = registrationService.registerWithoutEmail(new RegistrationRequest(player.getUsername(), player.getEmail(), player.getPassword(), player.isAi()));
        registrationService.confirmToken(token);
    }

    @Test
    void loginWorksWithRightPassword() {
        try {
            mockMvc.perform(post("/api/player/login")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(
                                    """
                                            {     "username": "KdgUser",
                                                "password": "Password"
                                            }"""))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username", equalTo("KdgUser")))
                    .andExpect(jsonPath("$.email", equalTo("kdgUser@student.kdg.be")))
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void loginFailsWithWrongPassword() {
        try {
            mockMvc.perform(post("/api/player/login")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(
                                    """
                                            {     "email": "kdgUser@student.kdg.be",
                                                "password": "Password"
                                            }"""))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    void blockUrlWhenNotAuthorized() {
        try {
            mockMvc.perform(get("/api/test/test")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @WithMockUser(username = "kdgUser@student.kdg.be")
    void allowUrlWhenSomeoneIsLoggedIn() {
        try {
            mockMvc.perform(get("/api/test/test")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @WithMockUser(username = "kdgUser@student.kdg.be")
    void addFriendWorks() {
        try {
            mockMvc.perform(get("/api/test/test")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterAll
    public void tearDown() {
        confirmationTokenRepository.deleteAll();
        playerRepository.delete(playerService.loadUserByUsername("KdgUser"));
    }
}

