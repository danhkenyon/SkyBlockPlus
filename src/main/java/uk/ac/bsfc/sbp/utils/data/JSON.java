package uk.ac.bsfc.sbp.utils.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.Map;
import java.util.concurrent.*;

public class JSON {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    private static final Map<String, JsonFile> files = new ConcurrentHashMap<>();
    private static final File dataFolder = new File("data");

    private static final ExecutorService executor = Executors.newCachedThreadPool(r -> {
        Thread t = new Thread(r, "JSON-AsyncWorker");
        t.setDaemon(true);
        return t;
    });

    private JSON() {}
    public static Gson getGson() {
        return gson;
    }

    public static ExecutorService getExecutor() {
        return executor;
    }
    public static JsonFile get(String name) {
        return files.computeIfAbsent(name, key -> {
            if (!dataFolder.exists() && !dataFolder.mkdirs()) {
                throw new RuntimeException("Failed to create data directory: " + dataFolder.getAbsolutePath());
            }
            return new JsonFile(new File(dataFolder, key.endsWith(".json") ? key : key + ".json"));
        });
    }

    public static void register(String key, JsonFile file) {
        files.put(key, file);
    }
    public static void clearCache() {
        files.clear();
    }
    public static void shutdown() {
        executor.shutdown();
    }
}
