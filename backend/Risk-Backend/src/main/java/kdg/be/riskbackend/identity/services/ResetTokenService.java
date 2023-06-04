package kdg.be.riskbackend.identity.services;

import kdg.be.riskbackend.identity.domain.token.ResetToken;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.repositories.ResetTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class ResetTokenService {
    private final ResetTokenRepository resetTokenRepository;
    private final PlayerService playerService;

    /**
     * This method is used to get a confirmation token.
     *
     * @param token the token of the confirmation token
     * @return the confirmation token
     */
    public Optional<ResetToken> getToken(String token) {
        return resetTokenRepository.findByToken(token);
    }

    /**
     * This method is used to create a reset token.
     *
     * @param username the username of the player
     * @return the reset token
     */
    public String createResetToken(String username) {
        Player player = playerService.loadUserByUsername(username);
        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                player
        );
        resetTokenRepository.save(resetToken);
        return token;
    }
}
