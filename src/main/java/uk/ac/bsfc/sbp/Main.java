package uk.ac.bsfc.sbp;

import org.bukkit.Bukkit;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import uk.ac.bsfc.sbp.core.spawners.SpawnerDAO;
import uk.ac.bsfc.sbp.core.spawners.SpawnerStackManager;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.analytics.AnalyticsRunnable;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

    public static Boolean analyticsOptIn = null;

    @Override
    public void onLoad() {
        instance = this;
        stackManager = new StackManager();
        this.setNaggable(SBConfig.getBoolean("bukkit-logging"));
        SBLogger.info("<green>Loading plugin...");
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();

        Boolean optIn = (Boolean) getConfig().get("analyticsOptIn", null);

        if (optIn == null) {
            SBLogger.raw("<green>-------------------------------------------------");
            SBLogger.raw("<red>Analytics opt-in not set. Please type 'true' or 'false' in the console to continue:");
            SBLogger.raw("<red><b>Note: This is completely anonymous and not required to use the plugin.</b>");
            SBLogger.raw("<green>-------------------------------------------------");
            new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    while (true) {
                        String input = reader.readLine();
                        if (input == null) continue;
                        input = input.trim().toLowerCase();
                        if (input.equals("true") || input.equals("false")) {
                            Boolean choice = Boolean.parseBoolean(input);
                            analyticsOptIn = choice;
                            getConfig().set("analyticsOptIn", choice);
                            saveConfig();
                            SBLogger.raw("<green>You chose: " + choice);
                            break;
                        } else {
                            getLogger().info("<red>Invalid input! Type 'true' or 'false'.");
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            return;
        } else {
            analyticsOptIn = optIn;
        }

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

        if (analyticsOptIn){
            new AnalyticsRunnable(20 * (60 * 5)).start();
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
