package kdg.be.riskbackend.identity.services;

import kdg.be.riskbackend.identity.domain.token.ResetToken;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.dtos.password.PasswordResetRequest;
import kdg.be.riskbackend.identity.exceptions.InvalidTokenException;
import kdg.be.riskbackend.identity.services.email.EmailSender;
import kdg.be.riskbackend.identity.util.EmailBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

/**
 * This class is used to handle the reset tokens.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordService {
    private final PlayerService playerService;
    private final ResetTokenService resetTokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EmailSender emailSender;
    @Value("${ui.url}")
    private String uiUrl;

    /**
     * This method is used to send a reset password email.
     *
     * @param username the email of the player
     * @return the reset token
     */
    public String resetRequest(String username) {
        //send email to user with link to reset password
        Player player = playerService.loadUserByUsername(username);
        String token = resetTokenService.createResetToken(username);
        String link = uiUrl+"/password/reset/" + token;
        try {
            emailSender.send(player.getEmail(), "Password reset", EmailBuilder.buildResetPasswordEmail(player.getUsername(), link));
        } catch (Exception e) {
            log.info("email not sent to " + e.getMessage());
        }
        return token;
    }

    /**
     * This method is used to reset the password of the player.
     *
     * @param passwordResetRequest the username of the player
     */
    public void resetPassword(@Valid PasswordResetRequest passwordResetRequest) {
        ResetToken token = resetTokenService.getToken(passwordResetRequest.getToken()).orElse(null);
        if (token == null) throw new InvalidTokenException("Token not found");
        //reset password
        playerService.loadUserByUsername(passwordResetRequest.getUsername());
        changePassword(passwordResetRequest.getUsername(), passwordResetRequest.getPassword());
    }

    /**
     * This method is used to change the password of the player.
     *
     * @param username the username of the player
     * @param password the new password
     */
    private void changePassword(String username, String password) {
        Player player = playerService.loadUserByUsername(username);
        player.setPassword(bCryptPasswordEncoder.encode(password));
        playerService.save(player);
    }
}
