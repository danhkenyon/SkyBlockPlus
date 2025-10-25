package uk.ac.bsfc.sbp.utils.data.database;

import uk.ac.bsfc.sbp.utils.data.SBConfig;

import static uk.ac.bsfc.sbp.utils.SBConstants.Database.*;

public final class DatabaseConfig {
    private final String driver;
    private final String url;
    private final String user;
    private final String password;
    private final int maxPoolSize;

    private DatabaseConfig(String driver, String url, String user, String password, int maxPoolSize) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
    }

    private static DatabaseConfig INSTANCE;
    public static DatabaseConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = DatabaseConfig.getConfig();
        }
        return INSTANCE;
    }

    public String getDriver() { return driver; }
    public String getUrl() { return url; }
    public String getUser() { return user; }
    public String getPassword() { return password; }
    public int getMaxPoolSize() { return maxPoolSize; }

    public static DatabaseConfig getConfig() {
        return new DatabaseConfig(
                SBConfig.getString("database.driver", DATABASE_DRIVER_NAME),
                SBConfig.getString("database.url", DEFAULT_DATABASE_URL),
                SBConfig.getString("database.user", DEFAULT_DATABASE_USER),
                SBConfig.getString("database.password", DEFAULT_DATABASE_PASSWORD),
                SBConfig.getInt("database.maxPoolSize", DEFAULT_DATABASE_POOL_SIZE)
        );
    }
}