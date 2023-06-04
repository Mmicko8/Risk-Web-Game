package kdg.be.riskbackend.identity.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * This class is used to create a bean for the PasswordEncoder.
 */
@Configuration
public class PasswordEncoder {
    /**
     * This method is used to create a bean for the PasswordEncoder.
     *
     * @return the PasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
