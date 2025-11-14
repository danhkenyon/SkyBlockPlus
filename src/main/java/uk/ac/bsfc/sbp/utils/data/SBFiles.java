package uk.ac.bsfc.sbp.utils.data;

import org.jetbrains.annotations.NotNull;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.SBLogger;

import java.io.File;
import java.io.IOException;

/**
 * The SBFiles class provides utility methods for managing file creation and access
 * within the plugin's data folder. It ensures that required directories and files
 * are created if they do not exist, logging warnings as necessary.
 *
 * This class supports resolving specific file types and guarantees that all files
 * returned are ready to use.
 *
 * Thread safety and concurrent access to the file system should be considered when
 * using this class, as it does not natively implement synchronization mechanisms.
 *
 * Key features of this class include:
 * - Automatic directory structure creation when needed.
 * - Transparent file creation if the specified file does not exist.
 * - Logging warnings when a requested file is not found, followed by creating a new file.
 *
 * Methods in this class may throw runtime exceptions if file access or creation fails.
 */
public class SBFiles {

    @NotNull
    public static File get(String name) {
        File file = new File(Main.getInstance().getDataFolder(), name);
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                if (!parent.mkdirs()) {
                    throw new IOException("Failed to create directories for " + file.getAbsolutePath());
                }
            }

            if (!file.exists()) {
                SBLogger.warn("File not found. creating new file: " + file.getAbsolutePath());

                if (!file.createNewFile()) {
                    throw new IOException("Failed to create file: " + file.getAbsolutePath());
                }
            }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to access file: " + file.getAbsolutePath(), e);
        }
    }

    @NotNull
    public static File get(String name, String type) {
        return SBFiles.get(name + "." + type);
    }
}