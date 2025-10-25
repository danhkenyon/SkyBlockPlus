package uk.ac.bsfc.sbp.core.events;

import uk.ac.bsfc.sbp.utils.data.database.tables.UserTable;
import uk.ac.bsfc.sbp.utils.event.Event;
import uk.ac.bsfc.sbp.utils.event.SBEventHandler;
import uk.ac.bsfc.sbp.utils.event.player.UserJoinEvent;

public class UserJoinHandler extends SBEventHandler {

    @Event()
    public void onPlayerJoin(UserJoinEvent event) {
        if (UserTable.getInstance().getRow(event.user().uuid()) == null) {
            UserTable.getInstance().insert(
                    event.user().uuid(),
                    event.user().username()
            );
        }
    }
}
