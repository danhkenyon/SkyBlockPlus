package uk.ac.bsfc.sbp.core.events;

import uk.ac.bsfc.sbp.utils.data.UserDatabase;
import uk.ac.bsfc.sbp.utils.event.Event;
import uk.ac.bsfc.sbp.utils.event.SBEventHandler;
import uk.ac.bsfc.sbp.utils.event.player.UserJoinEvent;

public class UserJoinHandler extends SBEventHandler {

    @Event()
    public void onPlayerJoin(UserJoinEvent event) {
        if (UserDatabase.fetchUser(event.user().uuid()) == null) {
            UserDatabase.insertUser(event.user());
        }
    }
}
