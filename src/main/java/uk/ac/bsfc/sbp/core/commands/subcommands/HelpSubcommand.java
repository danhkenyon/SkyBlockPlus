package uk.ac.bsfc.sbp.core.commands.subcommands;

import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.user.SBUser;

public class HelpSubcommand {
    public static void execute(SBCommand cmd) {
        SBUser user = cmd.getUser();

        user.sendMessage("");
        user.sendMessage("&6&l=== &e&lSkyBlock Help &6&l===");
        user.sendMessage(
                "&e&l/island",
                "&7- &bIsland creation help."
        );
        user.sendMessage(
                "&e&l/island help",
                "&7- &bDisplays this help message."
        );
        user.sendMessage(
                "&e&l/island create [island_name]",
                "&7- &bCreates a new island."
        );
        user.sendMessage(
                "&e&l/island invite <name> [rank]",
                "&7- &bAdds a user to the island's team."
        );
        user.sendMessage(
                "&e&l/island invite <accept | deny> <island>",
                "&7- &bAllows a user to accept or decline an invite."
        );
        user.sendMessage(
                "&e&l/island kick <name>",
                "&7- &bRemoves a user from the island's team."
        );
        user.sendMessage(
                "&e&l/island spawn",
                "&7- &bTeleports the user to the island's spawn location."
        );
        user.sendMessage(
                "&e&l/island setspawn",
                "&7- &bChanges the island's spawn location to the user's location."
        );
        user.sendMessage("");
    }
}
