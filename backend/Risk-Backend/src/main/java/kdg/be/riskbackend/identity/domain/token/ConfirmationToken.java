package kdg.be.riskbackend.identity.domain.token;

import kdg.be.riskbackend.identity.domain.user.Player;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * This class is used for creating a token for a user.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity(name = "confirmation_tokens")
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    @NotNull(message = "Token cannot be null")
    private String token;
    @Column(nullable = false)
    @NotNull(message = "Created at cannot be null")
    private LocalDateTime createdAt;
    @Column(nullable = false)
    @NotNull(message = "Expires at cannot be null")
    private LocalDateTime expiresAt;
    private LocalDateTime confirmedAt;
    @ManyToOne(cascade = CascadeType.DETACH)
    @NotNull(message = "Player cannot be null")
    private Player player;

    /**
     * class constructor
     *
     * @param token     the token
     * @param createdAt the time the token was created
     * @param expiresAt the time the token expires
     * @param player    the player
     */
    public ConfirmationToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, Player player) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.player = player;
    }
}
