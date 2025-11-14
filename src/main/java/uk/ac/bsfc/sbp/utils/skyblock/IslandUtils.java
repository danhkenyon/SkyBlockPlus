package uk.ac.bsfc.sbp.utils.skyblock;

import uk.ac.bsfc.sbp.core.skyblock.Island;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandTable;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.location.SBWorld;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class for managing and interacting with islands in the SkyBlock+ system.
 * This class provides a centralized singleton instance to handle operations
 * related to islands, such as retrieving, registering, and initializing them.
 */
public class IslandUtils {
    private final Map<UUID, Island> islands;

    private IslandUtils() {
        this.islands = new HashMap<>();
    }

    private static IslandUtils INSTANCE;
    public static IslandUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IslandUtils();
        }
        return INSTANCE;
    }

    public Map<UUID, Island> getIslands() {
        return islands;
    }

    public void init() {
        List<Island> rows = IslandTable.getInstance().getRows();

        for (Island island : rows) {
            islands.put(island.uuid(), island);
            SBLogger.info("<green>Loaded island <aqua>" + island.name() + " <gray>(ID: " + island.uuid() + ")");
        }

        SBLogger.info("<green>Loaded <aqua>" + islands.size() + " <green>islands from database.");
    }

    public Island getIsland(UUID id) {
        for (UUID uuid : islands.keySet()) {
            if (id == uuid) {
                return islands.get(uuid);
            }
        }

        return null;
    }
    public Island getIsland(String name) {
        for (Island island : islands.values()) {
            if (island.name().equalsIgnoreCase(name)) {
                return island;
            }
        }
        return null;
    }

    public void registerIsland(Island island) {
        islands.put(island.uuid(), island);
    }

    private static int index = 0;
    public static SBLocation nextLocation() {
        SBWorld world = SBConstants.Island.ISLAND_WORLD;
        int spacing = SBConstants.Island.BASE_ISLAND_SIZE + 1000;

        int layer = (int) Math.ceil((Math.sqrt(index + 1) - 1) / 2);
        int legLen = 2 * layer + 1;
        int legStart = (legLen - 2) * (legLen - 2);
        int pos = index - legStart;

        int x, z;

        if (pos < legLen - 1) {
            x = layer;
            z = -layer + 1 + pos;
        } else if (pos < 2 * (legLen - 1)) {
            x = layer - 1 - (pos - (legLen - 1));
            z = layer;
        } else if (pos < 3 * (legLen - 1)) {
            x = -layer;
            z = layer - 1 - (pos - 2 * (legLen - 1));
        } else {
            x = -layer + 1 + (pos - 3 * (legLen - 1));
            z = -layer;
        }

        index++;
        return SBLocation.of(world, x * spacing, 100, z * spacing);
    }
}
