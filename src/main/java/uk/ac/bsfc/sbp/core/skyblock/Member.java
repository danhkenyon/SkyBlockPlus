package uk.ac.bsfc.sbp.core.skyblock;

import org.jetbrains.annotations.NotNull;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandMemberTable;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

import javax.annotation.Nullable;
import java.util.UUID;

public class Member extends SBPlayer {
    private UUID islandId;
    private Rank rank;

    protected Member(String name, UUID uuid, Rank rank) {
        super(name, uuid);
        this.rank = rank;
        this.islandId = SBConstants.Island.UNKNOWN_ISLAND_UUID;
    }
    protected Member(String name, UUID uuid) {
        this(name, uuid, Rank.RECRUIT);
    }
    protected Member(SBPlayer player, Rank rank) {
        super(player.getName(), player.getUniqueID());
        this.rank = rank;
        this.islandId = SBConstants.Island.UNKNOWN_ISLAND_UUID;
    }
    protected Member(SBPlayer player) {
        this(player, Rank.RECRUIT);
    }

    public static Member of(SBPlayer player) {
        return new Member(player);
    }
    public static Member of(SBPlayer player, Rank rank) {
        return new Member(player, rank);
    }

    public void promote() {
        switch (rank) {
            case RECRUIT -> this.setRank(Rank.MEMBER);
            case MEMBER -> this.setRank(Rank.OFFICER);
            case OFFICER -> this.setRank(Rank.CO_LEADER);
            case CO_LEADER, LEADER -> SBLogger.err("Cannot promote beyond <aqua>" + this.getRank() + "<red>. (<white>" + super.getName() + "<red>)");
        }
    }
    public void demote() {
        switch (rank) {
            case LEADER -> SBLogger.err("Cannot demote the island leader. (<white>" + super.getName() + "<red>)");
            case CO_LEADER -> this.setRank(Rank.OFFICER);
            case OFFICER -> this.setRank(Rank.MEMBER);
            case MEMBER -> this.setRank(Rank.RECRUIT);
            case RECRUIT -> SBLogger.err("Cannot demote beyond <aqua>" + this.getRank() + "<red>. (<white>" + super.getName() + "<red>)");
        }
    }

    public void setRank(Rank rank) {
        this.rank = rank;
        IslandMemberTable.getInstance().insertOrUpdate(this);
    }
    public Rank getRank() {
        return rank;
    }

    public void setIsland(@NotNull Island island) {
        this.islandId = island.uuid();
        IslandMemberTable.getInstance().insertOrUpdate(this);
    }
    public void setIsland(@NotNull UUID id) {
        this.islandId = id;
        IslandMemberTable.getInstance().insertOrUpdate(this);
    }

    public void setIslandWithoutSave(@Nullable Island island) {
        this.islandId = (island == null) ? SBConstants.Island.UNKNOWN_ISLAND_UUID : island.uuid();
    }
    public void setIslandWithoutSave(UUID id) {
        this.islandId = (id == null) ? SBConstants.Island.UNKNOWN_ISLAND_UUID : id;
    }


    public UUID getIslandId() {
        return islandId;
    }

    public void save() {
        IslandMemberTable.getInstance().insertOrUpdate(this);
    }

    @Override
    public String toString() {
        return "Member[name=" + getName() + ", uuid=" + getUniqueID() + ", rank=" + rank + ", islandId=" + islandId + "]";
    }
}
