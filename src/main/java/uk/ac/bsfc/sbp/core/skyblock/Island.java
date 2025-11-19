package uk.ac.bsfc.sbp.core.skyblock;

import uk.ac.bsfc.sbp.utils.location.SBLocation;
import uk.ac.bsfc.sbp.utils.time.SBTime;
import uk.ac.bsfc.sbp.utils.user.SBUser;

import java.util.List;
import java.util.UUID;

/**
 * Represents an island within the SkyBlock+ system, containing details such as
 * its unique identifier, name, region, and members.
 */
public class Island {
    private final UUID id;
    private String name;
    private final IslandRegion region;
    private final long timeCreated;

    protected Island(UUID id, String name, SBLocation loc1, long timeCreated) {
        this.id = id;
        this.name = name;
        this.region = IslandRegion.of(loc1);
        this.timeCreated = SBTime.now().millis();
    }
    protected Island(UUID id, String name, SBLocation loc1) {
        this(id, name, loc1, SBTime.now().millis());
    }

    public static Island create(UUID id, String name, SBLocation loc1, List<SBUser> members) {
        Island island = new Island(id, name, loc1);
        for (SBUser user : members) {
            user.setIslandId(id);
        }
        return island;
    }
    public static Island create(String name, SBLocation loc1, SBUser user) {
        return create(UUID.randomUUID(), name, loc1, List.of(user));
    }
    public static Island create(String name, SBUser user) {
        return create(UUID.randomUUID(), name, null /* TODO: Get ISLAND LOCATION */, List.of(user));
    }
    public static Island get(UUID id, String name, SBLocation loc1, long timeCreated) {
        return new Island(id, name, loc1, timeCreated);
    }


    public UUID uuid() {
        return id;
    }
    public String name() {
        return name;
    }
    public IslandRegion region() {
        return region;
    }
    public long timeCreated() {
        return timeCreated;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void delete() {
        // TODO
    }

    @Override
    public String toString() {
        return "Island[id="+this.uuid()+", name="+this.name()+", loc="+this.region().getLoc1()+"]";
    }
}
