package uk.ac.bsfc.sbp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.command.SBCommandHandler;
import uk.ac.bsfc.sbp.utils.data.SBConfig;
import uk.ac.bsfc.sbp.utils.data.UserDatabase;
import uk.ac.bsfc.sbp.utils.event.SBEventRegister;

public final class Main extends JavaPlugin {
    private static Main instance;
    public static Main of() {
        return instance;
    }

    private SBCommandHandler commandHandler;
    private SBEventRegister eventRegister;

    @Override
    public void onLoad() {
        instance = this;
        this.setNaggable(SBConfig.getBoolean("bukkit-logging"));
        SBLogger.info("&aLoading plugin...");
    }

    @Override
    public void onEnable() {
        try {
            commandHandler = SBCommandHandler.getInstance();
            commandHandler.register();
            eventRegister = SBEventRegister.getInstance();
            eventRegister.register();

            UserDatabase.ensureTable();

            SBLogger.info("&aPlugin enabled!");
        } catch (Exception e) {
            SBLogger.info("&cDisabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        SBLogger.info("&cPlugin Disabled!");
    }

    public SBCommandHandler getCommandHandler() {
        return commandHandler;
    }
    public SBEventRegister getEventRegister() {
        return eventRegister;
    }
}