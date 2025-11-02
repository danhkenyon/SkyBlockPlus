package uk.ac.bsfc.sbp.core.skyblock;

public enum Rank {
    LEADER,
    CO_LEADER,
    OFFICER,
    MEMBER,
    RECRUIT;

    public static String displayName(Rank rank) {
        return switch (rank) {
            case LEADER -> "&6&lLeader";
            case CO_LEADER -> "&b&lCo-Leader";
            case OFFICER -> "&a&lOfficer";
            case MEMBER -> "&7&lMember";
            case RECRUIT -> "&f&lRecruit";
        };
    }
}
