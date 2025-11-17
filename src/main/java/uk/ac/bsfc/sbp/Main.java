package uk.ac.bsfc.sbp;

import org.bukkit.Bukkit;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import uk.ac.bsfc.sbp.core.spawners.SpawnerDAO;
import uk.ac.bsfc.sbp.core.spawners.SpawnerStackManager;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.command.SBCommandHandler;
import uk.ac.bsfc.sbp.utils.config.ConfigManager;
import uk.ac.bsfc.sbp.utils.data.SBConfig;
import uk.ac.bsfc.sbp.utils.entity.StackManager;
import uk.ac.bsfc.sbp.utils.event.SBEventRegister;
import uk.ac.bsfc.sbp.utils.menus.SBItem;
import uk.ac.bsfc.sbp.utils.menus.events.SBItemListener;
import uk.ac.bsfc.sbp.utils.menus.events.SBMenuListener;
import uk.ac.bsfc.sbp.utils.skyblock.IslandUtils;
import xyz.xenondevs.invui.InvUI;

public final class Main extends JavaPlugin {
    private static Main instance;
    public static Main getInstance() { return instance; }

    private static StackManager stackManager;
    public static StackManager getStackManager() { return stackManager; }

    private static SpawnerStackManager spawnerManager;
    private static SpawnerDAO spawnerDAO;

    private PersistentDataContainer globalContainer;
    private SBCommandHandler commandHandler;
    private SBEventRegister eventRegister;

    @Override
    public void onLoad() {
        instance = this;
        stackManager = new StackManager();
        this.setNaggable(SBConfig.getBoolean("bukkit-logging"));
        SBLogger.info("<green>Loading plugin...");
    }

    @Override
    public void onEnable() {
        try {
            InvUI.getInstance().setPlugin(this);
            ConfigManager.loadConfigs("uk.ac.bsfc.sbp.utils.config");

            spawnerDAO = new SpawnerDAO();
            spawnerManager = new SpawnerStackManager(spawnerDAO);
            spawnerManager.loadAll(spawnerDAO.loadAllSpawners());

            commandHandler = SBCommandHandler.getInstance();
            commandHandler.register();

            eventRegister = SBEventRegister.getInstance();
            eventRegister.register();

            IslandUtils.getInstance().init();
            SBItem.loadRegistry();
            SBItemListener.register();
            SBMenuListener.register();

            assert Bukkit.getWorlds().getFirst() != null;
            globalContainer = Bukkit.getWorlds().getFirst().getPersistentDataContainer();

            SBLogger.info("<green>Plugin enabled!");
        } catch (Exception e) {
            SBLogger.err("[SBP] Failed to enable plugin: " + e.getMessage());
            SBLogger.info("<red>Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        SBItem.saveRegistry();
        if (spawnerDAO != null) spawnerDAO.close();
        if (stackManager != null) stackManager.killAll();
        SBLogger.info("<red>Plugin Disabled!");
    }

    public SBCommandHandler getCommandHandler() { return commandHandler; }
    public SBEventRegister getEventRegister() { return eventRegister; }
    public PersistentDataContainer getGlobalContainer() { return globalContainer; }

    public static SpawnerStackManager getSpawnerManager() { return spawnerManager; }
    public static SpawnerDAO getSpawnerDAO() { return spawnerDAO; }

    public <T> T getConfig(Class<T> clazz) {
        return ConfigManager.getConfig(clazz);
    }
}
