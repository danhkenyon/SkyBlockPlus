package uk.ac.bsfc.sbp.utils.location;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.Wrapper;
import uk.ac.bsfc.sbp.utils.data.JSON;
import uk.ac.bsfc.sbp.utils.data.JsonFile;
import uk.ac.bsfc.sbp.utils.location.worlds.*;
import uk.ac.bsfc.sbp.utils.location.worlds.generators.FlatWorldGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public abstract class SBWorld extends Wrapper<World> {
    private final JsonFile worldsJson = JSON.get("worlds");

    private final File worldDirectory;
    private final UUID uuid;
    private final String name;
    private final WorldEnvironment env;
    private final long seed;

    protected SBWorld(World world) {
        this.worldDirectory = world.getWorldFolder();
        this.uuid = world.getUID();
        this.name = world.getName();
        this.env = WorldEnvironment.valueOf(world.getEnvironment().name());
        this.seed = world.getSeed();
    }
    protected SBWorld(String name, WorldEnvironment env, long seed) {
        this.worldDirectory = new File(Bukkit.getWorldContainer(), name);
        this.uuid = UUID.nameUUIDFromBytes(name.getBytes());
        this.name = name;
        this.env = env;
        this.seed = seed;

        try {
            if (worldDirectory.mkdirs()) {
                SBLogger.raw("<green>World <gold>" + name + "<green> directory created");
            }
        } catch (SecurityException e) {
            SBLogger.err(e.getMessage());
        }
    }

    public static SBWorld create(String name, WorldEnvironment env, long seed) {
        SBWorld existing = SBWorldUtils.getInstance().getWorld(name);
        if (existing != null) {
            return existing;
        }

        SBWorld world = switch (env) {
            case NETHER -> new SBNetherWorld(name, seed);
            case THE_END -> new SBEndWorld(name, seed);
            default -> new SBNormalWorld(name, seed);
        };

        world.loadWorld();
        SBWorldUtils.getInstance().register(world);
        world.save();

        return world;
    }

    public static SBWorld getWorld(UUID uuid) {
        return SBWorldUtils.getInstance().getWorld(uuid);
    }
    public static SBWorld getWorld(String name) {
        return SBWorldUtils.getInstance().getWorld(name);
    }

    @SuppressWarnings("unchecked")
    public static SBWorld load(String name) {
        JsonFile json = JSON.get("worlds");
        Object data = json.get(name);
        SBWorld world;

        if (data instanceof Map<?, ?> map) {
            world = SBWorld.fromMap((Map<Object, Object>) map);
        } else {
            World bukkitWorld = Bukkit.getWorld(name);
            if (bukkitWorld != null) {
                world = wrapExistingWorld(bukkitWorld);
            } else {
                world = new SBNormalWorld(name, System.currentTimeMillis());
            }
        }

        world.loadWorld();
        SBWorldUtils.getInstance().register(world);
        world.save();
        return world;
    }
    private static SBWorld wrapExistingWorld(World bukkitWorld) {
        String name = bukkitWorld.getName();
        long seed = bukkitWorld.getSeed();

        return switch (bukkitWorld.getEnvironment()) {
            case NETHER -> new SBNetherWorld(name, seed);
            case THE_END -> new SBEndWorld(name, seed);
            default -> new SBNormalWorld(name, seed);
        };
    }
    public void loadWorld() {
        World existing = Bukkit.getWorld(name);
        if (existing == null) {
            WorldCreator creator = this.getWorldCreator();
            creator.createWorld();
            SBLogger.raw("<green>World <gold>" + name + "<green> has been loaded");
        }
    }
    public void unload(boolean save) {
        if (isLoaded()) {
            Bukkit.unloadWorld(name, save);
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
        map.put("environment", env.name());
        map.put("seed", seed);
        map.put("directory", worldDirectory.getAbsolutePath());
        map.put("type", this.getClass().getSimpleName());

        if (this instanceof SBFlatWorld flatWorld) {
            map.put("layers", flatWorld.getLayers());
        }

        return map;
    }

    @SuppressWarnings("unchecked")
    public static SBWorld fromMap(Map<Object, Object> data) {
        String directory = String.valueOf(data.get("directory"));
        String name = new File(directory).getName();
        WorldEnvironment env = WorldEnvironment.valueOf(String.valueOf(data.getOrDefault("environment", "NORMAL")).toUpperCase());
        long seed = ((Number) data.getOrDefault("seed", System.currentTimeMillis())).longValue();
        String type = String.valueOf(data.getOrDefault("type", "SBNormalWorld"));

        return switch (type) {
            case "SBFlatWorld" -> {
                Object layersData = data.get("layers");
                List<FlatWorldGenerator.Layer> layers = List.of();
                if (layersData instanceof List<?> list) {
                    layers = (List<FlatWorldGenerator.Layer>) list;
                }
                yield new SBFlatWorld(name, env, seed, layers);
            }
            case "SBVoidWorld" -> new SBVoidWorld(name, env);
            case "SBNetherWorld" -> new SBNetherWorld(name, seed);
            case "SBEndWorld" -> new SBEndWorld(name, seed);
            default -> new SBNormalWorld(name, seed);
        };
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
        return Bukkit.getWorld(name) != null;
    }

    @Override
    public World toBukkit() {
        World world = Bukkit.getWorld(name);
        if (world == null) {
            world = getWorldCreator().createWorld();
        }
        return world;
    }

    public abstract WorldCreator getWorldCreator();
}
