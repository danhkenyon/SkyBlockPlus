package uk.ac.bsfc.sbp.utils.location;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import uk.ac.bsfc.sbp.utils.SBLogger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;

public class WorldManager {
    private final Map<String, SBWorld> worlds = new HashMap<>();
    private final File pluginFolder;
    private final File worldsFile;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private WorldManager() {
        this.pluginFolder = new File("plugins/SkyBlockPlus");
        this.worldsFile = new File(pluginFolder, "worlds.json");

        if (!pluginFolder.exists()) pluginFolder.mkdirs();
        if (!worldsFile.exists()) saveWorldsList(new ArrayList<>());
    }

    private static WorldManager INSTANCE;

    public static WorldManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WorldManager();
        }
        return INSTANCE;
    }

    public void loadWorld(SBWorld world) {
        if (world.getBukkitWorld() != null) return;

        File folder = world.getWorldFolder();
        if (!folder.exists()) {
            throw new IllegalArgumentException("World folder does not exist: " + folder.getName());
        }

        World bukkitWorld = Bukkit.createWorld(new WorldCreator(world.getName()));
        world.setBukkitWorld(bukkitWorld);
        worlds.put(world.getName(), world);
    }

    public void unloadWorld(SBWorld world) {
        World bukkitWorld = world.getBukkitWorld();
        if (bukkitWorld == null) return;

        Bukkit.unloadWorld(bukkitWorld, false);
        world.setBukkitWorld(null);
        worlds.remove(world.getName());
    }

    public SBWorld createWorld(String name, long seed) {
        WorldCreator creator = new WorldCreator(name);
        creator.seed(seed);
        World world = Bukkit.createWorld(creator);
        SBWorld sbWorld = SBWorld.of(name, world);
        worlds.put(name, sbWorld);

        addWorldToJson(name);
        return sbWorld;
    }

    public SBWorld createWorld(String name) {
        return createWorld(name, System.currentTimeMillis());
    }

    public void deleteWorld(SBWorld world) {
        unloadWorld(world);

        File folder = world.getWorldFolder();
        if (folder.exists()) {
            deleteFolderRecursive(folder);
        }

        removeWorldFromJson(world.getName());
    }

    public SBWorld getWorld(String name) {
        return worlds.get(name);
    }

    public void loadAllFromJson() {
        List<String> worldNames = loadWorldsList();

        for (String name : worldNames) {
            File worldFolder = new File(Bukkit.getWorldContainer(), name);
            if (worldFolder.exists() && worldFolder.isDirectory()) {
                SBWorld world = SBWorld.of(name, worldFolder);
                loadWorld(world);
            } else {
                SBLogger.err("[SBP] World folder missing for " + name + " — skipping load.");
            }
        }
    }

    private List<String> loadWorldsList() {
        try (FileReader reader = new FileReader(worldsFile)) {
            Type listType = new TypeToken<List<String>>() {}.getType();
            List<String> list = gson.fromJson(reader, listType);
            return list != null ? list : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveWorldsList(List<String> worlds) {
        try (FileWriter writer = new FileWriter(worldsFile)) {
            gson.toJson(worlds, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addWorldToJson(String worldName) {
        List<String> worldsList = loadWorldsList();
        if (!worldsList.contains(worldName)) {
            worldsList.add(worldName);
            saveWorldsList(worldsList);
        }
    }

    private void removeWorldFromJson(String worldName) {
        List<String> worldsList = loadWorldsList();
        if (worldsList.remove(worldName)) {
            saveWorldsList(worldsList);
        }
    }

    // -----------------------
    // Utilities
    // -----------------------

    private void deleteFolderRecursive(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) deleteFolderRecursive(f);
                else f.delete();
            }
        }
        folder.delete();
    }
}
