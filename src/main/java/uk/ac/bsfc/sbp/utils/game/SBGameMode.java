package uk.ac.bsfc.sbp.utils.game;

public enum SBGameMode {
    SURVIVAL,
    CREATIVE,
    ADVENTURE,
    SPECTATOR;

    public org.bukkit.GameMode getGameMode() {
        return org.bukkit.GameMode.valueOf(this.name());
    }
}
