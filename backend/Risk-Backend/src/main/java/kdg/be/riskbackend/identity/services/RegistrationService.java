package kdg.be.riskbackend.identity.services;

import kdg.be.riskbackend.identity.domain.token.ConfirmationToken;
import kdg.be.riskbackend.identity.domain.user.AppUserRole;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.dtos.registration.RegistrationRequest;
import kdg.be.riskbackend.identity.services.email.EmailSender;
import kdg.be.riskbackend.identity.util.EmailBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;

/**
 * This class is used to handle the registration of a new user.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RegistrationService {
    private final PlayerService playerService;
    private final EmailSender emailSender;
    private final ConfirmationTokenService confirmationTokenService;
    @Value("${backend.url}")
    private String backendUrl;

    /**
     * This method is used to register a new user.
     *
     * @param request the registration request
     * @return token
     */
    @Transactional
    public String register(@Valid RegistrationRequest request) {
        String token = playerService.singUpPlayer(new Player(request.getUsername(), request.getEmail(), request.getPassword(), AppUserRole.USER, request.isAi(),"default"));
        String link = backendUrl+"/api/player/confirm/" + token;
        try {
            emailSender.send(request.getEmail(), "confirm email", EmailBuilder.buildConfirmAccountEmail(request.getUsername(), link));
        } catch (Exception e) {
            log.info("email not sent to " + e.getMessage());
        }
        return token;
    }

    /**
     * This method is used to confirm the registration of a new user.
     *
     * @param token the token
     */
    @Transactional
    public void confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token).orElseThrow(() -> new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }
        confirmationTokenService.setConfirmedAt(token);
        playerService.enableAppUser(confirmationToken.getPlayer().getEmail());
    }

    /**
     * registers a player without an email
     *
     * @param request the registration request
     * @return token
     */
    public String registerWithoutEmail(@Valid RegistrationRequest request) {
        var player = new Player(request.getUsername(), request.getEmail(), request.getPassword(), AppUserRole.USER, request.isAi());
        return playerService.singUpPlayer(player);
    }
}
