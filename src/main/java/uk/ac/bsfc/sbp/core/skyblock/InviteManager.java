package uk.ac.bsfc.sbp.core.skyblock;

/**
 * Manages island invitations and provides functionalities for sending, revoking,
 * accepting, and denying invites between islands and members.
 */
public class InviteManager {
    /*
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
        SBLogger.info("<aqua>" + member.getName() + " <green>has been invited to island <aqua>" + island.name()+"<green>.");

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (this.isInvited(island, member)) {
                this.revokeInvite(island, member);

                member.sendMessage("<red>Invite from <aqua>" + island.name() + "<red> expired!");
                SBLogger.info("<gray>(<aqua>" + member.getName() + "<gray>) <red>Island invite from <aqua>" + island.name() + "<red> expired.");
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
            member.sendMessage("<green>Accepted invite from <aqua>" + island.name() + "<green>!");
            SBLogger.info("<gray>(<aqua>" + member.getName() + "<gray>) <green>Joined island <aqua>" + island.name());
        } else {
            member.sendMessage("<red>You don't have a invite from that island!");
        }
    }
    public void denyInvite(Island island, Member member) {
        if (this.isInvited(island, member)) {
            this.revokeInvite(island, member);
            member.sendMessage("<red>You denied the invite from <aqua>" + island.name());
            SBLogger.info("<gray>(<aqua>" + member.getName() + "<gray>) <green>Declined invite from <aqua>" + island.name());
        } else {
            member.sendMessage("<red>You don't have a invite from that island!");
        }
    }

     */
}

