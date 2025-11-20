package uk.ac.bsfc.sbp.core.commands.skyblock.subcommands;

import uk.ac.bsfc.sbp.utils.command.SBCommand;

/**
 * The InfoSubcommand class provides functionality to display detailed information about
 * a user's island in the application. It retrieves the user's island details, such as
 * the island's name, ID, location, and members, and formats this information into a
 * structured message that is sent to the user.
 *
 * Key Features:
 * - Checks if a user belongs to an island.
 * - Retrieves island and member details from the appropriate data tables.
 * - Formats island information, including name, ID, location, and members, into a list
 *   of messages.
 * - Sends the formatted information directly to the user.
 *
 * Dependencies:
 * - Requires access to the `IslandMemberTable` and `IslandTable` instances for retrieving
 *   member and island data.
 * - Utilizes the `SBCommand` to get context about the user invoking the command.
 * - Uses `SBLogger` for logging member information.
 *
 * Behavior:
 * - If the user is not associated with any island, they are notified with a message
 *   suggesting how to create one.
 * - If the user belongs to an island, the members are sorted by rank and their information
 *   is included in the response.
 *
 */
public class InfoSubcommand {
    public static void execute(SBCommand cmd) {
        /*
        var user = cmd.getUser();
        Member member = IslandMemberTable.getInstance().getRow("player_uuid", user.getUniqueID());
        SBLogger.info(member.toString());
        Island island = IslandTable.getInstance().getRow("id", member.getIslandId());

        if (island == null) {
            member.sendMessage("<red>You do not have an island! Use <yellow><b>/island create <red>to make one.");
            return;
        }

        List<Member> sortedMembers = new ArrayList<>(island.members());
        sortedMembers.sort(Comparator.comparingInt((Member m) -> m.getRank().ordinal()));

        String[] members = new String[sortedMembers.size()];

        for (int i = 0; i < island.members().size(); i++) {
            Member m = sortedMembers.get(i);
            members[i] = "{messages.prefix} <gray> - " + Rank.displayName(m.getRank()) + " <gray>| <aqua>" + m.getName();
        }

        List<String> info = new ArrayList<>() {{
            add("{messages.prefix} <gold><b>=== <yellow><b>Island Info <gold><b>===");
            add("{messages.prefix} <yellow><b>Name: <aqua>" + island.name());
            add("{messages.prefix} <yellow><b>Island ID: <aqua>" + island.uuid().toString().substring(0, 13));
            add("{messages.prefix} <yellow><b>Location: <aqua>Loc[x=" + island.region().getLoc1().x() + ", y=" + island.region().getLoc1().y() + ", z=" + island.region().getLoc1().z() + "]");
            add("{messages.prefix} <yellow><b>Members: <aqua>" + island.members().size());
            addAll(List.of(members));
        }};

        member.sendMessage(
                info.toArray(String[]::new)
        );

         */
    }
}
