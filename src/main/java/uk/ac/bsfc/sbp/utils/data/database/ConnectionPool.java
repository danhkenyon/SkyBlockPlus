package uk.ac.bsfc.sbp.utils.data.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class ConnectionPool implements AutoCloseable {
    private final HikariDataSource source;

    private ConnectionPool(DatabaseConfig config) {
        HikariConfig hikari = new HikariConfig();
        hikari.setJdbcUrl(config.getUrl());
        hikari.setUsername(config.getUser());
        hikari.setPassword(config.getPassword());
        hikari.setDriverClassName(config.getDriver());
        hikari.setMaximumPoolSize(config.getMaxPoolSize());
        hikari.setAutoCommit(false);

        this.source = new HikariDataSource(hikari);
    }

    private static ConnectionPool INSTANCE;
    public static ConnectionPool getInstance(DatabaseConfig config) {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionPool(config);
        }
        return INSTANCE;
    }
    public static ConnectionPool getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConnectionPool(DatabaseConfig.getInstance());
        }
        return INSTANCE;
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
    public @Override void close() {
        if (source != null && !source.isClosed()) {
            source.close();
        }
    }
}