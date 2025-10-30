package uk.ac.bsfc.sbp.core.commands.subcommands;

import uk.ac.bsfc.sbp.core.skyblock.Island;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandMemberTable;

public class DeleteSubcommand {
    public static void execute(SBCommand cmd) {
        var user = cmd.getUser();
        var member = IslandMemberTable.getInstance().getRow("player_uuid", user.uuid());
        Island island = member.getIsland();
        if (island == null) {
            member.sendMessage("&cYou do not have an island to delete!");
            return;
        }
        if (!island.getLeader().uuid().equals(member.uuid())) {
            member.sendMessage("&cOnly the island leader can delete the island.");
            return;
        }

        island.delete();
        member.sendMessage("&aSuccessfully deleted your island.");
    }
}
