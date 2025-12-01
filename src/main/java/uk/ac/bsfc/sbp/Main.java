package uk.ac.bsfc.sbp;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import uk.ac.bsfc.sbp.core.spawners.SpawnerDAO;
import uk.ac.bsfc.sbp.core.spawners.SpawnerStackManager;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.analytics.AnalyticsRunnable;
import uk.ac.bsfc.sbp.utils.command.SBCommandHandler;
import uk.ac.bsfc.sbp.utils.config.ConfigManager;
import uk.ac.bsfc.sbp.utils.config.FeatureConfig;
import uk.ac.bsfc.sbp.utils.data.SBDatabase;
import uk.ac.bsfc.sbp.utils.data.database.DatabaseTable;
import uk.ac.bsfc.sbp.utils.entity.StackManager;
import uk.ac.bsfc.sbp.utils.event.SBEventRegister;
import uk.ac.bsfc.sbp.utils.location.SBWorldUtils;
import uk.ac.bsfc.sbp.utils.menus.SBItem;
import uk.ac.bsfc.sbp.utils.menus.SBItemData;
import uk.ac.bsfc.sbp.utils.menus.events.SBItemListener;
import uk.ac.bsfc.sbp.utils.menus.events.SBMenuListener;
import uk.ac.bsfc.sbp.utils.skyblock.IslandUtils;
import xyz.xenondevs.invui.InvUI;

public final class Main extends JavaPlugin {
    private static Main instance;
    public static Main getInstance() { return instance; }

    private static StackManager stackManager;
    private static SpawnerStackManager spawnerManager;
    private static SpawnerDAO spawnerDAO;

    private PersistentDataContainer globalContainer;
    private SBCommandHandler commandHandler;
    private SBEventRegister eventRegister;
    private SBWorldUtils worldUtils;

    public static Boolean analyticsOptIn = null;

    @Override
    public void onLoad() {
        instance = this;
        stackManager = new StackManager();
        SBLogger.info("<green>Loading plugin...");
    }

    @Override
    public void onEnable() {
        try {
            InvUI.getInstance().setPlugin(this);
            ConfigManager.loadConfigs("uk.ac.bsfc.sbp.utils.config");

            FeatureConfig featureConfig = getConfig(FeatureConfig.class);
            Boolean optIn = featureConfig.analytics;

            if (optIn == null) {
                enterSetupMode();
                return;
            }

            analyticsOptIn = optIn;

            // ALL START CODE MUST OCCUR AFTER THIS POINT

            /*
             * i swear to god if someone fucking deleted this im gonna die on the spot
             */
            DatabaseTable.getAllTables().forEach(DatabaseTable::ensureTableExists);

            spawnerDAO = new SpawnerDAO();
            spawnerManager = new SpawnerStackManager(spawnerDAO);
            spawnerManager.loadAll(spawnerDAO.loadAllSpawners());

            commandHandler = SBCommandHandler.getInstance();
            commandHandler.register();
            eventRegister = SBEventRegister.getInstance();
            eventRegister.register();

            worldUtils = SBWorldUtils.getInstance();
            worldUtils.loadAllWorlds();

            IslandUtils.getInstance().init();
            SBItem.loadRegistry();
            SBItemListener.register();
            SBMenuListener.register();

            globalContainer = Bukkit.getWorlds().getFirst().getPersistentDataContainer();

            SBLogger.info("<green>Plugin enabled!");
        } catch (Exception e) {
            SBLogger.err("[SBP] Failed to enable plugin: " + e.getMessage());
            SBLogger.info("<red>Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (Boolean.TRUE.equals(analyticsOptIn)) {
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
    public SBWorldUtils getWorldUtils() { return worldUtils; }
    public static StackManager getStackManager() { return stackManager; }
    public static SpawnerStackManager getSpawnerManager() { return spawnerManager; }
    public static SpawnerDAO getSpawnerDAO() { return spawnerDAO; }

    public <T> T getConfig(Class<T> clazz) {
        return ConfigManager.getConfig(clazz);
    }

    private void enterSetupMode() {
        SBLogger.raw("<green>-------------------------------------------------");
        SBLogger.raw("<red>Analytics opt-in is NOT SET!");
        SBLogger.raw("<red>You must choose before SkyBlockPlus can finish loading.");
        SBLogger.raw("<red>The information we collect is used to improve SkyBlockPlus.");
        SBLogger.raw("<red>You can see what information we collect inside /plugins/SkyBlockPlus/features.conf");
        SBLogger.raw("");
        SBLogger.raw("<yellow>Use one of the commands:");
        SBLogger.raw("<yellow>  /analytics opt-in");
        SBLogger.raw("<yellow>  /analytics opt-out");
        SBLogger.raw("");
        SBLogger.raw("<red>After choosing, please RESTART the server.");
        SBLogger.raw("<green>-------------------------------------------------");

        Bukkit.getCommandMap().register("analytics", new BukkitCommand("analytics") {
            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {
                if (!sender.isOp()) {
                    sender.sendMessage("You must be an operator to use this command!");
                    return true;
                }

                if (args.length != 1) {
                    sender.sendMessage("Usage: /analytics <opt-in|opt-out>");
                    return true;
                }

                if (args[0].equalsIgnoreCase("opt-in")) {
                    updateAnalyticsChoice(true, sender);
                    return true;
                }

                if (args[0].equalsIgnoreCase("opt-out")) {
                    updateAnalyticsChoice(false, sender);
                    return true;
                }

                sender.sendMessage("Usage: /analytics <opt-in|opt-out>");
                return true;
            }
        });

        SBLogger.info("<yellow>SkyBlockPlus is now in SETUP MODE. Nothing else will load.");
    }

    private void updateAnalyticsChoice(boolean value, CommandSender sender) {
        FeatureConfig cfg = getConfig(FeatureConfig.class);
        cfg.analytics = value;

        ConfigManager.saveConfig(cfg);

        sender.sendMessage("Analytics has been set to: " + value);
        sender.sendMessage("Please restart the server to finish setup.");

        SBLogger.info("Analytics choice set to " + value + ". Restart required.");
    }
}
