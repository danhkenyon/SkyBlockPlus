package uk.ac.bsfc.sbp.core.commands.subcommands;

import uk.ac.bsfc.sbp.core.skyblock.Island;
import uk.ac.bsfc.sbp.core.skyblock.Member;
import uk.ac.bsfc.sbp.utils.command.SBCommand;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandMemberTable;

import java.util.ArrayList;
import java.util.List;

public class InfoSubcommand {
    public static void execute(SBCommand cmd) {
        var user = cmd.getUser();
        Member member = IslandMemberTable.getInstance().getRow("player_uuid", user.uuid());

        if (member == null) {
            user.sendMessage("&cYou do not have an island! Use &e&l/island create &cto make one.");
            return;
        }
        Island island = member.getIsland();
        if (island == null) {
            member.sendMessage("&cYou do not have an island! Use &e&l/island create &cto make one.");
            return;
        }

        String[] members = new String[island.getMembers().size()];
        for (int i = 0; i < island.getMembers().size(); i++) {
            members[i] = ("&7 - &b" + island.getMembers().get(i).username());
        }

        List<String> info = new ArrayList<>() {{
            add("&6&l=== &e&lIsland Info &6&l===");
            add("&e&lName: &b" + island.getName());
            add("&e&lIsland ID: &b" + island.getId());
            add("&e&lLocation: &b" + island.getRegion().getLoc1().toString());
            add("&e&lLeader: &b" + island.getLeader().username());
            add("&e&lMembers: &b" + island.getMembers().size());
            addAll(List.of(members));
        }};

        member.sendMessage(
                true,
                info.toArray(String[]::new)
        );
    }
}
