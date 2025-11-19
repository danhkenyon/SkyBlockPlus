package uk.ac.bsfc.sbp.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import uk.ac.bsfc.sbp.utils.data.SBConfig;
import uk.ac.bsfc.sbp.utils.location.SBWorld;
import uk.ac.bsfc.sbp.utils.time.SBTime;

import java.util.UUID;

/**
 * The SBConstants class contains a collection of constant values used across the project for
 * managing plugin-specific configurations, files, database details, schematics, messages, and more.
 * It organizes constants into nested static classes for better modularity and logical grouping.
 */
public final class SBConstants {
    private SBConstants() {}

    public static final MiniMessage mm = MiniMessage.miniMessage();

    public static final String PLUGIN_NAME = "SkyBlockPlus";
    public static final String PLUGIN_TITLE = "SkyBlock+";
    public static final String PLUGIN_FOLDER = "/";
    public static final String CONFIG_FILE = "config.yml";
    public static final String SERVER_INFO = "SkyBlock+";

    public static final class Island {
        public static final String DEFAULT_ISLAND_NAME = "%leader%'s Island";
        public static final int BASE_ISLAND_SIZE = 500;
        public static final String ISLAND_WORLD_PREFIX = SBConfig.getString("islands.world-prefix", "island-");
        public static final int ISLANDS_PER_WORLD = SBConfig.getInt("islands.amount-per-world", 1000);

        public static final UUID UNKNOWN_ISLAND_UUID = new UUID(0, 0);

        public static final String DEFAULT_ISLAND_SCHEMATIC = Schematics.SCHEMATIC_FOLDER + SBConfig.getString("island-schematics.default", "default_island.schem");
        public static final SBWorld ISLAND_WORLD = SBWorld.getWorld("world");
    }

    public static final class Database {
        public static final String DATABASE_DRIVER_NAME = "org.mariadb.jdbc.Driver";
        public static final String DEFAULT_DATABASE_URL = "jdbc:mariadb://localhost:3306/sbp";
        public static final String DEFAULT_DATABASE_USER = "root";
        public static final String DEFAULT_DATABASE_PASSWORD = "admin";
        public static final int DEFAULT_DATABASE_POOL_SIZE = 20;

        public static final String TABLE_USERS = "users";
        public static final String TABLE_ISLANDS = "islands";
        public static final String TABLE_ISLAND_MEMBERS = "island_members";
    }

    public static final class Configuration {
        public static final String CONFIG_FILE_NAME = "config.yml";
        public static final String MESSAGES_CONFIG_FILE_NAME = "config.yml";
    }

    public static final class Schematics {
        public static final String SCHEMATIC_FOLDER = "schematics/";
        public static final String DEFAULT_SCHEMATIC_NAME = SBTime.format("HHmmss_ddMMyyyy") + ".schem";

        public static final boolean ASYNC = SBConfig.getBoolean("schematics.async", true);
    }
    public static final class Messages {
        public static final String DEFAULT_PLUGIN_PREFIX = "<#55FFFF><b>SkyBlock<#00AA00>+ <#555555><b>➤ <reset>";
        public static final String DEFAULT_JOIN_MESSAGE = "<aqua>%username% <yellow>has joined the server.";

        public static final class WorldEdit {
            public static final String ASYNC = "<dark_gray>[<dark_red><bold>ASYNC<dark_gray>";
            public static final String SET_POSITION_1 = "&7Set position 1 to %player.loc%";
            public static final String SET_POSITION_2 = "&7Set position 2 to %player.loc%";
            public static final String CLIPBOARD_COPY = "&aCopied to clipboard.";
            public static final String CLIPBOARD_PASTE = "&aPasted from clipboard at %player.loc%.";
        }
        public static final class Commands {

        }
    }
}
