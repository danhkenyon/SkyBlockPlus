package uk.ac.bsfc.sbp.utils.data.database.tables;

import org.bukkit.Location;
import uk.ac.bsfc.sbp.core.skyblock.Island;
import uk.ac.bsfc.sbp.core.skyblock.Member;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.data.database.DatabaseTable;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.location.SBWorld;
import uk.ac.bsfc.sbp.utils.skyblock.IslandUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static uk.ac.bsfc.sbp.utils.SBConstants.Island.BASE_ISLAND_SIZE;
import static uk.ac.bsfc.sbp.utils.SBConstants.Island.UNKNOWN_ISLAND_UUID;

public class IslandTable extends DatabaseTable<Island> {
    public IslandTable() {
        super(SBConstants.Database.TABLE_ISLANDS, 2);
    }

    private static IslandTable INSTANCE;
    public static IslandTable getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IslandTable();
        }
        return INSTANCE;
    }

    @Override
    public Island mapRow(Map<String, Object> row) {
        try {
            UUID id = UUID.fromString((String) row.get("id"));

            String name = (String) row.get("name");
            String worldName = (String) row.get("world");
            double x = ((Number) row.get("x")).doubleValue();
            double y = ((Number) row.get("y")).doubleValue();
            double z = ((Number) row.get("z")).doubleValue();

            List<Member> members = IslandMemberTable.getInstance().getIslandMembers(id);
            SBLocation loc = SBLocation.of(SBWorld.of(worldName), x, y, z);
            Island island = Island.createIsland(id, name, loc, members);

            island.setName(name);
            IslandUtils.getInstance().getIslands().put(id, island);
            return island;
        } catch (Exception e) {
            SBLogger.err("[IslandTable] Failed to map row: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void ensureTableExists() {
        super.database.getExecutor().update("Island Table Creation",
                "CREATE TABLE IF NOT EXISTS " + this.getTableName() + " (" +
                        "id CHAR(36) PRIMARY KEY," +
                        "name VARCHAR(64) NOT NULL UNIQUE," +
                        "size INT NOT NULL DEFAULT " + BASE_ISLAND_SIZE + "," +
                        "world VARCHAR(64) NOT NULL," +
                        "x DOUBLE NOT NULL," +
                        "y DOUBLE NOT NULL," +
                        "z DOUBLE NOT NULL," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                        ");"
        );
    }

    public UUID insert(Island island, Location baseLocation) {
        if (island == null) {
            SBLogger.err("[IslandTable] Null island provided to insert()");
            return UNKNOWN_ISLAND_UUID;
        }
        if (baseLocation == null || baseLocation.getWorld() == null) {
            SBLogger.err("[IslandTable] Invalid location provided to insert()");
            return UNKNOWN_ISLAND_UUID;
        }
        String id = UUID.randomUUID().toString();

        super.database.getExecutor().insert(
                "INSERT INTO " + this.getTableName() + " (id, name, size, world, x, y, z) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE size = VALUES(size), world = VALUES(world), x = VALUES(x), y = VALUES(y), z = VALUES(z);",
                id,
                island.name(),
                island.size(),
                baseLocation.getWorld().getName(),
                baseLocation.getX(),
                baseLocation.getY(),
                baseLocation.getZ()
        );

        UUID retrieved = UUID.fromString(super.database.getExecutor().asyncQuery(
                "SELECT id FROM " + this.getTableName() + " WHERE name = ? LIMIT 1;",
                String.class,
                island.name()
        ).join().stream().findFirst().orElse(UNKNOWN_ISLAND_UUID.toString()));

        if (UNKNOWN_ISLAND_UUID.equals(retrieved)) {
            SBLogger.err("[IslandTable] Failed to retrieve ID for island " + island.name());
            return UNKNOWN_ISLAND_UUID;
        }

        SBLogger.info("[IslandTable] <green>Saved island <aqua>" + island.name() + "<green> (ID: " + retrieved + ")");
        return retrieved;
    }

    public boolean exists(UUID id) {
        return super.exists("id", id);
    }
    public boolean existsByName(String name) {
        return super.exists("name", name);
    }
}