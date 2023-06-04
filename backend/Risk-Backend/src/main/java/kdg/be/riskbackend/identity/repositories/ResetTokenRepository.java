package kdg.be.riskbackend.identity.repositories;

import kdg.be.riskbackend.identity.domain.token.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
    /**
     * finds a reset token by token
     *
     * @param token the token of the reset token
     * @return the reset token
     */
    Optional<ResetToken> findByToken(String token);
}
