package uk.ac.bsfc.sbp.utils.user;

import java.util.UUID;

public class SBConsole extends SBUser {
    protected SBConsole() {
        super("CONSOLE", new UUID(0, 0), true);
    }
}
