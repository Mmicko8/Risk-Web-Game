package kdg.be.riskbackend.identity.dtos.registration;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * receives the email, username and password from the user
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegistrationRequest {
    @Length(min = 2, max = 15, message = "username must be between 2 and 15 characters")
    @NotNull
    String username;
    @Email String email;

    @Length(min = 6, max = 50, message = "password must be between 6 and 50 characters")
    @NotNull
    String password;
    boolean isAi;
}
