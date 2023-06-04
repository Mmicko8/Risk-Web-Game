package kdg.be.riskbackend.lobby.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailCheck {
    public static boolean checkIfValidEmail(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(email);
        return mat.matches();
    }
}
