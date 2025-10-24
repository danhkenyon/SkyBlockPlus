package uk.ac.bsfc.sbp.utils.data;

import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.user.SBUser;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UserDatabase {
    private static final String TABLE = "users";

    public static SBUser insertUser(Player player) {
        return UserDatabase.insertUser(player.getUniqueId(), player.getName());
    }
    public static SBUser insertUser(SBUser user) {
        return UserDatabase.insertUser(user.uuid(), user.username());
    }
    public static SBUser insertUser(UUID uuid, String username) {
        SBDatabase.update(
                "INSERT INTO " + TABLE + " (uuid, name) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = ?;",
                uuid.toString(),
                username,
                username
        );

        return UserDatabase.fetchUser(uuid);
    }

    public static void deleteUser(Player player) {
        UserDatabase.deleteUser(player.getUniqueId());
    }
    public static void deleteUser(SBUser user) {
        UserDatabase.deleteUser(user.uuid());
    }
    public static void deleteUser(UUID uuid) {
        SBDatabase.update("DELETE FROM " + TABLE + " WHERE uuid = ?;", uuid.toString());
    }
    public static void deleteUser(String username) {
        SBDatabase.update("DELETE FROM " + TABLE + " WHERE name = ?;", username);
    }

    public static SBUser fetchUser(UUID uuid) {
        List<Map<String, Object>> rows = SBDatabase.query(
                "SELECT uuid, name FROM " + TABLE + " WHERE uuid = ?;", uuid.toString()
        );
        if (rows.isEmpty()) {
            return null;
        }
        Map<String, Object> row = rows.getFirst();

        return SBUser.from(UUID.fromString(
                (String) row.get("uuid")),
                (String) row.get("name")
        );
    }
    public static SBUser fetchUser(String username) {
        List<Map<String, Object>> rows = SBDatabase.query(
                "SELECT uuid, name FROM " + TABLE + " WHERE name = ?;", username
        );
        if (rows.isEmpty()) {
            return null;
        }
        Map<String, Object> row = rows.getFirst();
        return SBUser.from(UUID.fromString(
                (String) row.get("uuid")),
                (String) row.get("name")
        );
    }

    public static List<SBUser> fetchUsers() {
        List<Map<String, Object>> rows = SBDatabase.query(
                "SELECT uuid, name FROM " + TABLE + ";"
        );
        return rows.stream().map(row ->
            SBUser.from(UUID.fromString(
                (String) row.get("uuid")),
                (String) row.get("name")
            )
        ).toList();
    }

    public static void ensureTable() {
        SBDatabase.update(
                "CREATE TABLE IF NOT EXISTS " + TABLE + " (" +
                        "uuid VARCHAR(36) PRIMARY KEY," +
                        "name VARCHAR(16) NOT NULL" +
                        ");"
        );
    }
}
