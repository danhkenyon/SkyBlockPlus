package uk.ac.bsfc.sbp.core;

import org.bukkit.Location;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.skyblock.IslandUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Island {
    private final int size = SBConstants.Island.BASE_ISLAND_SIZE;

    private final long id;
    private String name;
    private final List<Member> members;

    private final Region region;

    protected Island(String name, List<Member> members) {
        this.id = IslandUtils.generateId();
        this.name = name;
        this.members = members;
        this.region = new Region(this, IslandUtils.nextLocation());

        members.forEach(m -> m.setIsland(this));
        IslandUtils.getInstance().registerIsland(this);
    }
    protected Island(Member member) {
        this(SBConstants.Island.DEFAULT_ISLAND_NAME, new ArrayList<>(){{
            add(member);
        }});
    }

    public static Island createIsland(String name, List<Member> members) {
        return new Island(name, members);
    }
    public static Island createIsland(Member member) {
        return new Island(member);
    }

    public long getId() {
        return id;
    }
    public int getSize() {
        return size;
    }
    public String getName() {
        return name;
    }
    public List<Member> getMembers() {
        return members;
    }
    public Member getLeader() {
        for (Member member : members) {
            if (member.getRank() == Rank.LEADER) {
                return member;
            }
        }
        return null;
    }
    public Member getMember(UUID uuid) {
        for (Member member : members) {
            if (member.uuid().equals(uuid)) {
                return member;
            }
        }
        return null;
    }
    public Member getMember(String name) {
        for (Member member : members) {
            if (member.username().equalsIgnoreCase(name)) {
                return member;
            }
        }
        return null;
    }
    public Region getRegion() {
        return region;
    }
    public boolean hasMember(UUID uuid) {
        for (Member member : members) {
            if (member.uuid().equals(uuid)) {
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
    }
    public void removeMember(Member member) {
        this.members.remove(member);
    }

    @Override
    public String toString() {
        return "Island[id="+this.getId()+", name="+this.getName()+", members=" + this.getMembers() + "]";
    }
}
