package uk.ac.bsfc.sbp.utils.location;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.Wrapper;
import uk.ac.bsfc.sbp.utils.data.JSON;
import uk.ac.bsfc.sbp.utils.data.JsonFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an SBWorld, a wrapper around a Bukkit {@link World} object that provides additional functionality
 * for managing worlds in a server environment. The class includes methods for creating, loading, unloading,
 * saving, and deleting worlds, as well as handling their associated metadata.
 */
public class SBWorld extends Wrapper<World> {
    private final JsonFile worldsJson = JSON.get("worlds");

    private final UUID uuid;
    private final String name;
    private final File worldDirectory;

    private WorldEnvironment env;
    private long seed;
    private boolean loaded;

    protected SBWorld(World world) {
        this.uuid = world.getUID();
        this.name = world.getName();
        this.worldDirectory = world.getWorldFolder();
        this.env = WorldEnvironment.valueOf(world.getEnvironment().name().toLowerCase());
        this.seed = world.getSeed();
        this.loaded = true;
    }
    protected SBWorld(String name, String env, long seed, boolean loaded) {
        this.uuid = UUID.nameUUIDFromBytes(name.getBytes());
        this.name = name;

        File worldDir = new File(Bukkit.getWorldContainer(), name);
        try {
            if (worldDir.mkdirs()) {
                SBLogger.raw("<green>World <gold>" + name + "<green> has been created");
            }
        }catch (SecurityException e){
            SBLogger.err(e.getMessage());
        }

        this.worldDirectory = worldDir;

        this.env = WorldEnvironment.valueOf(env.toUpperCase());
        this.seed = seed;
        this.loaded = loaded;
    }

    public static SBWorld getWorld(UUID uuid) {
        return SBWorldUtils.getInstance().getWorld(uuid);
    }
    public static SBWorld getWorld(String name) {
        SBWorld world = SBWorldUtils.getInstance().getWorld(name);
        if (world == null) {
            world = SBWorld.load(name);
        }
        return world;
    }


    public @SuppressWarnings("unchecked") static SBWorld load(String name) {
        JsonFile json = JSON.get("worlds");
        Object data = json.get(name);
        SBWorld world;

        if (data instanceof Map<?, ?> map) {
            world = fromMap((Map<Object, Object>) map);
        } else {
            world = new SBWorld(name, "NORMAL", System.currentTimeMillis(), false);
        }

        world.load();
        SBWorldUtils.getInstance().register(world);
        world.save();
        return world;
    }
    public static SBWorld create(String name, String env, long seed) {
        if (SBWorldUtils.getInstance().getLoadedWorlds().contains(SBWorld.getWorld(name))) {
            return SBWorld.getWorld(name);
        }
        SBWorld world = new SBWorld(name, env, seed, false);
        world.load();
        SBWorldUtils.getInstance().register(world);
        world.save();
        return world;
    }

    public void load() {
        if (!isLoaded()) {
            toBukkit();
        }
    }
    public void unload(boolean save) {
        if (isLoaded()) {
            Bukkit.unloadWorld(name, save);
            loaded = false;
        }
    }

    public boolean delete() {
        unload(false);
        try {
            deleteRecursively(worldDirectory);
            SBWorldUtils.getInstance().unregister(this);
            worldsJson.getData().remove(name);
            worldsJson.saveAsync();
            return true;
        } catch (IOException e) {
            SBLogger.err(e.getMessage());
            return false;
        }
    }
    private void deleteRecursively(File file) throws IOException {
        if (!file.exists()) return;
        if (file.isDirectory()) {
            for (File child : Objects.requireNonNull(file.listFiles())) {
                deleteRecursively(child);
            }
        }
        Files.delete(file.toPath());
    }

    public void save() {
        worldsJson.set(name, toMap());
        worldsJson.saveAsync();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("uuid", uuid.toString());
        map.put("environment", env);
        map.put("seed", seed);
        map.put("loaded", loaded);
        map.put("directory", worldDirectory.getAbsolutePath());
        return map;
    }
    public static SBWorld fromMap(Map<Object, Object> data) {
        String directory = String.valueOf(data.get("directory"));
        String name = new File(directory).getName();
        String env = String.valueOf(data.getOrDefault("environment", "NORMAL"));
        long seed = ((Number) data.getOrDefault("seed", System.currentTimeMillis())).longValue();
        boolean loaded = (boolean) data.getOrDefault("loaded", false);
        return new SBWorld(name, env, seed, loaded);
    }

    public UUID getUniqueId() {
        return uuid;
    }
    public String getName() {
        return name;
    }
    public File getWorldDirectory() {
        return worldDirectory;
    }
    public WorldEnvironment getEnvironment() {
        return env;
    }
    public long getSeed() {
        return seed;
    }
    public boolean isLoaded() {
        return loaded && Bukkit.getWorld(name) != null;
    }

    public void setEnvironment(WorldEnvironment env) {
        this.env = env;
    }
    public void setSeed(long seed) {
        this.seed = seed;
    }
    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    @Override
    public World toBukkit() {
        World world = Bukkit.getWorld(uuid);
        if (world == null) {
            try {
                boolean ignored = worldDirectory.mkdirs();
            }catch (SecurityException e){
                SBLogger.err(e.getMessage());
            }


            WorldCreator creator = new WorldCreator(name)
                    .environment(Environment.valueOf(env.name()))
                    .seed(seed)
                    .type(WorldType.NORMAL);

            world = creator.createWorld();
            loaded = (world != null);
        }
        return world;
    }
}
