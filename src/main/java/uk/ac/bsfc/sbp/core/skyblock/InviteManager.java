package uk.ac.bsfc.sbp.core.skyblock;

import org.bukkit.Bukkit;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandMemberTable;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandTable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InviteManager {
    private final Map<UUID, List<Member>> invites = new ConcurrentHashMap<>();

    private static InviteManager INSTANCE;
    public static InviteManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InviteManager();
        }
        return INSTANCE;
    }

    public void sendInvite(Island island, Member member) {
        if (IslandTable.getInstance().getRow("id", member.getIslandId()) != null) {
            return;
        }

        invites.computeIfAbsent(island.uuid(), id -> new ArrayList<>()).add(member);
        SBLogger.info("&b" + member.username() + " &ahas been invited to island &b" + island.name()+"&a.");

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (this.isInvited(island, member)) {
                this.revokeInvite(island, member);

                member.sendMessage("&cInvite from &b" + island.name() + "&c expired!");
                SBLogger.info("&7(&b" + member.username() + "&7) &cIsland invite from &b" + island.name() + "&c expired.");
            }
        }, 20L * 60);
    }
    public void revokeInvite(Island island, Member member) {
        List<Member> members = invites.get(island.uuid());
        if (members != null) {
            members.remove(member);
            if (members.isEmpty()) {
                invites.remove(island.uuid());
            }
        }
    }

    public boolean isInvited(Island island, Member member) {
        List<Member> members = invites.get(island.uuid());
        return members != null && members.contains(member);
    }

    public void acceptInvite(Island island, Member member) {
        if (this.isInvited(island, member)) {
            this.revokeInvite(island, member);
            island.addMember(member);
            member.sendMessage("&aAccepted invite from &b" + island.name() + "&a!");
            SBLogger.info("&7(&b" + member.username() + "&7) &aJoined island &b" + island.name());
        } else {
            member.sendMessage("&cYou don't have a invite from that island!");
        }
    }
    public void denyInvite(Island island, Member member) {
        if (this.isInvited(island, member)) {
            this.revokeInvite(island, member);
            member.sendMessage("&cYou denied the invite from &b" + island.name());
            SBLogger.info("&7(&b" + member.username() + "&7) &aDeclined invite from &b" + island.name());
        } else {
            member.sendMessage("&cYou don't have a invite from that island!");
        }
    }
}