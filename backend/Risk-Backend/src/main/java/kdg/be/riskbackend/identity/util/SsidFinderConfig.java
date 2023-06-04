package kdg.be.riskbackend.identity.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SsidFinderConfig {
    /**
     * This method is used to create a bean for the SsidFinder.
     *
     * @return the SsidFinder
     */
    @Bean
    public SsidFinder ssidFinder() {
        return new SsidFinder();
    }
}
