package uk.ac.bsfc.sbp.utils.data;

import uk.ac.bsfc.sbp.utils.data.database.ConnectionPool;
import uk.ac.bsfc.sbp.utils.data.database.DatabaseConfig;
import uk.ac.bsfc.sbp.utils.data.database.QueryExecutor;

import java.util.List;
import java.util.Map;

public class SBDatabase implements AutoCloseable {
    private final ConnectionPool pool;
    private final QueryExecutor executor;

    private SBDatabase(DatabaseConfig config) {
        this.pool = ConnectionPool.getInstance(config);
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
        return SBDatabase.getInstance().getExecutor().query(sql, params);
    }
    public static int update(String sql, Object... params) {
        return SBDatabase.getInstance().getExecutor().update(sql, params);
    }
    public static long insert(String sql, Object... params) {
        return SBDatabase.getInstance().getExecutor().insert(sql, params);
    }

    public static List<String> getAllColumnValues(String table, String column) {
        return SBDatabase.getInstance().getExecutor().getAllColumnValues(table, column);
    }

    @Override
    public void close() {
        pool.close();
    }
}
