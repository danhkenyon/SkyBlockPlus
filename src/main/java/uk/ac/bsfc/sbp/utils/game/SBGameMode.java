package uk.ac.bsfc.sbp.utils.game;

/**
 * Represents a custom enumeration of game modes in a server environment,
 * providing mappings to the standard Bukkit {@link org.bukkit.GameMode}.
 *
 * This enum defines four game modes:
 * - SURVIVAL: A mode where players must gather resources, manage health, and survive.
 * - CREATIVE: A mode where players have unlimited resources and flying ability.
 * - ADVENTURE: A mode for exploration with restricted block breaking and interaction.
 * - SPECTATOR: A mode for observing the game without interacting with the environment.
 */
public enum SBGameMode {
    SURVIVAL,
    CREATIVE,
    ADVENTURE,
    SPECTATOR;

    public org.bukkit.GameMode getGameMode() {
        return org.bukkit.GameMode.valueOf(this.name());
    }

    @Override
    public String toString() {
        char[] arr = this.name().toLowerCase().toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);
        return new String(arr);
    }
}
