package uk.ac.bsfc.sbp.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.data.SBConfig;
import uk.ac.bsfc.sbp.utils.time.SBTime;
import uk.ac.bsfc.sbp.utils.time.SBTimeFormat;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class SBLogger {
    private static final boolean bukkitLogging = SBConfig.getBoolean("bukkit-logging");
    private static final boolean timeStamps = SBConfig.getBoolean("log-timestamp");
    private static final SBTimeFormat timeFormat = SBTimeFormat.of(SBConfig.getString("log-timestamp-format"));

    private static final Plugin plugin = Main.getInstance();
    private static final PrintStream printStream = new PrintStream(new FileOutputStream(FileDescriptor.out));

    public static void newLine() {
        if (bukkitLogging) plugin.getLogger().info("");
        else new PrintStream(new FileOutputStream(FileDescriptor.out)).println();
    }
    public static void raw(String s) {
        String timeStamp = "";
        if (timeStamps) {
            timeStamp = "<gray>[<i>" + SBTime.format(timeFormat) +"<gray>]";
        }
        Bukkit.getConsoleSender().sendMessage(SBColourUtils.format(s));

    }
    public static void info(String message) {
        SBLogger.raw("<gray>[INFO] " + message);
    }
    public static void warn(String message) {
        SBLogger.raw("<yellow>[WARN] " + message);
    }
    public static void err(String message) {
        SBLogger.raw("<red>[ERR] " + message);
    }
}