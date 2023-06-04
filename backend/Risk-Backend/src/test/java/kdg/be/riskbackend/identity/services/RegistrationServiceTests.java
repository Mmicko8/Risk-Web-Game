package kdg.be.riskbackend.identity.services;

import kdg.be.riskbackend.identity.domain.token.ConfirmationToken;
import kdg.be.riskbackend.identity.domain.user.AppUserRole;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.dtos.registration.RegistrationRequest;
import kdg.be.riskbackend.identity.repositories.ConfirmationTokenRepository;
import kdg.be.riskbackend.identity.repositories.PlayerRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RegistrationServiceTests {
    String token = "";
    Player testPlayer = new Player("kdgPlayer", "kdg@student.kdg.be", "Password", AppUserRole.USER, false);
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private PlayerService playerService;

    @Test
    void testRegistrationSetsUserInactiveInTheDatabase() {
        //doing an action
        token = registrationService.registerWithoutEmail(new RegistrationRequest("kdgPlayer", "kdg@student.kdg.be", "Password", false));
        //getting the data
        Player player = playerRepository.findByEmail("kdg@student.kdg.be").orElse(null);
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token).orElse(null);
        //asserting player
        Assertions.assertNotNull(player);
        assertThat(player).usingRecursiveComparison()
                .ignoringFields("playerId", "password", "friends","friendRequests", "shopItems", "achievements","aiDifficulty")
                .isEqualTo(testPlayer);
        //asserting token
        Assertions.assertNotNull(confirmationToken);
        assertThat(confirmationToken).usingRecursiveComparison()
                .ignoringFields("id", "token")
                .isEqualTo(confirmationToken);
    }

    @Test
    void testRegistrationWorks() {
        //doing an action
        registrationService.confirmToken(token);
        ConfirmationToken confirmationToken = new ConfirmationToken("token", LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), testPlayer);
        //getting data
        Player player = playerRepository.findByEmail("kdg@student.kdg.be").orElse(null);
        testPlayer.setEnabled(true);
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        //asserting
        assertThat(player).usingRecursiveComparison()
                .ignoringFields("playerId", "password", "friends","friendRequests", "shopItems", "achievements","aiDifficulty")
                .isEqualTo(testPlayer);
        //asserting token
        Assertions.assertNotNull(confirmationToken);
        assertThat(confirmationToken).usingRecursiveComparison()
                .ignoringFields("id", "token")
                .isEqualTo(confirmationToken);

    }

    @AfterAll
    void cleanUp() {
        confirmationTokenRepository.deleteAll();
        playerRepository.delete(playerService.loadUserByUsername(testPlayer.getUsername()));
    }
}