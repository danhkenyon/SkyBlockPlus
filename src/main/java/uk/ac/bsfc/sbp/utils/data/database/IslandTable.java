package uk.ac.bsfc.sbp.utils.data.database;

import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.core.skyblock.Island;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.config.FeatureConfig;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.location.WorldEnvironment;
import uk.ac.bsfc.sbp.utils.location.worlds.SBVoidWorld;

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

    public SBLocation getNextIslandLocation() {
        SBLocation last = getLastIslandLocation();

        if (last == null) {
            return SBLocation.of(
                    Main.getInstance().getConfig(FeatureConfig.class).skyblock.base_world_name + "_1",
                    0, 100, 0
            );
        }
        String baseName = Main.getInstance().getConfig(FeatureConfig.class).skyblock.base_world_name;

        String currentWorldName = last.getWorld().getName();
        int worldNumber = extractWorldNumber(currentWorldName);

        int maxPerWorld = Main.getInstance().getConfig(FeatureConfig.class).skyblock.maxIslandsPerWorld;
        List<Island> islands = super.getRows();

        long countInThisWorld = islands.stream()
                .filter(i -> extractWorldNumber(i.region().getLoc1().getWorld().getName()) == worldNumber)
                .count();

        String targetWorld;

        if (countInThisWorld >= maxPerWorld) {
            targetWorld = baseName + (worldNumber + 1);

            SBVoidWorld.create(
                    targetWorld,
                    WorldEnvironment.NORMAL,
                    0
            );
            return SBLocation.of(
                    targetWorld,
                    0,
                    100,
                    0
            );
        }

        return SBLocation.of(
                currentWorldName,
                last.x() + 250,
                last.y(),
                last.z()
        );
    }

    public SBLocation getLastIslandLocation() {
        List<Island> islands = new ArrayList<>(super.getRows());
        System.out.println(islands);

        if (islands.isEmpty()) {
            return SBLocation.of(
                    Main.getInstance().getConfig(FeatureConfig.class).skyblock.base_world_name,
                    0, 100, 0
            );
        }

        islands.sort((a, b) -> {
            if (a == null && b == null) return 0;
            if (a == null) return -1;
            if (b == null) return 1;

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

        if (last != null) {
            SBLocation loc = last.region().getLoc1();

            return SBLocation.of(
                    loc.getWorld().getName(),
                    loc.x(),
                    loc.y(),
                    loc.z()
            );
        } else {
            SBVoidWorld.create(Main.getInstance().getConfig(FeatureConfig.class).skyblock.base_world_name+"_1", WorldEnvironment.NORMAL, 0).toBukkit();
            return SBLocation.of(Main.getInstance().getConfig(FeatureConfig.class).skyblock.base_world_name + "_1", 0, 100, 0);
        }
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
        if (row == null || row.isEmpty() || row.get("uuid") == null || row.get("name") == null) {
            return null;
        }
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
