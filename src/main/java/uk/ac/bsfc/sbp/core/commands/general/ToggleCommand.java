package uk.ac.bsfc.sbp.core.commands.general;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.NKeys;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.command.SBCommandHandler;

import java.util.List;

public class ToggleCommand extends SBCommand {

    private List<String> commands = SBCommandHandler.getInstance().getCommandNames();

    public ToggleCommand() {
        super();

        this.name("dev/toggle-command");
        this.description("Toggles command.");
        this.permission("sbp.command.toggle");
    }

    @Override
    public void execute() {
        if (args.length != 1) {
            user.sendMessage("<red>Usage: /dev/toggle-command <command-name>");
            return;
        }

        if (!commands.contains(args[0])){
            user.sendMessage("<red>Invalid command name!");
            return;
        }
        if (args[0].equalsIgnoreCase(this.name)){
            user.sendMessage("<red>Nuh uhh cunt!");
            return;
        }

        PersistentDataContainer pdc = Main.getInstance().getGlobalContainer();
        if (!pdc.has(NKeys.getKey(args[0]))){
            pdc.set(NKeys.getKey(args[0]), PersistentDataType.BOOLEAN, true);
        }

        boolean currentValue = pdc.get(NKeys.getKey(args[0]), PersistentDataType.BOOLEAN);

        pdc.set(NKeys.getKey(args[0]), PersistentDataType.BOOLEAN, !currentValue);

        user.toBukkit(CommandSender.class).sendMessage(MiniMessage.miniMessage().deserialize("<green>Command: <gold>" +
                args[0] + " <green>has been toggled to " + ((!currentValue) ? "<green>True" : "<red>False")));

    }

    public @Override List<String> suggestions(int index) {
        return commands;
    }
}
