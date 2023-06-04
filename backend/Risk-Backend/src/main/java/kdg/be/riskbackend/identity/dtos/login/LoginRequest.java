package kdg.be.riskbackend.identity.dtos.login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * receives the username and password from the user
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotNull(message = "username must be specified")
    private String username;
    @NotNull(message = "password must be specified")
    @Length(min = 6, max = 50, message = "password must be between 6 and 50 characters")
    private String password;
}
