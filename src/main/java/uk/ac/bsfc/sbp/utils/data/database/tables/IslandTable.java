package uk.ac.bsfc.sbp.utils.data.database.tables;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import uk.ac.bsfc.sbp.core.Island;
import uk.ac.bsfc.sbp.core.Member;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.data.database.DatabaseTable;
import uk.ac.bsfc.sbp.utils.skyblock.IslandUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
            long id = ((Number) row.get("id")).longValue();
            String name = (String) row.get("name");
            int size = ((Number) row.get("size")).intValue();
            String worldName = (String) row.get("world");
            double x = ((Number) row.get("x")).doubleValue();
            double y = ((Number) row.get("y")).doubleValue();
            double z = ((Number) row.get("z")).doubleValue();

            List<Member> members = IslandMemberTable.getInstance().getIslandMembers(id);
            Location loc = new Location(Bukkit.getWorld(worldName), x, y, z);
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
        super.database.getExecutor().update(
                "CREATE TABLE IF NOT EXISTS " + this.getTableName() + " (" +
                        "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
                        "name VARCHAR(64) NOT NULL UNIQUE," +
                        "size INT NOT NULL DEFAULT " + SBConstants.Island.BASE_ISLAND_SIZE + "," +
                        "world VARCHAR(64) NOT NULL," +
                        "x DOUBLE NOT NULL," +
                        "y DOUBLE NOT NULL," +
                        "z DOUBLE NOT NULL," +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                        ");"
        );
        SBLogger.info("[IslandTable] &aEnsured table &b" + this.getTableName() + "&a exists.");
    }

    public long insert(Island island, Location baseLocation) {
        if (island == null) {
            SBLogger.err("[IslandTable] Null island provided to insert()");
            return -1;
        }
        if (baseLocation == null || baseLocation.getWorld() == null) {
            SBLogger.err("[IslandTable] Invalid location provided to insert()");
            return -1;
        }

        super.database.getExecutor().insert(
                "INSERT INTO " + this.getTableName() + " (name, size, world, x, y, z) VALUES (?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE size = VALUES(size), world = VALUES(world), x = VALUES(x), y = VALUES(y), z = VALUES(z);",
                island.getName(),
                island.getSize(),
                baseLocation.getWorld().getName(),
                baseLocation.getX(),
                baseLocation.getY(),
                baseLocation.getZ()
        );

        long id = super.database.getExecutor().query(
                "SELECT id FROM " + this.getTableName() + " WHERE name = ? LIMIT 1;",
                Long.class,
                island.getName()
        ).stream().findFirst().orElse(-1L);

        if (id == -1) {
            SBLogger.err("[IslandTable] Failed to retrieve ID for island " + island.getName());
            return -1;
        }

        SBLogger.info("[IslandTable] &aSaved island &b" + island.getName() + "&a (ID: " + id + ")");
        return id;
    }

    public boolean exists(long id) {
        return super.exists("id", id);
    }
    public boolean exists(String name) {
        return super.exists("name", name);
    }
}
