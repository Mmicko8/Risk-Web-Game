package kdg.be.riskbackend.identity.security.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class is used to create a bean for the JwtTokenBean.
 */
@Configuration
public class JwtTokenBean {
    /**
     * This method is used to create a bean for the JwtTokenBean.
     *
     * @return the JwtTokenBean
     */
    @Bean
    public JwtTokenUtil JwtTokenUtil() {
        return new JwtTokenUtil();
    }
}
