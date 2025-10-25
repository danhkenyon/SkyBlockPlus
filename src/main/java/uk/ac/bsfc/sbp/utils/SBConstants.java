package uk.ac.bsfc.sbp.utils;

public class SBConstants {
    public static final String PLUGIN_NAME = "SkyBlockPlus";
    public static final String PLUGIN_TITLE = "SkyBlock+";
    public static final String CONFIG_FILE = "config.yml";
    public static final String DEFAULT_JOIN_MESSAGE = "&e%username% has joined the server.";

    public static final class Database {
        public static final String DATABASE_DRIVER_NAME = "org.mariadb.jdbc.Driver";
        public static final String DEFAULT_DATABASE_URL = "jdbc:mariadb://localhost:3306/sbp";
        public static final String DEFAULT_DATABASE_USER = "root";
        public static final String DEFAULT_DATABASE_PASSWORD = "admin";
        public static final int DEFAULT_DATABASE_POOL_SIZE = 20;

        public static final String TABLE_USERS = "sbp_users";
    }
}
