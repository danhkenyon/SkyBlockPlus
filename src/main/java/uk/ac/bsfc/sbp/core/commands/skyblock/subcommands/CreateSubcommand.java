package uk.ac.bsfc.sbp.core.commands.skyblock.subcommands;

import uk.ac.bsfc.sbp.utils.command.SBCommand;

/**
 * Represents the CreateSubcommand class that provides the functionality
 * for creating an island in the application based on the specified conditions.
 *
 * This class is responsible for:
 * - Ensuring the command is not executed by a console user.
 * - Verifying if the user exists in the member database and managing membership status.
 * - Assigning the leader rank to the user if creating a new island.
 * - Creating a new island for the user and generating feedback messages.
 */
public class CreateSubcommand {
    public static void execute(SBCommand cmd) {
        /*
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

         */
    }
}
