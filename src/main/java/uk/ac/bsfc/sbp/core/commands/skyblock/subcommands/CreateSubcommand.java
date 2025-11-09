package uk.ac.bsfc.sbp.core.commands.skyblock.subcommands;

import uk.ac.bsfc.sbp.core.skyblock.Island;
import uk.ac.bsfc.sbp.core.skyblock.Member;
import uk.ac.bsfc.sbp.core.skyblock.Rank;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandMemberTable;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandTable;
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
        Member member = IslandMemberTable.getInstance().getRow("player_uuid", user.getUniqueID());
        if (member == null) {
            IslandMemberTable.getInstance().insertOrUpdate(Member.of(user.to(SBPlayer.class)));
            member = IslandMemberTable.getInstance().getRow("player_uuid", user.getUniqueID());
        }

        if (member.getIslandId() != null && member.getIslandId() != SBConstants.Island.UNKNOWN_ISLAND_UUID) {
            System.out.println(member.getIslandId());
            Island island = IslandTable.getInstance().getRow("id", member.getIslandId());
            member.sendMessage("<red>You already have an island! <gray>(<aqua>" + island.name() + "<gray>)");
            return;
        }
        member.setRank(Rank.LEADER);

        Island island = Island.createIsland(member);
        member.sendMessage("<green>Successfully created island! <gray>(Island ID: <aqua>" + island.uuid() + "<gray>)");
    }
}
