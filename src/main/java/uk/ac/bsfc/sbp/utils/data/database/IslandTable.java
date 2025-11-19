package uk.ac.bsfc.sbp.utils.data.database;

import uk.ac.bsfc.sbp.core.skyblock.Island;
import uk.ac.bsfc.sbp.utils.SBConstants;

import java.util.Map;

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
        return Island.create(
                java.util.UUID.fromString((String) row.get("uuid")),
                (String) row.get("name"),
                (String) row.get("world_name"),
                ((Number) row.get("x")).doubleValue(),
                ((Number) row.get("y")).doubleValue(),
                ((Number) row.get("z")).doubleValue(),
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
