package uk.ac.bsfc.sbp.utils.data.database.tables;

import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.data.SBDatabase;
import uk.ac.bsfc.sbp.utils.data.database.DatabaseTable;
import uk.ac.bsfc.sbp.utils.user.SBUser;

import java.util.Map;
import java.util.UUID;

public class UserTable extends DatabaseTable<SBUser> {
    public UserTable() {
        super(SBConstants.Database.TABLE_USERS, 1);
    }

    private static UserTable INSTANCE;
    public static UserTable getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UserTable();
        }
        return INSTANCE;
    }

    public void insert(Player user) {
        if (user == null) {
            SBLogger.err("[UserTable] NullPointerException during UserTable#insert(Player);");
            return;
        }
        super.database.getExecutor().insert(
                "INSERT INTO " + this.getTableName() + " (uuid, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = ?;",
                user.getUniqueId().toString(),
                user.getName(),
                user.getName()
        );
        SBLogger.info("[UserTable] <green>Inserted <aqua>"+user.getName()+"<green> into the database.");
    }
    public void insert(SBUser user) {
        if (user == null) {
            SBLogger.err("[UserTable] NullPointerException during UserTable#insert(Player);");
            return;
        }
        super.database.getExecutor().insert(
                "INSERT INTO " + this.getTableName() + " (uuid, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = ?;",
                user.getUniqueID().toString(),
                user.getName(),
                user.getName()
        );
        SBLogger.info("[UserTable] <green>Inserted <aqua>"+user.getName()+"<green> into the database.");
    }
    public void insert(UUID uuid, String username) {
        try {
            SBDatabase.update(
                    "INSERT INTO " + this.getTableName() + " (uuid, name) VALUES (?, ?) " +
                        "ON DUPLICATE KEY UPDATE name = ?;",
                    uuid.toString(),
                    username,
                    username
            );
            SBLogger.info("[UserTable] <green>Inserted <aqua>"+username+"<green> into the database.");
        } catch (RuntimeException e) {
            SBLogger.err("[UserTable] RuntimeException occurred for <aqua>" + username + "<red>: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean exists(UUID uuid) {
        return super.exists("uuid", uuid);
    }
    public boolean exists(String name) {
        return super.exists("name", name);
    }

    @Override
    public void ensureTableExists() {
        super.database.getExecutor().update("User Table Creation",
                "CREATE TABLE IF NOT EXISTS " + this.getTableName() + " (" +
                    "uuid VARCHAR(36) PRIMARY KEY," +
                    "name VARCHAR(16) NOT NULL" +
                    ");"
        );
    }

    @Override
    public SBUser mapRow(Map<String, Object> row) {
        return SBUser.from(
                UUID.fromString((String) row.get("uuid")),
                (String) row.get("name")
        );
    }
}
