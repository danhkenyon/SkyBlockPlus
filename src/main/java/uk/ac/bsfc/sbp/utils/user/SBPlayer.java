package uk.ac.bsfc.sbp.utils.user;

import org.bukkit.entity.Player;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.game.SBGameMode;
import uk.ac.bsfc.sbp.utils.schematic.Clipboard;
import uk.ac.bsfc.sbp.utils.schematic.ClipboardUtils;

import java.util.UUID;

public class SBPlayer extends SBUser {
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
    }
    private final Clipboard clipboard;

    private String skinUrl;
    private String chatColour;
    private SBGameMode gameMode;

    private boolean allowFlight;
    private boolean flying;

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

    public void skinUrl(String skinUrl) {
        this.skinUrl = skinUrl;
    }
    public void chatColour(String chatColour) {
        this.chatColour = chatColour;
    }
    public void gameMode(SBGameMode gameMode) {
        this.gameMode = gameMode;

        super.toBukkit(Player.class).setGameMode(this.gameMode().getGameMode());
        SBLogger.info("&aUpdated &e" + this.username() + "'s &agame mode to &e" + this.gameMode().name() + "&a.");
    }
    public void allowFlight(boolean allowFlight) {
        this.allowFlight = allowFlight;
        super.toBukkit(Player.class).setAllowFlight(allowFlight);
    }
    public void flying(boolean value) {
        if (this.flying == value) {
            SBLogger.warn("&eCannot update flight for " + this.username() + ". Already " + (flying() ? "flying" : "not flying") + "!");
        } else {
            this.flying = value;
            super.toBukkit(Player.class).setFlying(value);
            SBLogger.info("&a" + this.username() + " is now &e" + (flying() ? "flying" : "not flying") + "!");
        }
    }

    @Override
    public String toString() {
        return "SBPlayer[username=" + this.username() + ", uuid=" + this.uuid()+"]";
    }
}