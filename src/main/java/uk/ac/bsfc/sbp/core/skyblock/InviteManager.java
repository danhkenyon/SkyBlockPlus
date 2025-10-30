package uk.ac.bsfc.sbp.core.skyblock;

import org.bukkit.Bukkit;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.SBLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InviteManager {
    private final Map<Long, List<Member>> invites = new ConcurrentHashMap<>();

    private static InviteManager INSTANCE;
    public static InviteManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new InviteManager();
        }
        return INSTANCE;
    }
    
    public void sendInvite(Island island, Member member) {
        if (member.getIsland() != null) {
            return;
        }

        invites.computeIfAbsent(island.getId(), id -> new ArrayList<>()).add(member);
        SBLogger.info("&b" + member.username() + " &ahas been invited to island &b" + island.getName()+"&a.");

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (this.isInvited(island, member)) {
                this.revokeInvite(island, member);

                member.sendMessage("&cInvite from &b" + island.getName() + "&c expired!");
                SBLogger.info("&7(&b" + member.username() + "&7) &cIsland invite from &b" + island.getName() + "&c expired.");
            }
        }, 20L * 60);
    }
    public void revokeInvite(Island island, Member member) {
        List<Member> members = invites.get(island.getId());
        if (members != null) {
            members.remove(member);
            if (members.isEmpty()) {
                invites.remove(island.getId());
            }
        }
    }
    
    public boolean isInvited(Island island, Member member) {
        List<Member> members = invites.get(island.getId());
        return members != null && members.contains(member);
    }

    public void acceptInvite(Island island, Member member) {
        if (this.isInvited(island, member)) {
            this.revokeInvite(island, member);
            island.addMember(member);
            member.sendMessage("&aAccepted invite from &b" + island.getName() + "&a!");
            SBLogger.info("&7(&b" + member.username() + "&7) &aJoined island &b" + island.getName());
        } else {
            member.sendMessage("&cYou don't have a invite from that island!");
        }
    }
    public void denyInvite(Island island, Member member) {
        if (this.isInvited(island, member)) {
            this.revokeInvite(island, member);
            member.sendMessage("&cYou denied the invite from &b" + island.getName());
            SBLogger.info("&7(&b" + member.username() + "&7) &aDeclined invite from &b" + island.getName());
        } else {
            member.sendMessage("&cYou don't have a invite from that island!");
        }
    }
}
