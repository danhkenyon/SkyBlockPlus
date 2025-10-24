package uk.ac.bsfc.sbp.utils.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SBDatabase {
    private static final HikariDataSource dataSource;

    private static final String URL = "jdbc:mariadb://localhost:3306/sbp";
    private static final String USER = "root";
    private static final String PASSWORD = "admin";

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(PASSWORD);
        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setMaximumPoolSize(20);
        config.setAutoCommit(false);
        dataSource = new HikariDataSource(config);
    }

    private SBDatabase() {
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public static boolean isConnected() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static List<Map<String, Object>> query(String sql, Object... params) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, params);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                results.add(row);
            }

        } catch (SQLException e) {
            throw new RuntimeException("[ERR] Query failed: " + sql, e);
        }
        return results;
    }

    public static int update(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, params)) {

            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[ERR] Update failed: " + sql, e);
        }
    }

    public static long insert(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            bindParameters(stmt, params);
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("[ERR] Insert failed: " + sql, e);
        }
        return -1;
    }

    private static PreparedStatement prepareStatement(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        bindParameters(stmt, params);
        return stmt;
    }

    private static void bindParameters(PreparedStatement stmt, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
        }
    }

    public static List<String> getAllColumnValues(String tableName, String columnName) {
        List<String> values = new ArrayList<>();
        List<Map<String, Object>> rows = query("SELECT " + columnName + " FROM " + tableName + ";");
        for (Map<String, Object> row : rows) {
            Object value = row.get(columnName);
            if (value != null) values.add(value.toString());
        }
        return values;
    }
}