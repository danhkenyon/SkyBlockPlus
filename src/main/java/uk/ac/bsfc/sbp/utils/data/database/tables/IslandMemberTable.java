package uk.ac.bsfc.sbp.utils.data.database.tables;

import uk.ac.bsfc.sbp.core.skyblock.Member;
import uk.ac.bsfc.sbp.utils.data.database.DatabaseTable;

import java.util.Map;

/**
 * Represents a database table dedicated to managing island membership information.
 * This table stores information about members associated with specific islands,
 * their unique identifiers, and their roles within the island.
 */
public class IslandMemberTable extends DatabaseTable<Member> {
    protected IslandMemberTable(String tableName) {
        super(tableName);
    }

    @Override
    public Member mapRow(Map<String, Object> row) {
        return null;
    }

    @Override
    public void ensureTableExists() {

    }
    /*
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
            Member member = Member.of((SBPlayer) SBPlayer.from(uuid), rank);

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
        super.database.getExecutor().update("Island Member Table Creation",
                "CREATE TABLE IF NOT EXISTS " + this.getTableName() + " (" +
                        "player_uuid CHAR(36) PRIMARY KEY," +
                        "island_id CHAR(36) NOT NULL DEFAULT '"+UNKNOWN_ISLAND_UUID+"'," +
                        "rank ENUM('LEADER','CO_LEADER','OFFICER','MEMBER','RECRUIT') NOT NULL DEFAULT 'RECRUIT'" +
                ");"
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
                member.getUniqueID().toString(),
                member.getIslandId(),
                member.getRank().name()
        );
    }
    public void deleteIslandMembers(UUID id) {
        super.database.getExecutor().update("Island Member Deletion",
                "DELETE FROM " + this.getTableName() + " WHERE island_id = ?",
                id
        );
        SBLogger.info("[IslandMemberTable] <red>Deleted all members for island ID <aqua>" + id);
    }

    public List<Member> getIslandMembers(UUID id) {
        return getRows("island_id", id);
    }
    public boolean exists(UUID id) {
        return super.exists("player_uuid", id.toString());
    }

     */
}