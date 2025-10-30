package uk.ac.bsfc.sbp.utils.skyblock;

import org.bukkit.Location;
import org.bukkit.World;
import uk.ac.bsfc.sbp.core.skyblock.Island;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class IslandUtils {
    private final Map<Long, Island> islands;
    private static final AtomicLong COUNTER = new AtomicLong(0);

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

    public Map<Long, Island> getIslands() {
        return islands;
    }

    public void initIslands() {
        List<Island> rows = IslandTable.getInstance().getRows();

        for (Island island : rows) {
            islands.put(island.getId(), island);
            COUNTER.updateAndGet(prev -> Math.max(prev, island.getId()));
            SBLogger.info("&aLoaded island &b" + island.getName() + " &7(ID: " + island.getId() + ")");
        }

        SBLogger.info("&aLoaded &b" + islands.size() + " &aislands from database.");
    }

    public Island getIsland(long id) {
        for (long islandId : islands.keySet()) {
            if (id == islandId) {
                return islands.get(islandId);
            }
        }
        return null;
    }
    public Island getIsland(String name) {
        for (Island island : islands.values()) {
            if (island.getName().equalsIgnoreCase(name)) {
                return island;
            }
        }
        return null;
    }

    public void registerIsland(Island island) {
        islands.put(island.getId(), island);
    }

    public static long generateId() {
        return COUNTER.incrementAndGet();
    }

    private static int index = 0;
    public static Location nextLocation() {
        World world = SBConstants.Island.ISLAND_WORLD;
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
        return new Location(world, x * spacing, 100, z * spacing);
    }
}
