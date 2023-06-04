package kdg.be.riskbackend.identity.api_controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.dtos.PlayerDto;
import kdg.be.riskbackend.identity.repositories.PlayerRepository;
import kdg.be.riskbackend.identity.services.PlayerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlayerControllerEditTest {
    Player player;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private PlayerService playerService;

    @Test
    @WithMockUser(username = "blablabla@student.kdg.be")
    void editPlayer() {
        try {
            player = playerRepository.findAll().get(0);
            var dto = new PlayerDto(player.getPlayerId(), "jefke", "jefke@student.kdg.be", "coolPicture");
            mockMvc.perform(put("/api/player/edit")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isNoContent())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @Test
    @WithMockUser(username = "blablabla@student.kdg.be")
    void editPlayerWillFailWithUnknownId() {
        try {
            var dto = new PlayerDto(0, "jefke", "jefke@student.kdg.be", "coolPicture");
            mockMvc.perform(put("/api/player/edit")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    @AfterAll
    public void cleanUp() {
        playerService.editPlayer(new PlayerDto(player.getPlayerId(), player.getUsername(), player.getEmail(), "coolPicture"));
    }
}