package uk.ac.bsfc.sbp.utils.data;

import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.data.database.ConnectionPool;
import uk.ac.bsfc.sbp.utils.data.database.DatabaseConfig;
import uk.ac.bsfc.sbp.utils.data.database.QueryExecutor;

import java.util.List;
import java.util.Map;

/**
 * SBDatabase is a singleton class that provides a simplified interface for interacting
 * with a database. It manages database connections using a ConnectionPool and executes
 * queries through a QueryExecutor. The class supports various SQL operations, including
 * querying, updating, and inserting data.
 *
 * It ensures thread-safe access to the database and maintains a single instance of the
 * connection pool and query executor.
 *
 * Features:
 * - Singleton pattern for centralized database management.
 * - Easy retrieval of the connection pool and query executor.
 * - Asynchronous query execution with synchronous behavior for results.
 * - Automatic resource management upon closure.
 *
 * Methods:
 * - `getInstance()`: Retrieves the singleton instance of SBDatabase.
 * - `getInstance(boolean createNew)`: Retrieves the singleton instance or creates a new one if specified.
 * - `reload()`: Reloads and recreates the database connection.
 * - `query(String sql, Object... params)`: Executes a SQL query and returns the result as a list of maps.
 * - `update(String sql, Object... params)`: Executes a SQL update and returns the number of affected rows.
 * - `insert(String sql, Object... params)`: Executes a SQL insert and returns the generated ID.
 * - `getAllColumnValues(String table, String column)`: Retrieves all unique values for a specified column in a table.
 * - `isConnected()`: Checks if the database connection is active.
 * - `close()`: Closes the database connection and releases resources.
 */
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
