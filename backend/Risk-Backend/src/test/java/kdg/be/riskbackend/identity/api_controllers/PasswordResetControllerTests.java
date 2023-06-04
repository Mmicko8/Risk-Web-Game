package kdg.be.riskbackend.identity.api_controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdg.be.riskbackend.identity.dtos.login.LoginRequest;
import kdg.be.riskbackend.identity.dtos.password.PasswordResetRequest;
import kdg.be.riskbackend.identity.services.PasswordService;
import kdg.be.riskbackend.identity.services.email.EmailSender;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PasswordResetControllerTests {
    @Autowired
    private PasswordService passwordService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    EmailSender emailSender;

    @Test
    @WithMockUser(username = "KdgUser1")
    void requestingPasswordResetWorks() {
        try {
            mockMvc.perform(post("/api/password/resetRequest")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @WithMockUser(username = "KdgUser1000")
    void resetPasswordRequestFailsWithNonExistingUser() {
        try {
            mockMvc.perform(post("/api/password/resetRequest")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isConflict());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    @WithMockUser(username = "KdgUser1")
    void resetPasswordWorks() {
        try {
            var token = passwordService.resetRequest("KdgUser1");
            var passwordReset = new PasswordResetRequest("KdgUser1", "newPassword", token);
            mockMvc.perform(put("/api/password/reset")
                    .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(passwordReset)))
                    .andDo(print())
                    .andExpect(status().isNoContent());
            //login works with new password
            mockMvc.perform(post("/api/player/login")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new LoginRequest("KdgUser1", "newPassword"))))
                    .andDo(print())
                    .andExpect(status().isOk());
             token = passwordService.resetRequest("KdgUser1");
             passwordReset = new PasswordResetRequest("KdgUser1", "password", token);
            passwordService.resetPassword(passwordReset);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    } @Test
    @WithMockUser(username = "KdgUser1")
    void resetPasswordFailsWithWrongToken() {
        try {
            var passwordReset = new PasswordResetRequest("KdgUser1", "newPassword", "HISQJDHFLQSJDF");
            mockMvc.perform(put("/api/password/reset")
                    .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(passwordReset)))
                    .andDo(print())
                    .andExpect(status().isNotFound());
            //login works with new password
            mockMvc.perform(post("/api/player/login")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new LoginRequest("KdgUser1", "newPassword"))))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


}
