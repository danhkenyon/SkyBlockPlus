package uk.ac.bsfc.sbp.core.commands.subcommands;

import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.user.SBUser;

public class HelpSubcommand {
    public static void execute(SBCommand cmd) {
        SBUser user = cmd.getUser();

        user.sendMessage("&6&l=== &e&lSkyBlock Help &6&l===");
        user.sendMessage("&e/island &7| &fIsland creation help.");
        user.sendMessage("&e/island help &7| &fSkyblock help.");
        user.sendMessage("&e/island create <name> &7| &fSkyblock help.");
    }
}
