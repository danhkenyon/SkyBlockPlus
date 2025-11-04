package uk.ac.bsfc.sbp.utils;

import org.bukkit.plugin.Plugin;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.data.SBConfig;
import uk.ac.bsfc.sbp.utils.time.SBTimeFormat;
import uk.ac.bsfc.sbp.utils.time.SBTime;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

import static uk.ac.bsfc.sbp.utils.SBConstants.PLUGIN_TITLE;

public class SBLogger {
    private static final boolean bukkitLogging = SBConfig.getBoolean("bukkit-logging");
    private static final boolean timeStamps = SBConfig.getBoolean("log-timestamp");
    private static final SBTimeFormat timeFormat = SBTimeFormat.of(SBConfig.getString("log-timestamp-format"));

    private static final Plugin plugin = Main.getInstance();
    private static final PrintStream printStream = new PrintStream(new FileOutputStream(FileDescriptor.out));

    public static void newLine() {
        if (bukkitLogging) plugin.getLogger().info("");
        else SBLogger.printStream.println();
    }
    public static void raw(String s) {
        String timeStamp = "";
        if (timeStamps) {
            timeStamp = SBTime.format(timeFormat);
        }

        if (bukkitLogging) {
            plugin.getLogger().info(SBColourUtils.format(s));
        } else {
            SBLogger.printStream.println(SBColourUtils.ansi("&f[&7&o"+timeStamp+"&f] ["+PLUGIN_TITLE+"] "+s));
        }
    }
    public static void info(String message) {
        SBLogger.raw("&f[INFO] " + message);
    }
    public static void warn(String message) {
        SBLogger.raw("&e[WARN] " + message);
    }
    public static void err(String message) {
        SBLogger.raw("&c[ERR] " + message);
    }
}