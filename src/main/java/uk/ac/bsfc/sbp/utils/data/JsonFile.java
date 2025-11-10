package uk.ac.bsfc.sbp.utils.data;

import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * The JsonFile class provides an abstraction for managing JSON files, enabling
 * easy reading, writing, and modification of JSON data. It handles nested values
 * using dot-separated paths and supports both synchronous and asynchronous
 * operations for file IO.
 *
 * Functionality includes:
 * - Loading and saving JSON data to a file.
 * - Asynchronous reload and save operations.
 * - Thread-safe access to the underlying data structure.
 * - Support for nested JSON structure manipulation using dot-separated keys.
 */
public class JsonFile {
    private final File jsonFile;
    private volatile Map<String, Object> data;

    protected JsonFile(File file) {
        this.jsonFile = file;
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IOException("Failed to create directory: " + parent.getAbsolutePath());
            }

            if (!file.exists()) {
                data = new LinkedHashMap<>();
                save();
            } else {
                loadSync();
            }
        } catch (Exception e) {
            throw new RuntimeException("[ERR] Error loading JSON file: " + file, e);
        }
    }

    private void loadSync() {
        try (FileReader reader = new FileReader(jsonFile)) {
            Type type = new TypeToken<Map<String, Object>>() {}.getType();
            data = JSON.getGson().fromJson(reader, type);
            if (data == null) data = new LinkedHashMap<>();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load JSON file", e);
        }
    }
    public CompletableFuture<Void> reloadAsync() {
        return CompletableFuture.runAsync(() -> {
            try (FileReader reader = new FileReader(jsonFile)) {
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> newData = JSON.getGson().fromJson(reader, type);
                if (newData == null) newData = new LinkedHashMap<>();
                data = newData;
            } catch (IOException e) {
                throw new RuntimeException("Failed to reload JSON file", e);
            }
        }, JSON.getExecutor());
    }
    public CompletableFuture<Void> saveAsync() {
        return CompletableFuture.runAsync(this::saveSync, JSON.getExecutor());
    }
    private void saveSync() {
        synchronized (this) {
            try (FileWriter writer = new FileWriter(jsonFile)) {
                JSON.getGson().toJson(data, writer);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save JSON file", e);
            }
        }
    }

    public void save() {
        saveAsync();
    }

    public void set(String key, Object value) {
        setNestedValue(data, key, value);
        save();
    }
    public Object get(String key) {
        return getNestedValue(data, key);
    }
    public Object getOrDefault(String key, Object def) {
        Object val = get(key);
        if (val == null) {
            set(key, def);
            return def;
        }
        return val;
    }

    public String getString(String key) {
        Object val = getOrDefault(key, "");
        return val == null ? "" : String.valueOf(val);
    }
    public boolean getBoolean(String key) {
        Object val = getOrDefault(key, false);
        return val instanceof Boolean ? (Boolean) val : Boolean.parseBoolean(String.valueOf(val));
    }
    public int getInt(String key) {
        Object val = getOrDefault(key, 0);
        return val instanceof Number ? ((Number) val).intValue() : Integer.parseInt(String.valueOf(val));
    }
    public long getLong(String key) {
        Object val = getOrDefault(key, 0L);
        return val instanceof Number ? ((Number) val).longValue() : Long.parseLong(String.valueOf(val));
    }
    public double getDouble(String key) {
        Object val = getOrDefault(key, 0.0);
        return val instanceof Number ? ((Number) val).doubleValue() : Double.parseDouble(String.valueOf(val));
    }
    public float getFloat(String key) {
        Object val = getOrDefault(key, 0.0f);
        return val instanceof Number ? ((Number) val).floatValue() : Float.parseFloat(String.valueOf(val));
    }

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

    public File getFile() {
        return jsonFile;
    }
    public Map<String, Object> getData() {
        return data;
    }
}
