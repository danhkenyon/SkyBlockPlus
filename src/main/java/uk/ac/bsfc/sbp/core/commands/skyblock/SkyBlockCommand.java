package uk.ac.bsfc.sbp.core.commands.skyblock;

import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.data.SBConfig;

import java.util.List;

public class SkyBlockCommand extends SBCommand {
    public SkyBlockCommand() {
        super();
        super.name("skyblock");
        super.description("Main SkyBlock command.");
        super.usage("/skyblock <subcommand>");
        super.permission(null);

        super.aliases("sb");
    }

    public @Override void execute() {
        if (args.length == 0) {
            getUser().sendMessage(SBConstants.SERVER_INFO);
            return;
        }

        if (args[0].equalsIgnoreCase("admin")) {
            if (args[1].equalsIgnoreCase("reload")) {
                SBConfig.reload();
//                SBDatabase.reload();

                user.sendMessage("{messages.prefix} <green>Reloaded plugin data.");
            }
        }
    }

    public @Override List<String> suggestions(int index) {
        return switch (index) {
            case 0 -> List.of("admin");
            case 1 -> switch (args[0].toLowerCase()) {
                            case "admin" -> List.of("reload");
                            default -> List.of();
                        };
            default -> List.of();
        };
    }
}
