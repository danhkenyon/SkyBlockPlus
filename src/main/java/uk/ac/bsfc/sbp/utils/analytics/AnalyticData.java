package uk.ac.bsfc.sbp.utils.analytics;

public class AnalyticData {

    private final String serverId;
    private final long timestamp;

    private final int avgPlayerCount;
    private final double pluginRamMb;
    private final double pluginCpuPercent;
    private final double avgTps;

    private final String pluginVersion;
    private final String serverSoftware;

    public AnalyticData(
            int avgPlayerCount,
            double pluginRamMb,
            double pluginCpuPercent,
            double avgTps,
            String pluginVersion,
            String serverSoftware
    ) {
        this.serverId = ServerIdentifier.getServerID().toString();
        this.timestamp = System.currentTimeMillis();
        this.avgPlayerCount = avgPlayerCount;
        this.pluginRamMb = pluginRamMb;
        this.pluginCpuPercent = pluginCpuPercent;
        this.avgTps = avgTps;
        this.pluginVersion = pluginVersion;
        this.serverSoftware = serverSoftware;
    }

    public String getServerId() {
        return serverId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getAvgPlayerCount() {
        return avgPlayerCount;
    }

    public double getPluginRamMb() {
        return pluginRamMb;
    }

    public double getPluginCpuPercent() {
        return pluginCpuPercent;
    }

    public double getAvgTps() {
        return avgTps;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public String getServerSoftware() {
        return serverSoftware;
    }
}
