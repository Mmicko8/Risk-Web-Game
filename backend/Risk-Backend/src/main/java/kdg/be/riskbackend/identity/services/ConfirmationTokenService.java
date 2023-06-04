package kdg.be.riskbackend.identity.services;

import kdg.be.riskbackend.identity.domain.token.ConfirmationToken;
import kdg.be.riskbackend.identity.repositories.ConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * This class is used to handle the confirmation tokens.
 */
@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    /**
     * This method is used to save a confirmation token.
     *
     * @param confirmationToken the confirmation token
     */
    public void createConfirmationToken(@Valid ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }

    /**
     * This method is used to get a confirmation token.
     *
     * @param token the token of the confirmation token
     * @return an optional of the confirmation token
     */
    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    /**
     * This method is used to set confirmedAt to the current date and time.
     *
     * @param token the token of the confirmation token
     */
    public void setConfirmedAt(String token) {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token).orElseThrow(() -> new IllegalStateException("token not found"));
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        createConfirmationToken(confirmationToken);
    }
}
