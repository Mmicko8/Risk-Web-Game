package kdg.be.riskbackend.identity.services.email;

/**
 * This class is used to email the user.
 */
public interface EmailSender {
    /**
     * This method is used to email the user.
     *
     * @param to the email address of the user
     */
    void send(String to, String subject, String body);
}
