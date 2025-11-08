package uk.ac.bsfc.sbp.utils.game;

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
