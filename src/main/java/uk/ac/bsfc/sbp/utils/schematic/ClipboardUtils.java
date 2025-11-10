package uk.ac.bsfc.sbp.utils.schematic;

import uk.ac.bsfc.sbp.utils.user.SBPlayer;

import java.util.HashMap;
import java.util.Map;

/**
 * The ClipboardUtils class provides utility methods for managing Clipboard associations
 * with individual players. This is achieved through an internal mapping of SBPlayer instances
 * to Clipboard objects. It is implemented as a singleton to ensure a single shared instance
 * across the application.
 */
public class ClipboardUtils {
    private final Map<SBPlayer, Clipboard> clipboards;

    private ClipboardUtils() {
        this.clipboards = new HashMap<>();
    }

    private static ClipboardUtils INSTANCE;
    public static ClipboardUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ClipboardUtils();
        }
        return INSTANCE;
    }

    public Map<SBPlayer, Clipboard> getClipboards() {
        return clipboards;
    }

    public Clipboard getClipboard(SBPlayer player) {
        return clipboards.computeIfAbsent(player, p -> Clipboard.create());
    }

    public boolean hasClipboard(SBPlayer player) {
        return clipboards.containsKey(player);
    }

    public void setClipboard(SBPlayer player, Clipboard clipboard) {
        clipboards.put(player, clipboard);
    }
}
