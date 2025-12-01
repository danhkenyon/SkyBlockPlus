package uk.ac.bsfc.sbp.utils.config;

@ConfigFile("server-config")
public class ServerConfig implements ReloadableConfig {
    public boolean bukkitLogging = false;
    public boolean logTimestamp = false;
    public String logTimestampFormat = "hh:mm:ss";

    @Comment("Database connection setup")
    public Database database = new Database();

    public static class Database {
        @Comment("Type of database to use. E.g. SQLite, MySQL, MariaDB")
        public String provider = "sqlite";
        @Comment("Maximum number of connections allowed in the pool, again can mostly ignore this")
        public int maxPoolSize = 20;
        @Comment("[SQLite] The file location for the database.")
        public String file = "database.db";

        @Comment("The following is used for remote databases.")
        @Comment("Database connection URL")
        public String url = "jdbc:mariadb://localhost:3306/sbp";
        @Comment("Username and password for remote database connection")
        public String username = "root";
        public String password = "admin";
    }
}
