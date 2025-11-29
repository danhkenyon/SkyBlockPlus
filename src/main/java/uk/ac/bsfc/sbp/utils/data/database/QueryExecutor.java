package uk.ac.bsfc.sbp.utils.data.database;

import uk.ac.bsfc.sbp.utils.SBLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class QueryExecutor {
    private final ConnectionPool pool;
    private static final ExecutorService DB_EXECUTOR = Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors()));

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

    public CompletableFuture<List<Map<String, Object>>> asyncQuery(String sql, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            List<Map<String, Object>> results = new ArrayList<>();
            try (Connection conn = pool.getConnection();
                 PreparedStatement stmt = prepareStatement(conn, sql, params);
                 ResultSet rs = stmt.executeQuery()) {

                getResultSetMD(results, rs);
            } catch (SQLException e) {
                SBLogger.err("[DB] Async query failed: " + e.getMessage());
            }
            return results;
        }, DB_EXECUTOR);
    }
    public CompletableFuture<List<Map<String, Object>>> asyncQueryAll(String sql, Object... params) {
        return CompletableFuture.supplyAsync(() -> {
            List<Map<String, Object>> results = new ArrayList<>();
            try (Connection conn = pool.getConnection();
                 PreparedStatement stmt = prepareStatement(conn, sql, params);
                 ResultSet rs = stmt.executeQuery()) {

                getResultSetMD(results, rs);
            } catch (SQLException e) {
                SBLogger.err("[DB] Async queryAll failed: " + e.getMessage());
            }
            return results;
        }, DB_EXECUTOR);
    }
    public <T> CompletableFuture<List<T>> asyncQuery(String sql, Class<T> type, Object... params) {
        return asyncQueryAll(sql, params).thenApply(rows -> {
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
        });
    }
    public int update(String sql, Object... params) {
        return this.update(sql, sql, params);
    }
    public int update(String name, String sql, Object... params) {
        try (Connection conn = pool.getConnection();
             PreparedStatement stmt = prepareStatement(conn, sql, params)) {

            SBLogger.newLine();
            SBLogger.info("<green><------> <aqua><b>Database Update <green><------>");
            SBLogger.info("Script: " + name);

            int affected = stmt.executeUpdate();
            conn.commit();
            return affected;
        } catch (SQLException e) {
            // try to rollback if possible
            try {
                pool.getConnection().rollback();
            } catch (Exception ignored) {}
            throw new RuntimeException("[ERR] Update failed: " + sql, e);
        }
    }
    public long insert(String sql, Object... params) {
        try (Connection conn = pool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            bindParameters(stmt, params);
            stmt.executeUpdate();
            conn.commit();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }

        } catch (SQLException e) {
            // rollback attempt
            try {
                pool.getConnection().rollback();
            } catch (Exception ignored) {}
            throw new RuntimeException("[ERR] Insert failed: " + sql, e);
        }
        return -1;
    }
    public CompletableFuture<List<String>> getAllColumnValues(String table, String column) {
        String sql = "SELECT " + column + " FROM " + table;
        return asyncQueryAll(sql).thenApply(rows -> {
            List<String> values = new ArrayList<>();
            for (Map<String, Object> row : rows) {
                Object value = row.get(column);
                if (value != null) values.add(value.toString());
            }
            return values;
        });
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