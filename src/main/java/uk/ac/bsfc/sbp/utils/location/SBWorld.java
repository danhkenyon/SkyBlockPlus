package uk.ac.bsfc.sbp.utils.location;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.Wrapper;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

public class SBWorld extends Wrapper<World> {
    private final String name;
    private final File worldFolder;
    private World bukkitWorld;

    private SBWorld(String name, World bukkitWorld) {
        this.name = name;
        this.bukkitWorld = bukkitWorld;
        this.worldFolder = bukkitWorld.getWorldFolder();
    }
    private SBWorld(String name, File worldFolder) {
        this.name = name;
        this.worldFolder = worldFolder;
    }

    public static SBWorld of(String name, World bukkitWorld) {
        return new SBWorld(name, bukkitWorld);
    }
    public static SBWorld of(String name, File worldFolder) {
        return new SBWorld(name, worldFolder);
    }
    public static SBWorld of(String name) {
        File pluginFolder = new File(Main.getInstance().getDataFolder(),"SkyBlockPlus");
        File jsonFile = new File(pluginFolder, "worlds.json");

        if (!jsonFile.exists()) {
            throw new IllegalStateException("worlds.json does not exist in plugin folder: " + jsonFile.getAbsolutePath());
        }

        try (FileReader reader = new FileReader(jsonFile)) {
            Type listType = new TypeToken<List<String>>() {}.getType();
            List<String> worldNames = new Gson().fromJson(reader, listType);

            if (worldNames == null || !worldNames.contains(name)) {
                throw new IllegalArgumentException("World '" + name + "' not found in worlds.json!");
            }

            World bukkitWorld = Bukkit.getWorld(name);
            if (bukkitWorld != null) {
                return new SBWorld(name, bukkitWorld);
            }

            File worldFolder = new File(Bukkit.getWorldContainer(), name);
            if (!worldFolder.exists()) {
                throw new IllegalStateException("World folder not found: " + worldFolder.getAbsolutePath());
            }

            return new SBWorld(name, worldFolder);
        } catch (Exception e) {
            SBLogger.err(e.getMessage());
            throw new RuntimeException("Failed to load SBWorld for " + name, e);
        }
    }

    public String getName() {
        return name;
    }
    public File getFolder() {
        return worldFolder;
    }

    @Override
    public World toBukkit() {
        return bukkitWorld;
    }
    public void setBukkit(World world) {
        this.bukkitWorld = world;
    }

    public Block getBlock(int x, int y, int z) {
        return this.toBukkit().getBlockAt(x, y, z);
    }
    public Block getBlock(SBLocation location) {
        return this.toBukkit().getBlockAt(location.toBukkit());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        SBWorld sbWorld = (SBWorld) obj;

        return name.equals(sbWorld.name);
    }
}
