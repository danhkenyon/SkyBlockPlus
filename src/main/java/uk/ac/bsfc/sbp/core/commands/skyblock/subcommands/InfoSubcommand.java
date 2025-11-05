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
        Member member = IslandMemberTable.getInstance().getRow("player_uuid", user.uuid());
        SBLogger.info(member.toString());
        Island island = IslandTable.getInstance().getRow("id", member.getIslandId());

        if (island == null) {
            member.sendMessage("&cYou do not have an island! Use &e&l/island create &cto make one.");
            return;
        }

        List<Member> sortedMembers = new ArrayList<>(island.members());
        sortedMembers.sort(Comparator.comparingInt((Member m) -> m.getRank().ordinal()));

        String[] members = new String[sortedMembers.size()];

        for (int i = 0; i < island.members().size(); i++) {
            Member m = sortedMembers.get(i);
            members[i] = "&7 - " + Rank.displayName(m.getRank()) + " &7| &b" + m.username();
        }

        List<String> info = new ArrayList<>() {{
            add("&6&l=== &e&lIsland Info &6&l===");
            add("&e&lName: &b" + island.name());
            add("&e&lIsland ID: &b" + island.uuid().toString().substring(0, 13));
            add("&e&lLocation: &bLoc[x=" + island.region().getLoc1().x() + ", y=" + island.region().getLoc1().y() + ", z=" + island.region().getLoc1().z() + "]");
            add("&e&lMembers: &b" + island.members().size());
            addAll(List.of(members));
        }};

        member.sendMessage(
                true,
                info.toArray(String[]::new)
        );
    }
}
