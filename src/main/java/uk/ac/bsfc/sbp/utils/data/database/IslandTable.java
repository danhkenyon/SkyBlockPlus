package uk.ac.bsfc.sbp.utils.data.database;

import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.core.skyblock.Island;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.config.FeatureConfig;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.location.SBWorld;

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

    private SBLocation getNextIslandLocation() {
        SBLocation last = getLastIslandLocation();

        if (last == null) {
            return SBLocation.of(Main.getInstance().getConfig(FeatureConfig.class).skyblock.base_world_name+"_1", 0, 100, 0);
        }

        String worldName = last.getWorld().getName();
        int worldNumber = extractWorldNumber(worldName);
        int maxPerWorld = Main.getInstance().getConfig(FeatureConfig.class).skyblock.maxIslandsPerWorld;
        long countInThisWorld = super.getRows().stream()
                .filter(i -> extractWorldNumber(i.region().getLoc1().getWorld().getName()) == worldNumber)
                .count();

        if (countInThisWorld < maxPerWorld) {
            SBWorld.create(Main.getInstance().getConfig(FeatureConfig.class).skyblock.base_world_name + (worldNumber + 1), "NORMAL", 0);
        }

        SBLocation next = (countInThisWorld < maxPerWorld) ? SBLocation.of(
                worldName,
                last.x() + 250,
                last.y(),
                last.z()
        ) : SBLocation.of(
                Main.getInstance().getConfig(FeatureConfig.class).skyblock.base_world_name + (worldNumber + 1),
                0,
                100,
                0
        );

        return next;
    }

    private SBLocation getLastIslandLocation() {
        List<Island> islands = super.getRows();
        if (islands.isEmpty()) {
            return null;
        }

        islands.sort((a, b) -> {
            int aWorld = this.extractWorldNumber(a.region().getLoc1().getWorld().getName());
            int bWorld = this.extractWorldNumber(b.region().getLoc1().getWorld().getName());

            if (aWorld != bWorld) return Integer.compare(aWorld, bWorld);

            SBLocation aLoc = a.region().getLoc1();
            SBLocation bLoc = b.region().getLoc1();

            int cmpX = Double.compare(aLoc.x(), bLoc.x());
            if (cmpX != 0) return cmpX;
            int cmpY = Double.compare(aLoc.y(), bLoc.y());
            if (cmpY != 0) return cmpY;

            return Double.compare(aLoc.z(), bLoc.z());
        });
        Island last = islands.getLast();
        SBLocation loc = last.region().getLoc1();

        return SBLocation.of(
                loc.getWorld().getName(),
                loc.x(),
                loc.y(),
                loc.z()
        );
    }
    private int extractWorldNumber(String worldName) {
        if (worldName == null || !worldName.contains("_")) return 0;

        try {
            return Integer.parseInt(worldName.substring(worldName.lastIndexOf('_') + 1));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    @Override
    public Island mapRow(Map<String, Object> row) {
        return Island.get(
                UUID.fromString((String) row.get("uuid")),
                (String) row.get("name"),
                SBLocation.of(
                        (String) row.get("world_name"),
                        ((Number) row.get("x")).doubleValue(),
                        ((Number) row.get("y")).doubleValue(),
                        ((Number) row.get("z")).doubleValue()
                ),
                ((Number) row.get("time_created")).longValue()
        );
    }

    @Override
    public void ensureTableExists() {
        super.database.getExecutor().update(
                "Island Table Creation",

                "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                        "uuid VARCHAR(36) PRIMARY KEY," +
                        "name VARCHAR(64) NOT NULL," +
                        "world_name VARCHAR(32) NOT NULL," +
                        "x DOUBLE NOT NULL," +
                        "y DOUBLE NOT NULL," +
                        "z DOUBLE NOT NULL," +
                        "time_created BIGINT NOT NULL" +
                ");"
        );
    }
}
