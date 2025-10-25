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
        super(SBConstants.Database.TABLE_USERS);
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
        SBLogger.info("[UserTable] &aInserted &b"+user.getName()+"&a into the database.");
    }
    public void insert(SBUser user) {
        if (user == null) {
            SBLogger.err("[UserTable] NullPointerException during UserTable#insert(Player);");
            return;
        }
        super.database.getExecutor().insert(
                "INSERT INTO " + this.getTableName() + " (uuid, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = ?;",
                user.uuid().toString(),
                user.username(),
                user.username()
        );
        SBLogger.info("[UserTable] &aInserted &b"+user.username()+"&a into the database.");
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
            SBLogger.info("[UserTable] &aInserted &b"+username+"&a into the database.");
        } catch (RuntimeException e) {
            SBLogger.err("[UserTable] RuntimeException occurred for &b" + username + "&c: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void ensureTableExists() {
        super.database.getExecutor().update(
                "CREATE TABLE IF NOT EXISTS " + this.getTableName() + " (" +
                    "uuid VARCHAR(36) PRIMARY KEY," +
                    "name VARCHAR(16) NOT NULL" +
                    ");"
        );
        SBLogger.info("[UserTable] &aRan table creation script.");
    }

    @Override
    public SBUser mapRow(Map<String, Object> row) {
        return SBUser.from(
                UUID.fromString((String) row.get("uuid")),
                (String) row.get("name")
        );
    }
}
