package uk.ac.bsfc.sbp.utils.data;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import uk.ac.bsfc.sbp.utils.SBConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A singleton configuration manager for storing, retrieving, and manipulating YAML-based
 * configuration files. The {@code SBConfig} class provides a thread-safe and centralized
 * way to handle application configuration by utilizing a key-value structure. Nested
 * keys (dot-delimited paths) are supported to allow hierarchical data organization.
 *
 * Usage of this class ensures configuration persistence by reading from and writing to
 * a specified YAML file, with automatic file management and creation if it doesn't exist.
 *
 * Main responsibilities:
 * - Reading configuration values.
 * - Writing and modifying configuration values.
 * - Managing nested structures using dot-separated keys.
 * - Persisting changes to a YAML file on disk.
 *
 * Thread Safety:
 * This class uses a singleton pattern which guarantees only one instance of the
 * configuration manager is active during the application's lifecycle.
 */
public class SBConfig {
    private final Yaml yaml;
    private final File configFile;
    private Map<String, Object> data;

    private SBConfig(File file) {
        try {
            this.configFile = file;

            File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IOException("Failed to create directory: " + parent.getAbsolutePath());
            }

            DumperOptions options = new DumperOptions();
            options.setIndent(2);
            options.setPrettyFlow(true);
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            this.yaml = new Yaml(options);
            if (!file.exists()) {
                data = new LinkedHashMap<>();
                save();
            } else {
                try (FileInputStream in = new FileInputStream(file)) {
                    data = yaml.load(in);
                    if (data == null) data = new LinkedHashMap<>();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("[ERR] Error loading file: " + file, e);
        }
    }
    private SBConfig() {
        this(SBFiles.get(SBConstants.CONFIG_FILE));
    }

    private static SBConfig mainInstance;
    private static SBConfig getInstance() {
        if (mainInstance == null) {
            mainInstance = new SBConfig(SBFiles.get(SBConstants.Configuration.CONFIG_FILE_NAME));
        }
        return mainInstance;
    }

    private static SBConfig configInstance;
    public static SBConfig getMessageConfig() {
        if (configInstance == null) {
            configInstance = new SBConfig(SBFiles.get(SBConstants.Configuration.MESSAGES_CONFIG_FILE_NAME));
        }
        return configInstance;
    }

    private void reloadData() {
        try (FileInputStream in = new FileInputStream(configFile)) {
            data = yaml.load(in);
            if (data == null) data = new LinkedHashMap<>();
        } catch (Exception e) {
            throw new RuntimeException("Failed to reload config", e);
        }
    }
    private void save() {
        try (FileWriter writer = new FileWriter(configFile)) {
            yaml.dump(data, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save config file", e);
        }
    }

    // ------------------- Public API ------------------- //

    public static void reload() {
        SBConfig.getInstance().reloadData();
    }
    public static void set(String key, Object value) {
        SBConfig cfg = SBConfig.getInstance();
        SBConfig.setNestedValue(cfg.data, key, value);
        cfg.save();
    }

    public static Object get(String key) {
        SBConfig cfg = SBConfig.getInstance();
        Object value = SBConfig.getNestedValue(cfg.data, key);

        if (value == null) {
            set(key, null);
            return null;
        }
        return value;
    }
    public static Object getOrDefault(String key, Object defaultValue) {
        Object val = SBConfig.getNestedValue(SBConfig.getInstance().data, key);

        if (val == null) {
            set(key, defaultValue);
            return defaultValue;
        }
        return val;
    }

    public static String getString(String key) {
        return String.valueOf(SBConfig.getOrDefault(key, ""));
    }
    public static boolean getBoolean(String key) {
        return (boolean) SBConfig.getOrDefault(key, false);
    }
    public static int getInt(String key) {
        return (int) SBConfig.getOrDefault(key, 0);
    }
    public static long getLong(String key) {
        return (long) SBConfig.getOrDefault(key, 0L);
    }
    public static double getDouble(String key) {
        return (double) SBConfig.getOrDefault(key, 0.0);
    }
    public static float getFloat(String key) {
        return (float) SBConfig.getOrDefault(key, 0.0f);
    }

    public static String getString(String key, String val) {
        return String.valueOf(SBConfig.getOrDefault(key, val));
    }
    public static boolean getBoolean(String key, boolean val) {
        return (boolean) SBConfig.getOrDefault(key, val);
    }
    public static int getInt(String key, int val) {
        return (int) SBConfig.getOrDefault(key, val);
    }
    public static long getLong(String key, long val) {
        return (long) SBConfig.getOrDefault(key, val);
    }
    public static double getDouble(String key, double val) {
        return (double) SBConfig.getOrDefault(key, val);
    }
    public static float getFloat(String key, float val) {
        return (float) SBConfig.getOrDefault(key, val);
    }

    // ------------------- Helper Methods ------------------- //

    @SuppressWarnings("unchecked")
    private static Object getNestedValue(Map<String, Object> map, String path) {
        String[] parts = path.split("\\.");
        Object current = map;
        for (String part : parts) {
            if (!(current instanceof Map)) return null;
            current = ((Map<String, Object>) current).get(part);
            if (current == null) return null;
        }
        return current;
    }
    @SuppressWarnings("unchecked")
    private static void setNestedValue(Map<String, Object> map, String path, Object value) {
        String[] parts = path.split("\\.");
        Map<String, Object> current = map;
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            current = (Map<String, Object>) current.computeIfAbsent(part, k -> new LinkedHashMap<>());
        }
        current.put(parts[parts.length - 1], value);
    }
}