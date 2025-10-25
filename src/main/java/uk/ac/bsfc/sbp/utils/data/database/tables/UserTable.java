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
    private UserTable() {
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
    }
    public void insert(UUID uuid, String username) {
        SBDatabase.update(
                "INSERT INTO " + this.getTableName() + " (uuid, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = ?;",
                uuid.toString(),
                username,
                username
        );
    }

    @Override
    public void ensureTableExists() {
        super.database.getExecutor().update(
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
