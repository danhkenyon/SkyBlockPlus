package uk.ac.bsfc.sbp.core.commands.skyblock.subcommands;

import uk.ac.bsfc.sbp.core.skyblock.Island;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandMemberTable;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandTable;

/**
 * The DeleteSubcommand class is responsible for implementing the logic to delete an island
 * in the system. It verifies whether the user attempting the action is the leader of the island
 * and handles cases where the island does not exist or the user doesn't have sufficient privileges.
 */
public class DeleteSubcommand {
    public static void execute(SBCommand cmd) {
        var user = cmd.getUser();
        var member = IslandMemberTable.getInstance().getRow("player_uuid", user.getUniqueID());
        Island island = IslandTable.getInstance().getRow("id", member.getIslandId());

        if (island == null) {
            member.sendMessage("<red>You do not have an island to delete!");
            return;
        }
        if (!island.leader().getUniqueID().equals(member.getUniqueID())) {
            member.sendMessage("<red>Only the island leader can delete the island.");
            return;
        }

        island.delete();
        member.sendMessage("<green>Successfully deleted your island.");
    }
}
