package uk.ac.bsfc.sbp.core.commands.subcommands;

import uk.ac.bsfc.sbp.core.skyblock.Island;
import uk.ac.bsfc.sbp.core.skyblock.Member;
import uk.ac.bsfc.sbp.core.skyblock.Rank;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandMemberTable;
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
        Member member = IslandMemberTable.getInstance().getRow("player_uuid", user.uuid());

        if (IslandMemberTable.getInstance().exists(user.uuid())) {
            member.sendMessage("&cYou already have an island! &7(&b" + member.getIsland().getName() + "&7)");
            return;
        }
        if (member == null) {
            member = Member.of((SBPlayer) user, Rank.LEADER);
        }

        member.setRank(Rank.LEADER);
        Island island = Island.createIsland(member);
        member.sendMessage("&aSuccessfully created island! &7(Island ID: &b" + island.getId() + "&7)");
    }
}
