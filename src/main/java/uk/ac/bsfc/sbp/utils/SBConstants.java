package uk.ac.bsfc.sbp.utils;

import org.bukkit.Bukkit;
import org.bukkit.World;
import uk.ac.bsfc.sbp.utils.data.SBConfig;

public class SBConstants {
    public static final String DEFAULT_PLUGIN_PREFIX = "&b&lSkyBlock&3&l+ &8&l➤ &r";
    public static final String PLUGIN_PREFIX = SBConfig.getString("messages.plugin-prefix", DEFAULT_PLUGIN_PREFIX);

    public static final String PLUGIN_NAME = "SkyBlockPlus";
    public static final String PLUGIN_TITLE = "SkyBlock+";
    public static final String CONFIG_FILE = "config.yml";

    public static final String DEFAULT_JOIN_MESSAGE = "&e%username% has joined the server.";
    public static final String SERVER_INFO = "SkyBlock+";

    public static final class Island {
        public static final String DEFAULT_ISLAND_NAME = "%leader%'s Island";
        public static final int BASE_ISLAND_SIZE = 500;
        public static final World ISLAND_WORLD = Bukkit.getWorld("world");
    }

    public static final class Database {
        public static final String DATABASE_DRIVER_NAME = "org.mariadb.jdbc.Driver";
        public static final String DEFAULT_DATABASE_URL = "jdbc:mariadb://localhost:3306/sbp";
        public static final String DEFAULT_DATABASE_USER = "root";
        public static final String DEFAULT_DATABASE_PASSWORD = "admin";
        public static final int DEFAULT_DATABASE_POOL_SIZE = 20;

        public static final String TABLE_USERS = "users";
        public static final String TABLE_ISLANDS = "islands";
    }
}
