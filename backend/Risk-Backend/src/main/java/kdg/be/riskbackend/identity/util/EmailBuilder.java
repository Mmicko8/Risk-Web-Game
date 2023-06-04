package kdg.be.riskbackend.identity.util;

/**
 * This class is used to build an email.
 */
public class EmailBuilder {
    /**
     * This method is used to build an email.
     *
     * @param name the name of the user
     * @param link the link to the confirmation page
     * @return html of the email
     */
    public static String buildConfirmAccountEmail(String name, String link) {
        return "<html><body><h1>Welcome " + name + "</h1><p>Click the link to activate your account: " +
                "<a href=\"" + link + "\">Activate my account</a></p></body></html>";
    }

    /**
     * This method is used to build an email reset.
     *
     * @param name the name of the user
     * @param link the link to the reset password page
     * @return html of the email
     */
    public static String buildResetPasswordEmail(String name, String link) {
        return "<html><body><h1>Password reset</h1>" +
                "<p><strong>Someone requested a password reset for the account with username: "+name+"</strong></p>" +
                "<p>To reset your password, visit the following address: <a href=\"" + link + "\">Click here to reset password</a></p>" +
                "<p>If you did not request this change, you can ignore this email.<p/></body></html>";
    }

    public static String buildLobbyInviteEmail(String recipientName, String senderName, String link) {
        return "<html><body><h1>Game on "+recipientName+"</h1>" +
                "<h2>"+senderName+" has invited you to join his risk lobby!</h2>" +
                "<p>To accept the invitation, please click the following link: <a href=\"" + link + "\">Accept invitation</a></p>" +
                "<p><strong>Please do note that you have to have an account and must be signed in, otherwise" +
                " the link will redirect you to the register page from which you can register or sign in. After that" +
                " you can click the link in this email again to accept the invite</strong></p>" +
                "<p>If you do not wish to join the lobby, you can ignore this email!</p></body></html>";
    }
}
