package uk.ac.bsfc.sbp.core.commands.skyblock.subcommands;

import uk.ac.bsfc.sbp.core.skyblock.InviteManager;
import uk.ac.bsfc.sbp.core.skyblock.Member;
import uk.ac.bsfc.sbp.core.skyblock.Rank;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandMemberTable;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandTable;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

/**
 * The InviteSubcommand class handles various subcommands related to managing
 * invites within the system, including sending, accepting, and declining invites.
 */
public class InviteSubcommand {
    public static void execute(SBCommand cmd) {
        var args = cmd.args();

        if (args[1].equals("accept")) InviteSubcommand.acceptInvite(cmd, args[2]);
        else if (args[1].equals("deny")) InviteSubcommand.declineInvite(cmd, args[2]);
        else if (!args[1].isEmpty()) InviteSubcommand.sendInvite(cmd);
    }

    private static void acceptInvite(SBCommand cmd, String islandName) {
        InviteManager.getInstance().acceptInvite(
                IslandTable.getInstance().getRow("name", islandName),
                Member.of(cmd.getUser().to(SBPlayer.class))
        );
    }
    private static void declineInvite(SBCommand cmd, String islandName) {
        InviteManager.getInstance().denyInvite(
                IslandTable.getInstance().getRow("name", islandName),
                Member.of(cmd.getUser().to(SBPlayer.class))
        );
    }
    private static void sendInvite(SBCommand cmd) {
        String playerName = cmd.args()[1];
        Rank rankName = Rank.valueOf(cmd.args().length >= 3 ? cmd.args()[2] : "RECRUIT");

        InviteManager.getInstance().sendInvite(
                IslandTable.getInstance().getRow(
                        "id",
                        IslandMemberTable.getInstance().getRow("player_uuid", cmd.getUser().getUniqueID()).getIslandId()
                ),
                IslandMemberTable.getInstance().getRow("player_name", playerName)
        );
    }
}
