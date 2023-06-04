package kdg.be.riskbackend.identity.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/*
 * This class is used to find the ssid of the current network.
 */

public class SsidFinder {
    public String getSsid() throws IOException {
        String ssid = "";
        ProcessBuilder builder = new ProcessBuilder(
                "cmd.exe", "/c", "netsh wlan show all");
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while (r.read() != -1) {
            line = r.readLine();
            if (line.contains("SSID") || line.contains("Signal")) {
                if (!line.contains("BSSID"))
                    if (line.contains("SSID") && !line.contains("name") && !line.contains("SSIDs")) {
                        line = line.substring(8);
                        ssid = line;
                    }
            }
        }
        return ssid.replace(" ", "").replace(":", "");
    }
}
