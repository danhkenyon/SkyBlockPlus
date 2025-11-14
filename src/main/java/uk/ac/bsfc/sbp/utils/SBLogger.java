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

/**
 * Utility class for centralized logging functionality within the application.
 * The SBLogger class simplifies integrating logging with both a Bukkit plugin and
 * a direct console output while optionally supporting timestamped messages.
 *
 * This class provides methods for logging messages of varying levels of severity
 * (INFO, WARN, and ERR), along with raw message logging. It supports optional
 * time-stamped logs, which can be formatted based on a user-defined pattern.
 *
 * Key Features:
 * - Integration with Bukkit logging system when enabled via configuration.
 * - Supports direct console logging when Bukkit logging is disabled.
 * - Optional inclusion of timestamps in log messages, configured from a central config.
 * - Logging levels for informational, warning, and error messages.
 * - Direct raw log output without log level prefixes for custom messages.
 *
 * Configuration Dependencies:
 * - Configurable via SBConfig, pulling settings for enabling/disabling Bukkit logging
 *   and for timestamp usage and format.
 * - Uses the following keys from the configuration:
 *   - "bukkit-logging": Enables/disables logging to Bukkit's logger system.
 *   - "log-timestamp": Enables/disables timestamp inclusion in log messages.
 *   - "log-timestamp-format": Specifies the format pattern for timestamps.
 *
 * Thread-Safety:
 * This class operates with static methods and relies on configurations and
 * other statically initialized components. Ensure proper thread-safety in
 * dependent components (e.g., SBConfig) to avoid concurrency issues.
 */
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