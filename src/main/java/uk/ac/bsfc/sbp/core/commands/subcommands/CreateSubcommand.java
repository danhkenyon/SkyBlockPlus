package uk.ac.bsfc.sbp.core.commands.subcommands;

import uk.ac.bsfc.sbp.core.Island;
import uk.ac.bsfc.sbp.core.Member;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.user.SBConsole;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;
import uk.ac.bsfc.sbp.utils.user.SBUser;

public class CreateSubcommand {
    public static void execute(SBCommand cmd) {
        SBUser user = cmd.getUser();

        if (user instanceof SBConsole) {
            SBLogger.err("Command cannot be ran by CONSOLE.");
        }

        assert user instanceof SBPlayer;
        Member member = Member.of((SBPlayer) user);

        if (member.getIsland() != null) {
            member.sendMessage("&cYou already have an island! &7(&b" + member.getIsland().getName() + "&7)");
            return;
        }
        Island island = Island.createIsland(member);
        member.sendMessage("&aSuccessfully created island! &7(Island ID: &b" + island.getId() + "&7)");
    }
}
