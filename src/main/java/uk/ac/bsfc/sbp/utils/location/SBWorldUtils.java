package uk.ac.bsfc.sbp.utils.location;

import org.bukkit.Bukkit;
import org.bukkit.World;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.data.JSON;
import uk.ac.bsfc.sbp.utils.data.JsonFile;
import uk.ac.bsfc.sbp.utils.location.worlds.SBEndWorld;
import uk.ac.bsfc.sbp.utils.location.worlds.SBNetherWorld;
import uk.ac.bsfc.sbp.utils.location.worlds.SBNormalWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SBWorldUtils {
    private final List<SBWorld> loadedWorlds = new ArrayList<>();
    private final JsonFile worldsJson = JSON.get("worlds");

    private SBWorldUtils() {}

    private static SBWorldUtils INSTANCE;
    public static SBWorldUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SBWorldUtils();
        }
        return INSTANCE;
    }

    public void register(SBWorld world) {
        if (!loadedWorlds.contains(world)) {
            loadedWorlds.add(world);
        }
    }
    public void unregister(SBWorld world) {
        loadedWorlds.remove(world);
    }

    public List<SBWorld> getLoadedWorlds() {
        return new ArrayList<>(loadedWorlds);
    }
    public SBWorld getWorld(UUID uuid) {
        return loadedWorlds.stream()
                .filter(w -> w.getUniqueId().equals(uuid))
                .findFirst()
                .orElse(null);
    }
    public SBWorld getWorld(String name) {
        return loadedWorlds.stream()
                .filter(w -> w.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public void saveAll() {
        for (SBWorld world : loadedWorlds) {
            world.save();
        }
    }
    public void loadAllWorlds() {
        for (World bukkitWorld : Bukkit.getWorlds()) {
            if (getWorld(bukkitWorld.getName()) == null) {
                SBWorld wrapped = wrapExistingWorld(bukkitWorld);
                register(wrapped);
                wrapped.save();
                SBLogger.raw("<green>Wrapped existing world: <gold>" + bukkitWorld.getName());
            }
        }
        Object data = worldsJson.getData();

        if (data instanceof Map<?, ?> worldsMap) {
            for (Map.Entry<?, ?> entry : worldsMap.entrySet()) {
                String worldName = String.valueOf(entry.getKey());

                if (getWorld(worldName) != null) {
                    continue;
                }
                if (entry.getValue() instanceof Map<?, ?> worldData) {
                    try {
                        @SuppressWarnings("unchecked")
                        SBWorld world = SBWorld.fromMap((Map<Object, Object>) worldData);
                        world.loadWorld();
                        register(world);
                        SBLogger.raw("<green>Loaded world from JSON: <gold>" + worldName);
                    } catch (Exception e) {
                        SBLogger.err("Failed to load world " + worldName + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    private SBWorld wrapExistingWorld(World bukkitWorld) {
        String name = bukkitWorld.getName();
        long seed = bukkitWorld.getSeed();

        return switch (bukkitWorld.getEnvironment()) {
            case NETHER -> new SBNetherWorld(name, seed);
            case THE_END -> new SBEndWorld(name, seed);
            default -> new SBNormalWorld(name, seed);
        };
    }
}
