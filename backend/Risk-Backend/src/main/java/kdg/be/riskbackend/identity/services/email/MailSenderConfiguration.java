package kdg.be.riskbackend.identity.services.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * This class is used to create a bean for the MailSenderConfiguration.
 */
@Configuration
public class MailSenderConfiguration {
    @Value("${spring.mail.port}")
    int port;
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.username}")
    private String username = "";
    @Value("${spring.mail.password}")
    private String password;

    /**
     * This method is used to create a bean for the MailSenderConfiguration.
     *
     * @return the MailSenderConfiguration
     */
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        // set true for debugging
        props.put("mail.debug", "false");
        props.put("mail.smtp.user", username);
        props.put("mail.smtp.password", password);

        return mailSender;
    }
}