package uk.ac.bsfc.sbp.core.skyblock;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandTable;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.skyblock.IslandUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an island within the SkyBlock+ system, containing details such as
 * its unique identifier, name, region, and members.
 */
public class Island {
    private final int size = SBConstants.Island.BASE_ISLAND_SIZE;

    private final UUID id;
    private String name;
    private final List<Member> members;

    private final IslandRegion region;

    protected Island(UUID id, String name, SBLocation loc1, List<Member> members) {
        this.id = id;
        this.name = name;
        this.region = IslandRegion.of(loc1);
        this.members = members;
    }
    protected Island(String name, List<Member> members) {
        this.name = name;
        this.members = members;
        this.region = new IslandRegion(IslandUtils.nextLocation());

        this.id = IslandTable.getInstance().insert(this, region.getLoc1().toBukkit());

        IslandUtils.getInstance().registerIsland(this);
        members.forEach(m -> m.setIsland(this));

        Objects.requireNonNull(Bukkit.getWorld(region.getLoc1().getWorld().toBukkit().getUID())).
                getBlockAt(region.getLoc1().toBukkit()).setType(Material.BEDROCK);
        // paste island
    }
    protected Island(Member member) {
        this(SBConstants.Island.DEFAULT_ISLAND_NAME.replace("%leader%", member.getName()), new ArrayList<>(){{
            add(member);
        }});
    }

    public static Island createIsland(String name, List<Member> members) {
        return new Island(name, members);
    }
    public static Island createIsland(Member member) {
        return new Island(member);
    }
    public static Island createIsland(UUID id, String name, SBLocation loc1, List<Member> members) {
        return new Island(id, name, loc1, members);
    }

    public UUID uuid() {
        return id;
    }
    public int size() {
        return size;
    }
    public String name() {
        return name;
    }
    public List<Member> members() {
        return members;
    }
    public Member leader() {
        for (Member member : members) {
            if (member.getRank() == Rank.LEADER) {
                return member;
            }
        }
        return null;
    }
    public Member getMember(UUID uuid) {
        for (Member member : members) {
            if (member.getUniqueID().equals(uuid)) {
                return member;
            }
        }
        return null;
    }
    public Member getMember(String name) {
        for (Member member : members) {
            if (member.getName().equalsIgnoreCase(name)) {
                return member;
            }
        }
        return null;
    }
    public IslandRegion region() {
        return region;
    }
    public boolean hasMember(UUID uuid) {
        for (Member member : members) {
            if (member.getUniqueID().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void addMember(Member member) {
        this.members.add(member);
        member.setIsland(this);
    }
    public void removeMember(Member member) {
        this.members.remove(member);
        member.setIslandWithoutSave(SBConstants.Island.UNKNOWN_ISLAND_UUID);
        member.setRank(Rank.RECRUIT);
    }

    public void delete() {
        IslandTable.getInstance().delete(this.id);
        IslandUtils.getInstance().getIslands().remove(this.id);
        // TODO: Remove island schematic
    }

    @Override
    public String toString() {
        return "Island[id="+this.uuid()+", name="+this.name()+", members=" + this.members() + "]";
    }
}
