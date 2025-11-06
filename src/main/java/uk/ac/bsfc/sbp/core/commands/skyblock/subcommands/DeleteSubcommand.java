package uk.ac.bsfc.sbp.core.commands.skyblock.subcommands;

import uk.ac.bsfc.sbp.core.skyblock.Island;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandMemberTable;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandTable;

public class DeleteSubcommand {
    public static void execute(SBCommand cmd) {
        var user = cmd.getUser();
        var member = IslandMemberTable.getInstance().getRow("player_uuid", user.getUniqueID());
        Island island = IslandTable.getInstance().getRow("id", member.getIslandId());

        if (island == null) {
            member.sendMessage("&cYou do not have an island to delete!");
            return;
        }
        if (!island.leader().getUniqueID().equals(member.getUniqueID())) {
            member.sendMessage("&cOnly the island leader can delete the island.");
            return;
        }

        island.delete();
        member.sendMessage("&aSuccessfully deleted your island.");
    }
}
