package uk.ac.bsfc.sbp.core.skyblock;

/**
 * Represents the different ranks that a member of an island can hold.
 * These ranks define a member's role and permissions within the island hierarchy.
 */
public enum Rank {
    LEADER,
    CO_LEADER,
    OFFICER,
    MEMBER,
    RECRUIT;

    public static String displayName(Rank rank) {
        return switch (rank) {
            case LEADER -> "<gold><b>Leader";
            case CO_LEADER -> "<aqua><b>Co-Leader";
            case OFFICER -> "<green><b>Officer";
            case MEMBER -> "<gray><b>Member";
            case RECRUIT -> "<white><b>Recruit";
        };
    }
}
