package uk.ac.bsfc.sbp.utils.data;

import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.data.database.ConnectionPool;
import uk.ac.bsfc.sbp.utils.data.database.DatabaseConfig;
import uk.ac.bsfc.sbp.utils.data.database.QueryExecutor;

import java.util.List;
import java.util.Map;

public class SBDatabase implements AutoCloseable {
    private final ConnectionPool pool;
    private final QueryExecutor executor;

    private SBDatabase(DatabaseConfig config) {
        this.pool = ConnectionPool.getInstance(true, config);
        this.executor = QueryExecutor.getInstance(pool);
    }
    private SBDatabase() {
        this(DatabaseConfig.getConfig());
    }

    private static SBDatabase INSTANCE;
    public static SBDatabase getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SBDatabase();
        }
        return INSTANCE;
    }
    public static SBDatabase getInstance(boolean createNew) {
        if (createNew) {
            return new SBDatabase();
        }
        return SBDatabase.getInstance();
    }

    public static void reload() {
        SBDatabase.getInstance().close();
        SBDatabase.getInstance(true);
    }

    public ConnectionPool getPool() {
        return pool;
    }
    public QueryExecutor getExecutor() {
        return this.executor;
    }

    public boolean isConnected() {
        return pool.isConnected();
    }

    public static List<Map<String, Object>> query(String sql, Object... params) {
        try {
            return SBDatabase.getInstance().getExecutor().asyncQuery(sql, params).join();
        } catch (RuntimeException e) {
            SBLogger.err("[SBDatabase] RuntimeException occurred during SQL query! Message: " + e.getMessage());
            throw new RuntimeException();
        }
    }
    public static int update(String sql, Object... params) {
        try {
            return SBDatabase.getInstance().getExecutor().update(sql, params);
        } catch (RuntimeException e) {
            SBLogger.err("[SBDatabase] RuntimeException occurred during SQL update! Message: " + e.getMessage());
            throw new RuntimeException();
        }
    }
    public static long insert(String sql, Object... params) {
        try {
            return SBDatabase.getInstance().getExecutor().insert(sql, params);
        } catch (RuntimeException e) {
            SBLogger.err("[SBDatabase] RuntimeException occurred during SQL insert! Message: " + e.getMessage());
            throw new RuntimeException();
        }
    }

    public static List<String> getAllColumnValues(String table, String column) {
        return SBDatabase.getInstance().getExecutor().getAllColumnValues(table, column).join();
    }

    @Override
    public void close() {
        SBLogger.info("[Database] <red>Closing database connection!");
        pool.close();
    }
}
