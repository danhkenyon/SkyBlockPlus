package uk.ac.bsfc.sbp.utils.data.database;

import uk.ac.bsfc.sbp.core.skyblock.Island;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.data.SBDatabase;
import uk.ac.bsfc.sbp.utils.user.SBUser;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserTable extends DatabaseTable<SBUser> {
    public UserTable() {
        super(SBConstants.Database.TABLE_USERS, 1);
    }

    public static UserTable INSTANCE;
    public static UserTable getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserTable();
        }
        return INSTANCE;
    }

    public boolean exists(UUID uuid) {
        return this.exists("uuid", uuid.toString());
    }
    public boolean exists(String name) {
        return this.exists("username", name);
    }

    public SBUser getUser(UUID uuid) {
        if (exists(uuid)) return this.getRow("uuid", uuid.toString());
        else throw new NullPointerException("[UserTable] Could not find user: '" + uuid + "'");
    }
    public SBUser getUser(String name) {
        if (this.exists(name)) return this.getRow("username", name);
        else throw new NullPointerException("[UserTable] Could not find user: '" + name + "'");
    }

    public List<SBUser> getUsers(Island island) {
        if (island == null) {
            throw new NullPointerException("[UserTable] Attempted to get users for null island!");
        }
        return this.getRows("island_id", island.uuid().toString());
    }
    public List<SBUser> getUsers(UUID island_uuid) {
        return this.getRows("island_id", island_uuid.toString());
    }
    public List<SBUser> getUsers(String island_name) {
        return null;
    }

    public List<SBUser> getUsersWithRank(Island island, String rank) {
        if (island == null) {
            throw new NullPointerException("[UserTable] Attempted to get users for null island!");
        }

        List<Map<String, Object>> results = SBDatabase.query(
                "SELECT * FROM " + tableName + " WHERE island_id = ? AND island_rank = ?;",
                island.uuid().toString(),
                rank
        );

        return results.stream().map(this::mapRow).toList();
    }
    public List<SBUser> getUsersWithRank(UUID island_uuid, String rank) {
        List<Map<String, Object>> results = SBDatabase.query(
                "SELECT * FROM " + tableName + " WHERE island_id = ? AND island_rank = ?;",
                island_uuid.toString(),
                rank
        );

        return results.stream().map(this::mapRow).toList();
    }
    public List<SBUser> getUsersWithRank(String island_name, String rank) {
        return null; // todo
    }

    public void insert(SBUser user) {
        DatabaseType type = DatabaseConfig.getInstance().getType();
        if (type == DatabaseType.SQLITE) {
            super.database.getExecutor().insert(
                    "INSERT INTO " + this.getTableName() + " (uuid, username) VALUES (?, ?) " +
                            "ON CONFLICT(uuid) DO UPDATE SET username = excluded.username;",
                    user.getUniqueID().toString(),
                    user.getName()
            );
        } else {
            super.database.getExecutor().insert(
                    "INSERT INTO " + this.getTableName() + " (uuid, username) VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE username = ?;",
                    user.getUniqueID().toString(),
                    user.getName(),
                    user.getName()
            );
        }
    }
    public void insert(UUID uuid, String username) {
        DatabaseType type = DatabaseConfig.getInstance().getType();
        if (type == DatabaseType.SQLITE) {
            System.out.println("a");
            super.database.getExecutor().insert(
                    "INSERT INTO " + this.getTableName() + " (uuid, username) VALUES (?, ?) " +
                            "ON CONFLICT(uuid) DO UPDATE SET username = excluded.username;",
                    uuid.toString(),
                    username
            );
        } else {
            System.out.println("b");
            super.database.getExecutor().insert(
                    "INSERT INTO " + this.getTableName() + " (uuid, username) VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE username = ?;",
                    uuid.toString(),
                    username,
                    username
            );
        }
    }

    public void updateUsername(UUID uuid, String username) {
        super.database.getExecutor().update(
                "UPDATE " + this.getTableName() + " SET username = ? WHERE uuid = ?;",
                username,
                uuid.toString()
        );
    }
    public void updateUsername(String currentName, String newUsername) {
        super.database.getExecutor().update(
                "UPDATE " + this.getTableName() + " SET username = ? WHERE username = ?;",
                newUsername,
                currentName
        );
    }
    public void updateIslandUUID(UUID user_uuid, UUID island_uuid) {
        super.database.getExecutor().update(
                "UPDATE " + this.getTableName() + " SET island_id = ? WHERE uuid = ?;",
                island_uuid.toString(),
                user_uuid.toString()
        );
    }
    public void updateIslandRank(UUID user_uuid, String island_rank) {
        super.database.getExecutor().update(
                "UPDATE " + this.getTableName() + " SET island_rank = ? WHERE uuid = ?;",
                island_rank,
                user_uuid.toString()
        );
    }

    @Override
    public SBUser mapRow(Map<String, Object> row) {
        return SBUser.create(
                UUID.fromString((String) row.get("uuid")),
                (String) row.get("username"),
                ((Number)row.get("flight_time")).longValue(),
                UUID.fromString((String) row.get("island_id")),
                (String) row.get("island_rank")
        );
    }
    @Override
    public void ensureTableExists() {
        String rankColumn = "island_rank VARCHAR(16) NOT NULL DEFAULT 'RECRUIT' " +
                "CHECK (island_rank IN ('LEADER','CO_LEADER','OFFICER','MEMBER','RECRUIT'))";
        String islandDefault = "'" + SBConstants.Island.UNKNOWN_ISLAND_UUID + "'";

        super.database.getExecutor().update("User Table Creation",
                "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "username VARCHAR(64) NOT NULL," +
                "flight_time BIGINT NOT NULL DEFAULT 0," +
                "island_id VARCHAR(36) NOT NULL DEFAULT " + islandDefault + "," +
                rankColumn +
                ");"
        );
    }
}
