package uk.ac.bsfc.sbp.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.data.UserDatabase;
import uk.ac.bsfc.sbp.utils.user.SBUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SudoCommand extends SBCommand {
    public SudoCommand() {
        super();
        super.name("sudo");
        super.description("Execute a command as another user.");
        super.usage("/sudo <user> [amount] <command>");
        super.permission("sbp.command.sudo");

        super.aliases("sudo");
    }

    @Override
    public void execute() {
        if (args.length < 2) {
            getUser().sendMessage("&cUsage: &f" + usage());
            return;
        }
        String targetName = args[0];
        SBUser target;

        if (targetName.equalsIgnoreCase("CONSOLE")) {
            target = SBUser.from("CONSOLE");
        } else {
            Player targetPlayer = Bukkit.getPlayerExact(targetName);
            if (targetPlayer == null) {
                getUser().sendMessage("&cUser &f" + targetName + " &cis not online!");
                return;
            }
            target = SBUser.from(targetPlayer.getUniqueId());
        }
        int amt = 1;
        int commandStartIndex = 1;

        if (args.length > 2) {
            try {
                amt = Integer.parseInt(args[1]);
                commandStartIndex = 2;
            } catch (NumberFormatException ignored) {
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = commandStartIndex; i < args.length; i++) {
            sb.append(args[i]).append(" ");
        }
        String command = sb.toString().trim();

        if (command.isEmpty()) {
            super.getUser().sendMessage("&cYou must specify a command or chat message to execute!");
            return;
        }

        target.sudo(getUser(), command, amt);
    }

    @Override
    public List<String> tabComplete() {
        List<String> suggestions = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            suggestions.add(p.getName());
        }
        suggestions.add("CONSOLE");
        return suggestions;
    }
}
