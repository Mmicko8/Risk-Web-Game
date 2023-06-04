package kdg.be.riskbackend.identity.dtos.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * sends the email, username and password to the user
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerView {
    private String username;
    private String email;
}
