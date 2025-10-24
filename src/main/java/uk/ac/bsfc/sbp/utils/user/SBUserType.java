package uk.ac.bsfc.sbp.utils.user;

public enum SBUserType {
    UNDEFINED,
    PLAYER,
    CONSOLE;

    public static SBUserType fromClass(String name) {
        if (name.equalsIgnoreCase("SBConsole")) {
            return CONSOLE;
        } else if (name.equalsIgnoreCase("SBPlayer")) {
            return PLAYER;
        } else {
            return UNDEFINED;
        }
    }
}
