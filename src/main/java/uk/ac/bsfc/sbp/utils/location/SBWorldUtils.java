package uk.ac.bsfc.sbp.utils.location;

import uk.ac.bsfc.sbp.utils.data.JsonFile;
import uk.ac.bsfc.sbp.utils.data.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Utility class for managing the lifecycle and retrieval of {@link SBWorld} objects.
 * Provides functionality to register, unregister, retrieve, and save instances of SBWorld.
 * This class follows the singleton pattern to ensure a single instance is used globally.
 */
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
        if (!loadedWorlds.contains(world)) loadedWorlds.add(world);
    }
    public void unregister(SBWorld world) {
        loadedWorlds.remove(world);
    }
    public List<SBWorld> getLoadedWorlds() {
        return loadedWorlds;
    }

    public SBWorld getWorld(UUID uuid) {
        return loadedWorlds.stream()
                .filter(w -> w.getUniqueId() == uuid)
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
}
