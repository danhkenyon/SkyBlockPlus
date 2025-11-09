package uk.ac.bsfc.sbp.utils.user;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.game.SBGameMode;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.location.SBWorld;
import uk.ac.bsfc.sbp.utils.schematic.Clipboard;
import uk.ac.bsfc.sbp.utils.schematic.ClipboardUtils;
import uk.ac.bsfc.sbp.utils.strings.Placeholder;

import java.util.UUID;

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

        // Initialize clipboard
        if (ClipboardUtils.getInstance().hasClipboard(this)) {
            clipboard = ClipboardUtils.getInstance().getClipboard(this);
        } else {
            clipboard = Clipboard.create();
            ClipboardUtils.getInstance().getClipboards().put(this, clipboard);
        }

        // Default player properties
        skinUrl = "";
        chatColour = "<white>";
        gameMode = SBGameMode.SURVIVAL;
        allowFlight = false;
        flying = false;

        // Get the Bukkit player if online
        Player player = super.toBukkit(Player.class);

        // Ensure world exists
        SBWorld defaultWorld;
        if (player != null && player.getWorld() != null) {
            defaultWorld = SBWorld.getWorld(player.getWorld().getName());
            if (defaultWorld == null) {
                defaultWorld = SBWorld.load(player.getWorld().getName());
            }
            this.location = SBLocation.of(player.getLocation());
        } else {
            defaultWorld = SBWorld.getWorld("world");
            if (defaultWorld == null) {
                defaultWorld = SBWorld.load("world"); // Load or create default world
            }
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


    public void setFlySpeed(float speed){
        Player player = super.toBukkit(Player.class);
        if (player == null){
            return;
        }

        player.setFlySpeed(speed);
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

    @Override
    public void sendMessage(String message) {
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
