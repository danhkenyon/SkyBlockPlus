package uk.ac.bsfc.sbp.utils.data;

import org.jetbrains.annotations.NotNull;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.SBLogger;

import java.io.File;
import java.io.IOException;

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

            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Failed to create file: " + file.getAbsolutePath());
            }
            SBLogger.warn("File not found. creating new file: " + file.getAbsolutePath());

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