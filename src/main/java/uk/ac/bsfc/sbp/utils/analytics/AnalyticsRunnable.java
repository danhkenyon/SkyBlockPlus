package uk.ac.bsfc.sbp.utils.analytics;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.SBLogger;

import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class AnalyticsRunnable extends BukkitRunnable {

    private final long intervalTicks;

    public AnalyticsRunnable(long intervalTicks) {
        this.intervalTicks = intervalTicks;
    }

    @Override
    public void run() {
        if (Main.analyticsOptIn == null || !Main.analyticsOptIn) return;

        int avgPlayerCount = Bukkit.getServer().getOnlinePlayers().size();

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        double pluginRamMb = heapUsage.getUsed() / 1024.0 / 1024.0;


        double pluginCpuPercent = 0;
        try {
            com.sun.management.OperatingSystemMXBean osBean =
                    (com.sun.management.OperatingSystemMXBean) java.lang.management.ManagementFactory.getOperatingSystemMXBean();
            pluginCpuPercent = osBean.getProcessCpuLoad() * 100.0;
        } catch (Exception ignored) {
        }

        double avgTps = Bukkit.getServer().getTPS()[0];

        String pluginVersion = Main.getInstance().getDescription().getVersion();
        String serverSoftware = Bukkit.getServer().getVersion();

        AnalyticData data = new AnalyticData(
                avgPlayerCount,
                pluginRamMb,
                pluginCpuPercent,
                avgTps,
                pluginVersion,
                serverSoftware
        );

        sendAnalytics(data);
    }


    private void sendAnalytics(AnalyticData data) {
        SBLogger.raw("<green>[<aqua>Analytics<green>] Sending data: " + data.getServerId() + " players: " + data.getAvgPlayerCount());
        try {
            URL url = new URI("https://analytics.laykon.uk/analytics").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            String json = String.format(
                    "{\"serverId\":\"%s\",\"timestamp\":%d,\"avgPlayerCount\":%d,\"pluginRamMb\":%.2f,\"pluginCpuPercent\":%.2f,\"avgTps\":%.2f,\"pluginVersion\":\"%s\",\"serverSoftware\":\"%s\"}",
                    data.getServerId(),
                    data.getTimestamp(),
                    data.getAvgPlayerCount(),
                    data.getPluginRamMb(),
                    data.getPluginCpuPercent(),
                    data.getAvgTps(),
                    data.getPluginVersion(),
                    data.getServerSoftware()
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
                os.flush();
            }

            conn.getResponseCode();
            conn.disconnect();
        } catch (Exception e) {
            SBLogger.raw("<red>[<aqua>Analytics<red>] Failed to send data: " + e.getMessage());
        }
    }

    public void start() {
        this.runTaskTimerAsynchronously(Main.getInstance(), 0L, intervalTicks);
    }

}
