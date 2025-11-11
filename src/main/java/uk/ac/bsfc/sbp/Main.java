package uk.ac.bsfc.sbp;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.command.SBCommandHandler;
import uk.ac.bsfc.sbp.utils.data.JSON;
import uk.ac.bsfc.sbp.utils.data.SBConfig;
import uk.ac.bsfc.sbp.utils.data.database.DatabaseTable;
import uk.ac.bsfc.sbp.utils.event.SBEventRegister;
import uk.ac.bsfc.sbp.utils.menus.SBItem;
import uk.ac.bsfc.sbp.utils.menus.SBItemListener;
import uk.ac.bsfc.sbp.utils.skyblock.IslandUtils;

/**
 * Represents the main class of the plugin extending the JavaPlugin framework.
 *
 * This class acts as the entry point of the plugin and manages its lifecycle events such as loading,
 * enabling, and disabling. It provides access to essential components such as command handling,
 * event registering, and the global PersistentDataContainer.
 *
 * Features:
 * - Singleton access via the static getInstance() method.
 * - Loads plugin configuration and initializes necessary dependencies during the onLoad and onEnable phases.
 * - Handles proper shutdown logic in the onDisable phase to ensure clean plugin termination.
 * - Provides utility methods to retrieve command and event handlers, allowing external classes
 *   to interact with these core components of the plugin.
 *
 * Lifecycle:
 * - onLoad: Pre-initialization logic runs before activating the plugin, such as preloading APIs and setting configurations.
 * - onEnable: Primary initialization including command registration, event setup, and dependency initialization.
 * - onDisable: Cleans up resources and disables the plugin safely if necessary.
 */
public final class Main extends JavaPlugin {
    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

    private PersistentDataContainer globalContainer;
    private SBCommandHandler commandHandler;
    private SBEventRegister eventRegister;

    @Override
    public void onLoad() {
        instance = this;
        this.setNaggable(SBConfig.getBoolean("bukkit-logging"));
        SBLogger.info("<green>Loading plugin...");

        if (!NBT.preloadApi()) {
            SBLogger.warn("NBT-API wasn't initialized properly, disabling the plugin");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
    }

    @Override
    public void onEnable() {
        try {
            commandHandler = SBCommandHandler.getInstance();
            commandHandler.register();
            eventRegister = SBEventRegister.getInstance();
            eventRegister.register();

            DatabaseTable.getAllTables().forEach(DatabaseTable::ensureTableExists);
            IslandUtils.getInstance().init();

            SBItem.loadRegistry();
            SBItemListener.register();

            assert Bukkit.getWorlds().getFirst() != null;
            globalContainer = Bukkit.getWorlds().getFirst().getPersistentDataContainer();

            SBLogger.info("<green>Plugin enabled!");
        } catch (Exception e) {
            SBLogger.err(e.getMessage());
            SBLogger.info("<red>Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        SBItem.saveRegistry();
        SBLogger.info("<red>Plugin Disabled!");
    }

    public SBCommandHandler getCommandHandler() {
        return commandHandler;
    }
    public SBEventRegister getEventRegister() {
        return eventRegister;
    }
    public PersistentDataContainer getGlobalContainer(){
        return globalContainer;
    }
}