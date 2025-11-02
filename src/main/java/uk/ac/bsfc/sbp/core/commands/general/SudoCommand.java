package uk.ac.bsfc.sbp.core.commands.general;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.user.SBUser;

import java.util.ArrayList;
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

    public @Override void execute() {
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
    public @Override List<String> suggestions(int index) {
        switch (index) {
            case 0 -> {
                List<String> names = new ArrayList<>(Bukkit.getOnlinePlayers().size() + 1);
                names.add("CONSOLE");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    names.add(player.getName());
                }
                return names;
            }
            case 1 -> {
                return List.of("1", "5", "10", "25", "50", "100");
            }
            case 2 -> {
                return List.of("<command/chat message>");
            }
            default -> {
                return List.of();
            }
        }
    }
}
