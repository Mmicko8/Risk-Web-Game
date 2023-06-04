package kdg.be.riskbackend.identity.security.config;

import kdg.be.riskbackend.identity.security.filter.JwtTokenFilter;
import kdg.be.riskbackend.identity.services.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

/**
 * This class is used for configuring the security of the application.
 */
@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final PlayerService playerService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenFilter jwtFilter;

    /**
     * This method is used to configure the authentication manager.
     *
     * @param http the {@link HttpSecurity} to modify
     * @throws Exception if an error occurs
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // Enable CORS and disable CSRF
        http = http.cors().and().csrf().disable();

        // Set session management to stateless
        http = http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and();

        // Set unauthorized requests exception handler
        http = http
                .exceptionHandling()
                .authenticationEntryPoint(
                        (request, response, ex) -> response.sendError(
                                HttpServletResponse.SC_UNAUTHORIZED,
                                ex.getMessage()
                        )
                )
                .and();

        // Set permissions on endpoints
        http.authorizeRequests()
                // Our public endpoints
                .antMatchers(HttpMethod.POST, "/api/player/register").permitAll()
                .antMatchers(HttpMethod.GET, "/api/player/confirm/*").permitAll()
                .antMatchers(HttpMethod.POST, "/api/player/login").permitAll()
                .antMatchers(HttpMethod.GET, "/api/lobby/openLobbies/{amount}").permitAll()
                .antMatchers(HttpMethod.GET, "/api/player/leaderboard").permitAll()
                .antMatchers(HttpMethod.GET, "/api/test/openEndpoint").permitAll()
                .antMatchers(HttpMethod.POST, "/api/password/forgot/{username}").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/password/reset").permitAll()
                .antMatchers(HttpMethod.GET, "/api/game/monitoring/*").permitAll()
                .antMatchers(HttpMethod.GET, "/api/invite/accept/{lobbyId}/recipient/{recipient}/sender/{senderName}").permitAll()
                // Our private endpoints
                .anyRequest().authenticated();

        // Add JWT token filter
        http.addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
        );

    }

    /**
     * Creating bean for the authentication manager.
     *
     * @return the authentication manager
     * @throws Exception if an error occurs
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Configuring the authentication provider.
     *
     * @param auth the {@link AuthenticationManagerBuilder} to use
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    /**
     * This method is used to create a {@link DaoAuthenticationProvider} bean.
     *
     * @return the {@link DaoAuthenticationProvider} bean
     */
    @Bean
    protected DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(playerService);
        return provider;
    }
}
