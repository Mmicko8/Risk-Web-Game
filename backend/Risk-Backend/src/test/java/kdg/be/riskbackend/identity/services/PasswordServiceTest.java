package kdg.be.riskbackend.identity.services;

import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.dtos.password.PasswordResetRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PasswordServiceTest {
    @Autowired
    private PasswordService passwordService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Test
    void checkIfPasswordActuallyChanges() {
        String token = passwordService.resetRequest("KdgUser1");
        Authentication authenticate = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                "KdgUser1", "password"
                        )
                );

        Player player = (Player) authenticate.getPrincipal();
        Assertions.assertEquals("KdgUser1", player.getUsername());
        passwordService.resetPassword(new PasswordResetRequest("KdgUser1", "Password2", token));
        authenticate = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                "KdgUser1", "Password2"
                        )
                );

        player = (Player) authenticate.getPrincipal();
        Assertions.assertEquals("KdgUser1", player.getUsername());

    }

    @Test
    void testIfOldPasswordDoesntWorkAnymore() {
        //mock send email
        passwordService.resetRequest("KdgUser1");
        Assertions.assertThrows(BadCredentialsException.class, () -> authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                "KdgUser1", "Password"
                        )
                ));
    }

    @AfterAll
    void tearDown() {
        String token = passwordService.resetRequest("KdgUser1");
        passwordService.resetPassword(new PasswordResetRequest("KdgUser1", "Password", token));

    }
}