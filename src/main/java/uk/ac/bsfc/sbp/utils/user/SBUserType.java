package uk.ac.bsfc.sbp.utils.user;

/**
 * The SBUserType enum defines the type of user within the system.
 * It categorizes users into predefined roles: UNDEFINED, PLAYER, and CONSOLE.
 */
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
