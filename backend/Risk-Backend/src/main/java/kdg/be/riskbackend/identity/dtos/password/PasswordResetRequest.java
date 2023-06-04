package kdg.be.riskbackend.identity.dtos.password;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {
    @NotNull(message = "username must be specified")
    private String username;
    @NotNull(message = "password must be specified")
    @Length(min = 6, max = 50, message = "password must be between 6 and 50 characters")
    @Size(min = 1, message = "password must contain at least 1 uppercase character")
    private String password;
    @NotNull(message = "token must be specified")
    private String token;
}
