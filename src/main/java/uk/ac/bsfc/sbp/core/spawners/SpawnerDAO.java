package uk.ac.bsfc.sbp.core.spawners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.SBLogger;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpawnerDAO {

    private Connection conn;

    public SpawnerDAO() {
        try {
            File folder = Main.getInstance().getDataFolder();

            File dbFile = new File(folder, "spawners.db");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(
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
            }

        } catch (SQLException e) {
            SBLogger.err("[SBP] Failed to connect to database: " + e.getMessage());
        }
    }

    public void saveSpawner(SpawnerData data) {
        String sql = "REPLACE INTO spawners (id, world, x, y, z, type, stack_size, level) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, data.getId().toString());
            ps.setString(2, data.getLocation().getWorld().getName());
            ps.setInt(3, data.getLocation().getBlockX());
            ps.setInt(4, data.getLocation().getBlockY());
            ps.setInt(5, data.getLocation().getBlockZ());
            ps.setString(6, data.getType().name());
            ps.setInt(7, data.getStackSize());
            ps.setInt(8, data.getLevel());
            ps.executeUpdate();
        } catch (SQLException e) {
            SBLogger.err("[SBP] Failed to save spawner: " + e.getMessage());
        }
    }

    public void deleteSpawner(Location loc) {
        String sql = "DELETE FROM spawners WHERE world=? AND x=? AND y=? AND z=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, loc.getWorld().getName());
            ps.setInt(2, loc.getBlockX());
            ps.setInt(3, loc.getBlockY());
            ps.setInt(4, loc.getBlockZ());
            ps.executeUpdate();
        } catch (SQLException e) {
            SBLogger.err("[SBP] Failed to delete spawner: " + e.getMessage());
        }
    }

    public List<SpawnerData> loadAllSpawners() {
        List<SpawnerData> list = new ArrayList<>();
        String sql = "SELECT * FROM spawners";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UUID id = UUID.fromString(rs.getString("id"));
                String worldName = rs.getString("world");
                var world = Bukkit.getWorld(worldName);
                if (world == null) continue;

                Location loc = new Location(
                        world,
                        rs.getInt("x"),
                        rs.getInt("y"),
                        rs.getInt("z")
                );

                SpawnerData data = new SpawnerData(
                        id,
                        loc,
                        EntityType.valueOf(rs.getString("type")),
                        rs.getInt("stack_size"),
                        rs.getInt("level")
                );

                list.add(data);
            }

        } catch (SQLException e) {
            SBLogger.err("[SBP] Failed to load spawners: " + e.getMessage());
        }

        return list;
    }

    public void close() {
        try {
            if (conn != null && !conn.isClosed()) conn.close();
        } catch (SQLException e) {
            SBLogger.err("[SBP] Failed to close database connection: " + e.getMessage());
        }
    }
}
