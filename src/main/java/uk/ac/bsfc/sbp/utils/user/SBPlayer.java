package uk.ac.bsfc.sbp.utils.user;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.game.SBGameMode;
import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.location.SBWorld;
import uk.ac.bsfc.sbp.utils.schematic.Clipboard;
import uk.ac.bsfc.sbp.utils.schematic.ClipboardUtils;

import java.util.UUID;

public class SBPlayer extends SBUser {
    private final Clipboard clipboard;

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
        chatColour = "&f";
        gameMode = SBGameMode.SURVIVAL;
        allowFlight = false;
        flying = false;

        Player player = super.toBukkit(Player.class);
        if (player != null) {
            this.currentWorld = SBWorld.of(player.getWorld().getName(), player.getWorld());
            this.location = SBLocation.of(player.getLocation());
        } else {
            this.currentWorld = SBWorld.of("world");
            this.location = SBLocation.of();
        }
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

        SBLogger.info("&aUpdated &e" + this.getName() + "'s &agame mode to &e" + this.gameMode().name() + "&a.");
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
            SBLogger.warn("&eCannot update flight for " + this.getName() + ". Already " + (flying() ? "flying" : "not flying") + "!");
        } else {
            this.flying = value;

            Player player = super.toBukkit(Player.class);
            if (player != null) {
                player.setFlying(value);
            }

            SBLogger.info("&a" + this.getName() + " is now &e" + (flying() ? "flying" : "not flying") + "!");
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

        SBLogger.info("&a" + this.getName() + " moved to world &e" + world.getName() + "&a.");
    }
    public void location(SBLocation location) {
        this.location = location;

        Player player = super.toBukkit(Player.class);
        if (player != null && location.toBukkit() != null) {
            player.teleport(location.toBukkit());
        }

        SBLogger.info("&aTeleported &e" + this.getName() + " &ato &e" +
                location.getWorld() + " (" +
                Math.round(location.getX()) + ", " +
                Math.round(location.getY()) + ", " +
                Math.round(location.getZ()) + ")");
    }

    public void teleport(SBLocation loc) {
        this.currentWorld = loc.getWorld();
        this.location(loc);
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
