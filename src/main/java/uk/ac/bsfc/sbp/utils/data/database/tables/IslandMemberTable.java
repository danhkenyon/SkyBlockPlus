package uk.ac.bsfc.sbp.utils.data.database.tables;

import uk.ac.bsfc.sbp.core.skyblock.Island;
import uk.ac.bsfc.sbp.core.skyblock.Member;
import uk.ac.bsfc.sbp.core.skyblock.Rank;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.data.database.DatabaseTable;
import uk.ac.bsfc.sbp.utils.skyblock.IslandUtils;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class IslandMemberTable extends DatabaseTable<Member> {
    public IslandMemberTable() {
        super("island_members", 3);
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
            long islandId = ((Number) row.get("island_id")).longValue();
            UUID uuid = UUID.fromString((String) row.get("player_uuid"));
            String name = (String) row.get("player_name");
            String rankStr = (String) row.get("rank");

            Rank rank = Rank.valueOf(rankStr);
            Member member = Member.of((SBPlayer) SBPlayer.from(uuid, name), rank);
            Island island = IslandUtils.getInstance().getIsland(islandId);
            if (island != null) {
                member.setIsland(island);
                member.setRank(rank);
            }

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
                        "island_id BIGINT NOT NULL," +
                        "player_uuid CHAR(36) NOT NULL," +
                        "player_name VARCHAR(32) NOT NULL," +
                        "rank ENUM('LEADER','CO_LEADER','OFFICER','MEMBER','RECRUIT') NOT NULL DEFAULT 'RECRUIT'," +
                        "PRIMARY KEY (island_id, player_uuid)," +
                        "FOREIGN KEY (island_id) REFERENCES islands(id) ON DELETE CASCADE" +
                        ");"
        );
        SBLogger.info("[IslandMemberTable] &aEnsured table &b" + this.getTableName() + "&a exists.");
    }

    public void insert(long islandId, Member member) {
        if (member == null) {
            SBLogger.err("[IslandMemberTable] Null member provided to insert()");
            return;
        }

        super.database.getExecutor().insert(
                "INSERT INTO " + this.getTableName() +
                        " (island_id, player_uuid, player_name, rank) VALUES (?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE player_name = VALUES(player_name), rank = VALUES(rank);",
                islandId,
                member.uuid().toString(),
                member.username(),
                member.getRank().name()
        );

        SBLogger.info("[IslandMemberTable] &aSaved member &b" + member.username() + "&a for island ID &b" + islandId);
    }
    public void update(long islandId, Member member) {
        if (member == null) {
            SBLogger.err("[IslandMemberTable] Null member provided to update()");
            return;
        }

        super.database.getExecutor().update(
                "UPDATE " + this.getTableName() + " SET player_name = ?, rank = ? WHERE island_id = ? AND player_uuid = ?;",
                member.username(),
                member.getRank().name(),
                islandId,
                member.uuid().toString()
        );

        SBLogger.info("[IslandMemberTable] &aUpdated member &b" + member.username() + "&a for island ID &b" + islandId);
    }

    public List<Member> getIslandMembers(long islandId) {
        return getRows("island_id", islandId);
    }

    public void deleteIslandMembers(long islandId) {
        super.database.getExecutor().update(
                "DELETE FROM " + this.getTableName() + " WHERE island_id = ?",
                islandId
        );
        SBLogger.info("[IslandMemberTable] &cDeleted all members for island ID &b" + islandId);
    }

    public boolean exists(UUID id) {
        return super.exists("player_uuid", id);
    }
    public boolean exists(String name) {
        return super.exists("player_name", name);
    }
}