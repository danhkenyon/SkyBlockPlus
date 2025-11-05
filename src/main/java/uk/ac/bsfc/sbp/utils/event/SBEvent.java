package uk.ac.bsfc.sbp.utils.event;

import org.jetbrains.annotations.NotNull;
import uk.ac.bsfc.sbp.utils.time.SBTime;
import uk.ac.bsfc.sbp.utils.user.SBUser;

import java.time.Instant;

public abstract class SBEvent {
    protected Instant timestamp;
    protected boolean cancelled;
    protected SBUser user;

    protected SBEvent() {
        this.timestamp = Instant.now();
        this.cancelled = false;
    }

    // ====== Getters ======
    public SBTime timestamp() {
        return SBTime.now();
    }
    public SBUser user() {
        return user;
    }
    public boolean isCancelled() {
        return cancelled;
    }

    // ====== Setters ======

    public void user(@NotNull SBUser user) {
        this.user = user;
    }
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public void call() {

    }
}