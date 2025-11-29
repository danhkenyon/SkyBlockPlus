package uk.ac.bsfc.sbp.utils.data.database;

import uk.ac.bsfc.sbp.utils.data.SBConfig;

public final class DatabaseConfig {
    private final DatabaseType type;
    private final String driver;
    private final String url;
    private final String user;
    private final String password;
    private final int maxPoolSize;

    private DatabaseConfig(DatabaseType type,
                           String driver,
                           String url,
                           String user,
                           String password,
                           int maxPoolSize) {
        this.type = type;
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
    }

    private static DatabaseConfig INSTANCE;
    public static DatabaseConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = getConfig();
        }
        return INSTANCE;
    }

    public DatabaseType getType() { return type; }
    public String getDriver() { return driver; }
    public String getUrl() { return url; }
    public String getUser() { return user; }
    public String getPassword() { return password; }
    public int getMaxPoolSize() { return maxPoolSize; }

    public static DatabaseConfig getConfig() {
        DatabaseType type = DatabaseType.fromString(
                SBConfig.getString("database.type", "sqlite")
        );

        return switch (type) {
            case SQLITE -> new DatabaseConfig(
                    type,
                    type.getDriver(),
                    "jdbc:sqlite:" + SBConfig.getString("database.file", "database.db"),
                    "",
                    "",
                    SBConfig.getInt("database.maxPoolSize", 10)
            );
            case MARIADB, MYSQL, POSTGRES -> new DatabaseConfig(
                    type,
                    type.getDriver(),
                    SBConfig.getString("database.url"),
                    SBConfig.getString("database.user"),
                    SBConfig.getString("database.password"),
                    SBConfig.getInt("database.maxPoolSize", 10)
            );
        };
    }
}
