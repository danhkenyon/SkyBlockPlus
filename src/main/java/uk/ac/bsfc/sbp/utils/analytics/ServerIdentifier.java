package uk.ac.bsfc.sbp.utils.analytics;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.UUID;

public class ServerIdentifier {

     /*
     * Although this gets a lot of server info, it uses it to generate an anonymous but consistent server fingerprint.
     * will be used as the primary key inside of a database to store other info which is not defined yet, likely average tps, memory usage and such.
     *
     * essentially I want to be able to send non-duplicating anonymous data.
     */

    public static UUID SERVER_ID;

    public static UUID getServerID() {
        if (SERVER_ID == null) SERVER_ID = generate();
        return SERVER_ID;
    }


    public static UUID generate() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            addString(digest, System.getProperty("os.name"));
            addString(digest, System.getProperty("os.arch"));
            addString(digest, System.getProperty("os.version"));

            try {
                String cpu = ManagementFactory.getOperatingSystemMXBean().getName();
                addString(digest, cpu);
            } catch (Exception ignored) {}

            for (NetworkInterface nic : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                try {
                    byte[] mac = nic.getHardwareAddress();
                    if (mac != null) digest.update(mac);
                } catch (Exception ignored) {}
            }

            try {
                String machineId = Files.readString(Path.of("/etc/machine-id")).trim();
                addString(digest, machineId);
            } catch (Exception ignored) {}

            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                try {
                    ProcessBuilder pb = new ProcessBuilder("wmic", "diskdrive", "get", "SerialNumber");
                    Process p = pb.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        addString(digest, line.trim());
                    }
                } catch (Exception ignored) {}
            }

            byte[] hash = digest.digest();

            long msb = 0;
            long lsb = 0;
            for (int i = 0; i < 8; i++)  msb = (msb << 8) | (hash[i] & 0xff);
            for (int i = 8; i < 16; i++) lsb = (lsb << 8) | (hash[i] & 0xff);

            return new UUID(msb, lsb);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void addString(MessageDigest d, String s) {
        if (s != null) d.update(s.getBytes());
    }
}

