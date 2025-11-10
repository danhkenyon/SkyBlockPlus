package uk.ac.bsfc.sbp.utils.location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for managing world-related data storage and retrieval.
 * Handles operations such as reading and writing world names to a JSON file.
 */
public class WorldManagerIO {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "worlds.json";

    public static File getWorldsFile(File pluginDataFolder) {
        return new File(pluginDataFolder, FILE_NAME);
    }

    public static List<String> loadWorldNames(File pluginDataFolder) {
        File file = getWorldsFile(pluginDataFolder);

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                JsonArray array = json.getAsJsonArray("worlds");
                List<String> names = new ArrayList<>();
                if (array != null) array.forEach(el -> names.add(el.getAsString()));
                return names;
            } catch (IOException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        List<String> detected = detectWorlds();
        saveWorldNames(pluginDataFolder, detected);
        return detected;
    }
    private static List<String> detectWorlds() {
        File worldContainer = Bukkit.getWorldContainer();
        List<String> defaults = Arrays.asList("world", "world_nether", "world_the_end");

        List<String> detected = new ArrayList<>();
        File[] dirs = worldContainer.listFiles();

        if (dirs != null) {
            for (File folder : dirs) {
                if (folder.isDirectory() && new File(folder, "level.dat").exists()) {
                    detected.add(folder.getName());
                }
            }
        }

        if (detected.isEmpty()) detected.addAll(defaults);

        return detected;
    }

    public static void saveWorldNames(File pluginDataFolder, List<String> worldNames) {
        File file = getWorldsFile(pluginDataFolder);
        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();
        worldNames.forEach(array::add);
        json.add("worlds", array);

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(json, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
