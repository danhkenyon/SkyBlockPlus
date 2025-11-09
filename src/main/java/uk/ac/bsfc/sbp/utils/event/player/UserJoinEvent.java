package uk.ac.bsfc.sbp.utils.event.player;

import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.data.SBConfig;
import uk.ac.bsfc.sbp.utils.event.SBEvent;
import uk.ac.bsfc.sbp.utils.game.SBServer;
import uk.ac.bsfc.sbp.utils.user.SBUser;

import static uk.ac.bsfc.sbp.utils.SBConstants.Messages.DEFAULT_JOIN_MESSAGE;

public class UserJoinEvent extends SBEvent {
    private boolean cancelled;
    private String message;

    public UserJoinEvent(SBUser user) {
        this.user(user);

        this.cancelled = false;
        this.message = SBConfig.getString("messages.join", DEFAULT_JOIN_MESSAGE);
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
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

        SBServer.broadcastRaw(this.getMessage().replace("%username%", user.getName()));
    }

}