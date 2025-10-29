package uk.ac.bsfc.sbp.core;

import org.jetbrains.annotations.NotNull;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

import javax.annotation.Nullable;
import java.util.UUID;

public class Member extends SBPlayer {
    private @Nullable Island island;
    private Rank rank;

    protected Member(String name, UUID uuid, Rank rank) {
        super(name, uuid);
        this.rank = rank;
    }
    protected Member(String name, UUID uuid) {
        this(name, uuid, Rank.RECRUIT);
    }
    protected Member(SBPlayer player, Rank rank) {
        super(player.username(), player.uuid());
        this.rank = rank;
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
            case CO_LEADER, LEADER -> SBLogger.err("Cannot promote beyond &b" + this.getRank() + "&c. (&f" + super.username() + "&c)");
        }
    }
    public void demote() {
        switch (rank) {
            case LEADER -> SBLogger.err("Cannot demote the island leader. (&f" + super.username() + "&c)");
            case CO_LEADER -> this.setRank(Rank.OFFICER);
            case OFFICER -> this.setRank(Rank.MEMBER);
            case MEMBER -> this.setRank(Rank.RECRUIT);
            case RECRUIT -> SBLogger.err("Cannot demote beyond &b" + this.getRank() + "&c. (&f" + super.username() + "&c)");
        }
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }
    public Rank getRank() {
        return rank;
    }

    public @Nullable Island getIsland() {
        return island;
    }
    public void setIsland(@NotNull Island island) {
        this.island = island;
    }
}
