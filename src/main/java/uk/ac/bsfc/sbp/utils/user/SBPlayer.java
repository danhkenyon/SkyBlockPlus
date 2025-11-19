package uk.ac.bsfc.sbp.utils.user;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.location.SBWorld;
import uk.ac.bsfc.sbp.utils.schematic.Clipboard;
import uk.ac.bsfc.sbp.utils.schematic.ClipboardUtils;
import uk.ac.bsfc.sbp.utils.strings.Placeholder;

import java.util.UUID;

/**
 * Represents a player in the system, extending the SBUser class.
 * Provides additional functionality specific to player management,
 * such as handling game states, world location, flight capabilities, and more.
 */
public class SBPlayer extends SBUser {
    private final Clipboard clipboard;
    private final Placeholder[] playerPlaceholders;

    private String skinUrl;
    private String chatColour;
    private SBGameMode gameMode;
    private boolean allowFlight;
    private boolean flying;
    private SBWorld currentWorld;
    private SBLocation location;

    protected SBPlayer(String name, UUID uuid) {
        super(name, uuid, false);

        if (ClipboardUtils.getInstance().hasClipboard(this)) {
            clipboard = ClipboardUtils.getInstance().getClipboard(this);
        } else {
            clipboard = Clipboard.create();
            ClipboardUtils.getInstance().getClipboards().put(this, clipboard);
        }

        skinUrl = "";
        chatColour = "<white>";
        gameMode = SBGameMode.SURVIVAL;
        allowFlight = false;
        flying = false;

        Player player = super.toBukkit(Player.class);

        SBWorld defaultWorld;
        if (player != null) {
            defaultWorld = SBWorld.getWorld(player.getWorld().getName());
            this.location = SBLocation.of(player.getLocation());
        } else {
            defaultWorld = SBWorld.getWorld("world");
            this.location = SBLocation.of(defaultWorld, 0.5, 0, 0.5);
        }
        this.currentWorld = defaultWorld;

        playerPlaceholders = new Placeholder[]{
                Placeholder.of("%player.world%", this.currentWorld() != null ? this.currentWorld().getName() : "unknown"),
                Placeholder.of("%player.loc%", this.location != null ? this.location.format() : "(0,0,0)"),
                Placeholder.of("%player.chat-colour%", this.chatColour),
                Placeholder.of("%player.gamemode%", this.gameMode.toString()),
                Placeholder.of("%player.allow-flight%", this.allowFlight),
                Placeholder.of("%player.skin-url%", this.skinUrl),
        };
    }

    public String skinUrl() {
        return this.skinUrl;
    }
    public String chatColour() {
        return this.chatColour;
    }
    public SBGameMode gameMode() {
        return this.gameMode;
    }
    public boolean allowFlight() {
        return this.allowFlight;
    }
    public boolean flying() {
        return this.flying;
    }
    public Clipboard clipboard() {
        return this.clipboard;
    }
    public SBWorld currentWorld() {
        return this.currentWorld;
    }
    public SBLocation location() {
        return this.location;
    }

    public void skinUrl(String skinUrl) {
        this.skinUrl = skinUrl;
    }
    public void chatColour(String chatColour) {
        this.chatColour = chatColour;
    }
    public void gameMode(SBGameMode gameMode) {
        this.gameMode = gameMode;

        Player player = super.toBukkit(Player.class);
        if (player != null) {
            player.setGameMode(this.gameMode().getGameMode());
        }

        SBLogger.info("<green>Updated <yellow>" + this.getName() + "'s <green>game mode to <yellow>" + this.gameMode().name() + "<green>.");
    }
    public void allowFlight(boolean allowFlight) {
        this.allowFlight = allowFlight;

        Player player = super.toBukkit(Player.class);
        if (player != null) {
            player.setAllowFlight(allowFlight);
        }
    }
    public void flying(boolean value) {
        if (this.flying == value) {
            SBLogger.warn("<yellow>Cannot update flight for " + this.getName() + ". Already " + (flying() ? "flying" : "not flying") + "!");
        } else {
            this.flying = value;

            Player player = super.toBukkit(Player.class);
            if (player != null) {
                player.setFlying(value);
            }

            SBLogger.info("<green>" + this.getName() + " is now <yellow>" + (flying() ? "flying" : "not flying") + "!");
        }
    }

    public void currentWorld(SBWorld world) {
        this.currentWorld = world;

        Player player = super.toBukkit(Player.class);
        if (player != null && world.toBukkit() != null) {
            Location loc = player.getLocation();
            loc.setWorld(world.toBukkit());
            player.teleport(loc);
        }

        SBLogger.info("<green>" + this.getName() + " moved to world <yellow>" + world.getName() + "<green>.");
    }
    public void location(SBLocation location) {
        this.location = location;

        Player player = super.toBukkit(Player.class);
        if (player != null && location.toBukkit() != null) {
            player.teleport(location.toBukkit());
        }

        SBLogger.info("<green>Teleported <yellow>" + this.getName() + " <green>to <yellow>" +
                location.getWorld().toString() + " (" +
                Math.round(location.getX()) + ", " +
                Math.round(location.getY()) + ", " +
                Math.round(location.getZ()) + ")");
    }

    public void teleport(SBLocation loc) {
        this.currentWorld = loc.getWorld();
        this.location(loc);
    }

    public void flySpeed(float speed){
        Player player = super.toBukkit(Player.class);
        if (player == null){
            return;
        }

        player.setFlySpeed(speed);
    }

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


    public @Override void sendMessage(String message) {
        super.sendMessage(message, this.playerPlaceholders);
    }
    public @Override void sendMessage(Component message) {
        super.sendMessage(message, this.playerPlaceholders);
    }
    @Override
    public String toString() {
        return "SBPlayer[username=" + this.getName() +
                ", uuid=" + this.getUniqueID() +
                ", world=" + (currentWorld != null ? currentWorld.getName() : "null") +
                ", location=" + (location != null ?
                "(" + Math.round(location.getX()) + "," + Math.round(location.getY()) + "," + Math.round(location.getZ()) + ")" : "null") + "]";
    }
}
