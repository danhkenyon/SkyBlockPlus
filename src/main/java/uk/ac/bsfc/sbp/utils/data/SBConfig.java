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

public class SBConfig {
    private static SBConfig instance;
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

    private static SBConfig getInstance() {
        if (instance == null) {
            instance = new SBConfig();
        }
        return instance;
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