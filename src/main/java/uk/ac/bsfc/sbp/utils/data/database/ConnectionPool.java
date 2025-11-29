package uk.ac.bsfc.sbp.utils.data.database;

import com.zaxxer.hikari.HikariDataSource;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.data.database.providers.DatabaseProvider;
import uk.ac.bsfc.sbp.utils.data.database.providers.DatabaseProviderFactory;

import java.sql.Connection;
import java.sql.SQLException;

public final class ConnectionPool implements AutoCloseable {
    private final HikariDataSource source;

    private ConnectionPool(DatabaseConfig config) {
        DatabaseProvider provider = DatabaseProviderFactory.create(config);
        this.source = new HikariDataSource(provider.createConfig());

        SBLogger.info("[Database] <green>Connected using <aqua>" + provider.getName());
    }

    private static ConnectionPool INSTANCE;
    public static ConnectionPool getInstance(DatabaseConfig config) {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionPool(config);
        }
        return INSTANCE;
    }
    public static ConnectionPool getInstance(boolean createNew, DatabaseConfig config) {
        if (createNew) {
            return new ConnectionPool(config);
        }
        return getInstance(config);
    }
    public static ConnectionPool getInstance() {
        return getInstance(DatabaseConfig.getInstance());
    }

    public Connection getConnection() throws SQLException {
        return source.getConnection();
    }
    public boolean isConnected() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public void close() {
        if (source != null && !source.isClosed()) {
            source.close();
        }
    }
}