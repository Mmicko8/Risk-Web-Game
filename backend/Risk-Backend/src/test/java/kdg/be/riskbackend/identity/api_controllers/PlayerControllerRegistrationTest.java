package kdg.be.riskbackend.identity.api_controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdg.be.riskbackend.identity.dtos.registration.RegistrationRequest;
import kdg.be.riskbackend.identity.repositories.ConfirmationTokenRepository;
import kdg.be.riskbackend.identity.repositories.PlayerRepository;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.identity.services.RegistrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlayerControllerRegistrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;


    @Test
    public void postApiCallForRegisteringUserWorks() {
        try {
            var dto = new RegistrationRequest("jefke", "jefke@student.kdg.be", "Password", false);
            MvcResult result = mockMvc.perform(post("/api/player/register")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andReturn();
            var token = result.getResponse().getContentAsString();
            registrationService.confirmToken(token);
            confirmationTokenRepository.deleteAll();
            playerRepository.delete(playerService.loadUserByUsername("jefke"));

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Test
    public void postApiCallForRegisteringUserFailsWithToShortPassword() {
        try {
            var dto = new RegistrationRequest("jefke", "jefke@student.kdg.be", "P", false);
            mockMvc.perform(post("/api/player/register")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    //bad request omdat het password te kort is
                    .andExpect(status().isBadRequest())
                    .andReturn();

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void confirmingAccountWithTokenWorks() {
        try {
            var dto = new RegistrationRequest("jefke", "jefke@student.kdg.be", "Password", false);
            var token=registrationService.register(dto);
            mockMvc.perform(get("/api/player/confirm/" + token)
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is3xxRedirection());
            confirmationTokenRepository.deleteAll();
            playerRepository.delete(playerService.loadUserByUsername("jefke"));

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Test
    public void confirmingAccountWithTokenFailsWithWrongToken() {
        try {
            mockMvc.perform(get("/api/player/confirm/gdlsqfglqsdfgyhubqslodf" )
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}