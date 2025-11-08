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
        else new PrintStream(new FileOutputStream(FileDescriptor.out)).println();
    }
    public static void raw(String s) {
        String timeStamp = "";
        if (timeStamps) {
            timeStamp = "&7[&7&o" + SBTime.format(timeFormat) +"&7]";
        }

        if (bukkitLogging) {
            plugin.getLogger().info(SBColourUtils.format(s));
        } else {
            //TODO: fix once we got formatting up again
            new PrintStream(new FileOutputStream(FileDescriptor.out)).println(/*SBColourUtils.ansi(*/timeStamp+" ["+PLUGIN_TITLE+"] "+s)/*)*/;
        }
    }
    public static void info(String message) {
        SBLogger.raw("&7[INFO] " + message);
    }
    public static void warn(String message) {
        SBLogger.raw("&e[WARN] " + message);
    }
    public static void err(String message) {
        SBLogger.raw("&c[ERR] " + message);
    }
}