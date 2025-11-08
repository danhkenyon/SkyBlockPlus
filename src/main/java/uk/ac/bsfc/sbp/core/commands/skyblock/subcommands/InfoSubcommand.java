package uk.ac.bsfc.sbp.core.commands.skyblock.subcommands;

import uk.ac.bsfc.sbp.core.skyblock.Island;
import uk.ac.bsfc.sbp.core.skyblock.Member;
import uk.ac.bsfc.sbp.core.skyblock.Rank;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandMemberTable;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandTable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InfoSubcommand {
    public static void execute(SBCommand cmd) {
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
    }
}
