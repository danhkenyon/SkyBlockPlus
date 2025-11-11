package uk.ac.bsfc.sbp.utils.user;

import java.util.UUID;

/**
 * Represents a special implementation of {@link SBUser} dedicated to handling console-related operations.
 * The {@code SBConsole} class defines the console user in the system, utilizing a defined UUID
 * and name to uniquely identify it as the "CONSOLE".
 *
 * The {@code SBConsole} class is intended to be instantiated specifically for cases where
 * the console is treated as a sender or recipient of commands and messages.
 *
 * Internally, the class extends the abstract {@link SBUser}, inheriting its properties and behavior,
 * while configuring itself as representing a console entity.
 *
 * Features of this class:
 * - Uses "CONSOLE" as its name and a fixed UUID of {@code UUID(0, 0)}.
 * - Marks itself as a console user via the {@code console} flag.
 * - Designed to offer system functionality distinct from player users.
 */
public class SBConsole extends SBUser {
    protected SBConsole() {
        super("CONSOLE", new UUID(0, 0), true);
    }
}
