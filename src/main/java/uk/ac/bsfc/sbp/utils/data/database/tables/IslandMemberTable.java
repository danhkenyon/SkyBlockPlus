package uk.ac.bsfc.sbp.utils.data.database.tables;

import uk.ac.bsfc.sbp.core.skyblock.Island;
import uk.ac.bsfc.sbp.core.skyblock.Member;
import uk.ac.bsfc.sbp.core.skyblock.Rank;
import uk.ac.bsfc.sbp.utils.SBConstants;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.data.database.DatabaseTable;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

import java.util.*;

import static uk.ac.bsfc.sbp.utils.SBConstants.Island.UNKNOWN_ISLAND_UUID;

public class IslandMemberTable extends DatabaseTable<Member> {
    private static final ThreadLocal<Set<UUID>> loading = ThreadLocal.withInitial(HashSet::new);

    public IslandMemberTable() {
        super(SBConstants.Database.TABLE_ISLAND_MEMBERS, 3);
    }

    private static IslandMemberTable INSTANCE;
    public static IslandMemberTable getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new IslandMemberTable();
        }
        return INSTANCE;
    }

    @Override
    public Member mapRow(Map<String, Object> row) {
        try {
            UUID islandId = UUID.fromString((String) row.get("island_id"));

            UUID uuid = UUID.fromString((String) row.get("player_uuid"));
            String rankStr = (String) row.get("rank");

            Rank rank = Rank.valueOf(rankStr);
            Member member = Member.of(SBPlayer.from(uuid).to(SBPlayer.class), rank);

            if (!UNKNOWN_ISLAND_UUID.equals(islandId)) {
                member.setIsland(islandId);
                member.setRank(rank);
            } else member.setIslandWithoutSave(UNKNOWN_ISLAND_UUID);

            return member;
        } catch (Exception e) {
            SBLogger.err("[IslandMemberTable] Failed to map row: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void ensureTableExists() {
        super.database.getExecutor().update(
                "CREATE TABLE IF NOT EXISTS " + this.getTableName() + " (" +
                        "player_uuid CHAR(36) PRIMARY KEY," +
                        "island_id CHAR(36) NOT NULL DEFAULT '"+UNKNOWN_ISLAND_UUID+"'," +
                        "rank ENUM('LEADER','CO_LEADER','OFFICER','MEMBER','RECRUIT') NOT NULL DEFAULT 'RECRUIT'" +
                        ");" //  PLAYER_UUID | ISLAND_ID | RANK
        );
    }

    public void insertOrUpdate(Member member) {
        if (member == null) {
            SBLogger.err("[IslandMemberTable] Null member provided to insertOrUpdate()");
            return;
        }

        super.database.getExecutor().insert(
                "INSERT INTO " + this.getTableName() +
                        " (player_uuid, island_id, rank) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE island_id = VALUES(island_id), rank = VALUES(rank);",
                member.uuid().toString(),
                member.getIslandId(),
                member.getRank().name()
        );
    }
    public void deleteIslandMembers(UUID id) {
        super.database.getExecutor().update(
                "DELETE FROM " + this.getTableName() + " WHERE island_id = ?",
                id
        );
        SBLogger.info("[IslandMemberTable] &cDeleted all members for island ID &b" + id);
    }

    public List<Member> getIslandMembers(UUID id) {
        return getRows("island_id", id);
    }
    public boolean exists(UUID id) {
        return super.exists("player_uuid", id.toString());
    }
}