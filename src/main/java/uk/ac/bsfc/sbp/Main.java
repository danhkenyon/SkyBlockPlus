package uk.ac.bsfc.sbp;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.command.SBCommandHandler;
import uk.ac.bsfc.sbp.utils.data.SBConfig;
import uk.ac.bsfc.sbp.utils.data.SBDatabase;
import uk.ac.bsfc.sbp.utils.data.database.DatabaseTable;
import uk.ac.bsfc.sbp.utils.event.SBEventRegister;
import uk.ac.bsfc.sbp.utils.location.WorldManager;
import uk.ac.bsfc.sbp.utils.skyblock.IslandUtils;

public final class Main extends JavaPlugin {
    private static Main instance;
    public static Main getInstance() {
        return instance;
    }

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

            WorldManager.getInstance();

            SBLogger.info("<green>Plugin enabled!");
        } catch (Exception e) {
            SBLogger.err(e.getMessage());
            SBLogger.info("<red>Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        SBLogger.info("<red>Plugin Disabled!");
    }

    public SBCommandHandler getCommandHandler() {
        return commandHandler;
    }
    public SBEventRegister getEventRegister() {
        return eventRegister;
    }
}