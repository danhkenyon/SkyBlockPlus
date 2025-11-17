package uk.ac.bsfc.sbp.utils.data;

import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.SBLogger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteManager {

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                File folder = Main.getInstance().getDataFolder();

                String path = new File(folder, "spawners.db").getAbsolutePath();
                connection = DriverManager.getConnection("jdbc:sqlite:" + path);
            } catch (SQLException e) {
                SBLogger.err("Failed to connect to database: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }

    public static void init() {
        try (Connection conn = getConnection()) {
            conn.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS spawners (" +
                            "id TEXT PRIMARY KEY," +
                            "world TEXT," +
                            "x INTEGER," +
                            "y INTEGER," +
                            "z INTEGER," +
                            "type TEXT," +
                            "stack_size INTEGER," +
                            "level INTEGER" +
                            ");"
            );
        } catch (SQLException e) {
            SBLogger.err("Failed to initialize database: " + e.getMessage());
        }
    }
}
