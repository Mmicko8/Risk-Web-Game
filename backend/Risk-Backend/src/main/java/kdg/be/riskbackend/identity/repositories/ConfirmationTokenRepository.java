package kdg.be.riskbackend.identity.repositories;

import kdg.be.riskbackend.identity.domain.token.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * This interface is used to communicate with the database for confirmation tokens.
 */
@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    /**
     * finds a confirmation token by token
     *
     * @param token the token of the confirmation token
     * @return the confirmation token
     */
    Optional<ConfirmationToken> findByToken(String token);
}
