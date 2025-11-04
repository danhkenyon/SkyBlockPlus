package uk.ac.bsfc.sbp.utils.schematic;

import org.bukkit.Location;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

import java.util.*;

public class RegionUtils {
    private final LinkedHashMap<SBPlayer, Region> regions;
    private final LinkedHashMap<SBPlayer, Schematic> schematics;

    private RegionUtils() {
        regions = new LinkedHashMap<>();
        schematics  = new LinkedHashMap<>();
    }

    private static RegionUtils INSTANCE;
    public static RegionUtils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RegionUtils();
        }
        return INSTANCE;
    }

    public Map<SBPlayer, Region> getRegions() {
        return regions;
    }
    public Region getRegion(SBPlayer player) {
        return regions.get(player);
    }

    public void inputLoc1(SBPlayer player, Location location) {
        regions.merge(player, new Region(location, null), (old, n) -> new Region(location, old.getLoc2()));
    }
    public void inputLoc2(SBPlayer player, Location location) {
        regions.merge(player, new Region(null, location), (old, n) -> new Region(old.getLoc1(), location));
    }

    public Map<SBPlayer, Schematic> getSchematics() {
        return schematics;
    }
    public Schematic getSchematic(SBPlayer player) {
        return schematics.get(player);
    }
}
