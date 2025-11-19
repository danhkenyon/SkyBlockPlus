package uk.ac.bsfc.sbp.utils.event;

import uk.ac.bsfc.sbp.core.skyblock.Island;
import uk.ac.bsfc.sbp.utils.SBLogger;

public class IslandCreationEvent extends SBEvent {
    private boolean cancelled;

    public IslandCreationEvent(Island island) {
        this.user(user);

        this.cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public void call() {
        SBLogger.raw("<white>"+user.getName()+" <yellow>executed the event: <aqua>"+getClass().getSimpleName());

    }
}
