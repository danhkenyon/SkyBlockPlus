package uk.ac.bsfc.sbp.core.commands.config;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import uk.ac.bsfc.sbp.Main;

public class AnalyticsCommand extends BukkitCommand {

    public AnalyticsCommand() {
        super("analytics");
        this.setDescription("Opt into or out of analytics");
        this.setUsage("/analytics <opt-in|opt-out>");
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        return Main.getInstance().handleAnalyticsCommand(sender, args);
    }
}
