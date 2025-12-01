package uk.ac.bsfc.sbp.core.events;

import org.bukkit.event.player.PlayerJoinEvent;
import uk.ac.bsfc.sbp.utils.data.database.UserTable;
import uk.ac.bsfc.sbp.utils.event.Event;
import uk.ac.bsfc.sbp.utils.event.SBEventHandler;

public class UserJoinHandler extends SBEventHandler {

    @Event(async = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (UserTable.getInstance().getRow(event.getPlayer().getUniqueId()) == null) {
            UserTable.getInstance().insert(
                    event.getPlayer().getUniqueId(),
                    event.getPlayer().getName()
            );
        }
    }
}