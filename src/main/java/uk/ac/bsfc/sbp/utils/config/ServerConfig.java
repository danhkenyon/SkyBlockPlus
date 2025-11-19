package uk.ac.bsfc.sbp.utils.config;

@ConfigFile("server-config")
public class ServerConfig implements ReloadableConfig {
    public boolean bukkitLogging = false;
    public boolean logTimestamp = false;
    public String logTimestampFormat = "hh:mm:ss";

    @Comment("Database connection setup")
    public Database database = new Database();

    @Comment("Test 'update' on the config")
    public char test = 'a';



    public static class Database {
        @Comment("Driver name, you can mostly ignore this")
        public String driver = "com.mysql.jdbc.Driver";
        @Comment("Database connection URL")
        public String url = "jdbc:mariadb://localhost:3306/sbp";
        @Comment("Username and password for database connection")
        public String username = "root";
        public String password = "admin";
        @Comment("Maximum number of connections allowed in the pool, again can mostly ignore this")
        public int maxPoolSize = 20;
    }
}
