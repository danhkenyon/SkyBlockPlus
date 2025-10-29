package uk.ac.bsfc.sbp.utils.data.database;

import uk.ac.bsfc.sbp.utils.SBLogger;

import java.sql.*;
import java.util.*;

public final class QueryExecutor {
    private final ConnectionPool pool;

    private QueryExecutor(ConnectionPool pool) {
        this.pool = pool;
    }

    private static QueryExecutor INSTANCE;
    public static QueryExecutor getInstance(ConnectionPool pool) {
        if (INSTANCE == null) {
            INSTANCE = new QueryExecutor(pool);
        }
        return INSTANCE;
    }

    public List<Map<String, Object>> query(String sql, Object... params) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = pool.getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, params);
             ResultSet rs = stmt.executeQuery()) {

            getResultSetMD(results, rs);
        } catch (SQLException e) {
            throw new RuntimeException("[ERR] Query failed: " + sql, e);
        }
        return results;
    }
    public <T> List<T> query(String sql, Class<T> type, Object... params) {
        List<Map<String, Object>> rows = query(sql, params);
        List<T> result = new ArrayList<>();

        for (Map<String, Object> row : rows) {
            try {
                if (Number.class.isAssignableFrom(type)
                        || type == String.class
                        || type == Boolean.class
                        || type.isPrimitive()) {
                    row.values().stream().findFirst().ifPresent(value -> result.add(type.cast(value)));
                    continue;
                }

                T instance = type.getDeclaredConstructor().newInstance();
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    try {
                        String fieldName = entry.getKey();
                        Object value = entry.getValue();

                        var field = type.getDeclaredField(fieldName);
                        field.setAccessible(true);
                        field.set(instance, value);
                    } catch (NoSuchFieldException ignored) {
                    }
                }
                result.add(instance);
            } catch (Exception e) {
                throw new RuntimeException("Failed to map row to " + type.getSimpleName(), e);
            }
        }

        return result;
    }

    public int update(String sql, Object... params) {
        try (Connection conn = pool.getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, params)) {

            SBLogger.newLine();
            SBLogger.info("&a<------> &b&lDatabase Update &a<------>");
            SBLogger.info("SQL Script: " + sql);
            SBLogger.info("Parameters: " + Arrays.toString(params));
            SBLogger.newLine();

            int affected = stmt.executeUpdate();
            conn.commit();
            return affected;
        } catch (SQLException e) {
            throw new RuntimeException("[ERR] Update failed: " + sql, e);
        }
    }
    public @SuppressWarnings("unsafe") long insert(String sql, Object... params) {
        try (Connection conn = pool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            bindParameters(stmt, params);
            stmt.executeUpdate();
            conn.commit();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }

        } catch (SQLException e) {
            throw new RuntimeException("[ERR] Insert failed: " + sql, e);
        }
        return -1;
    }

    public List<String> getAllColumnValues(String table, String column) {
        List<String> values = new ArrayList<>();
        List<Map<String, Object>> rows = query("SELECT " + column + " FROM " + table);
        for (Map<String, Object> row : rows) {
            Object value = row.get(column);
            if (value != null) values.add(value.toString());
        }
        return values;
    }

    private PreparedStatement prepareStatement(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        this.bindParameters(stmt, params);
        return stmt;
    }
    private void bindParameters(PreparedStatement stmt, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
        }
    }

    public static void getResultSetMD(List<Map<String, Object>> results, ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columns = meta.getColumnCount();

        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= columns; i++) {
                row.put(meta.getColumnLabel(i), rs.getObject(i));
            }
            results.add(row);
        }
    }
}
