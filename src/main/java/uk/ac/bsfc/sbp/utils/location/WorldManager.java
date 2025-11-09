package uk.ac.bsfc.sbp.utils.location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.data.SBFiles;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles world creation, loading, unloading, and persistence for the plugin.
 * Worlds are tracked via SBWorld objects and saved to a JSON file.
 */
public final class WorldManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type STRING_LIST_TYPE = new TypeToken<List<String>>() {}.getType();

    private static volatile WorldManager INSTANCE;

    private final Map<String, SBWorld> loadedWorlds = new ConcurrentHashMap<>();
    private final File worldsFile;

    // -----------------------
    // Singleton
    // -----------------------

    private WorldManager() {
        this.worldsFile = SBFiles.get("worlds.json");
        ensureWorldsFile();
    }

    public static WorldManager getInstance() {
        if (INSTANCE == null) {
            synchronized (WorldManager.class) {
                if (INSTANCE == null) INSTANCE = new WorldManager();
            }
        }
        return INSTANCE;
    }

    // -----------------------
    // Initialization
    // -----------------------

    private void ensureWorldsFile() {
        if (worldsFile.exists()) return;

        try {
            if (worldsFile.createNewFile()) {
                saveWorldList(Arrays.asList("world", "world_nether", "world_the_end"));
            }
        } catch (IOException e) {
            SBLogger.err("[SBP] Failed to create worlds.json<br>" + e.getMessage());
        }
    }

    // -----------------------
    // Public API
    // -----------------------

    /**
     * Loads all worlds defined in the JSON file, if their folders exist.
     */
    public void loadAllFromJson() {
        for (String name : loadWorldList()) {
            File folder = new File(Bukkit.getWorldContainer(), name);
            if (!folder.isDirectory()) {
                SBLogger.err("[SBP] Missing folder for world \"" + name + "\" — skipped.");
                continue;
            }
            loadWorld(SBWorld.of(name, folder));
        }
    }

    /**
     * Loads a world if it exists on disk and isn't already loaded.
     */
    public SBWorld loadWorld(SBWorld world) {
        if (world == null) return null;
        if (world.toBukkit() != null) return world; // already loaded

        File folder = world.getFolder();
        if (!folder.exists()) {
            SBLogger.err("[SBP] Tried to load non-existent world: " + folder.getName());
            return null;
        }

        World bukkitWorld = Bukkit.createWorld(new WorldCreator(world.getName()));
        world.setBukkit(bukkitWorld);
        loadedWorlds.put(world.getName(), world);
        return world;
    }

    /**
     * Creates a new world and persists it to the JSON list.
     */
    public SBWorld createWorld(String name, long seed) {
        if (loadedWorlds.containsKey(name)) return loadedWorlds.get(name);

        World world = Bukkit.createWorld(new WorldCreator(name).seed(seed));
        SBWorld sbWorld = SBWorld.of(name, world);

        loadedWorlds.put(name, sbWorld);
        addWorldToJson(name);

        return sbWorld;
    }

    /** Creates a world with a random seed. */
    public SBWorld createWorld(String name) {
        return createWorld(name, System.currentTimeMillis());
    }

    /**
     * Unloads the given world from memory, without saving changes.
     */
    public void unloadWorld(SBWorld world) {
        if (world == null) return;

        World bukkitWorld = world.toBukkit();
        if (bukkitWorld == null) return;

        Bukkit.unloadWorld(bukkitWorld, false);
        world.setBukkit(null);
        loadedWorlds.remove(world.getName());
    }

    /**
     * Deletes a world from disk and removes it from JSON tracking.
     */
    public void deleteWorld(SBWorld world) {
        if (world == null) return;

        unloadWorld(world);

        File folder = world.getFolder();
        if (folder.exists()) {
            deleteRecursive(folder);
        }

        removeWorldFromJson(world.getName());
    }

    /**
     * Gets a loaded world by name.
     */
    public SBWorld getWorld(String name) {
        return loadedWorlds.get(name);
    }

    /**
     * Returns a read-only view of loaded worlds.
     */
    public Collection<SBWorld> getLoadedWorlds() {
        return Collections.unmodifiableCollection(loadedWorlds.values());
    }

    // -----------------------
    // JSON Persistence
    // -----------------------

    private List<String> loadWorldList() {
        if (!worldsFile.exists()) return new ArrayList<>();
        try (FileReader reader = new FileReader(worldsFile)) {
            List<String> list = GSON.fromJson(reader, STRING_LIST_TYPE);
            return list == null ? new ArrayList<>() : new ArrayList<>(list);
        } catch (IOException e) {
            SBLogger.err("[SBP] Failed to read worlds.json");
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveWorldList(List<String> worlds) {
        try (FileWriter writer = new FileWriter(worldsFile, false)) {
            GSON.toJson(worlds, writer);
        } catch (IOException e) {
            SBLogger.err("[SBP] Failed to write worlds.json");
            e.printStackTrace();
        }
    }

    private void addWorldToJson(String name) {
        List<String> worlds = loadWorldList();
        if (worlds.contains(name)) return;
        worlds.add(name);
        saveWorldList(worlds);
    }

    private void removeWorldFromJson(String name) {
        List<String> worlds = loadWorldList();
        if (worlds.remove(name)) {
            saveWorldList(worlds);
        }
    }

    // -----------------------
    // Utilities
    // -----------------------

    private void deleteRecursive(File folder) {
        File[] contents = folder.listFiles();
        if (contents != null) {
            for (File file : contents) {
                if (file.isDirectory()) deleteRecursive(file);
                else if (!file.delete()) {
                    SBLogger.warn("[SBP] Could not delete: " + file.getAbsolutePath());
                }
            }
        }
        if (!folder.delete()) {
            SBLogger.warn("[SBP] Could not delete folder: " + folder.getAbsolutePath());
        }
    }
}
