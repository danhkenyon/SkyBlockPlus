package uk.ac.bsfc.sbp.utils.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.event.player.UserJoinEvent;
import uk.ac.bsfc.sbp.utils.user.SBUser;

public class EventBridge implements Listener {

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage("");
        Main.getInstance().getEventRegister().fire(new UserJoinEvent(SBUser.from(e.getPlayer())));
    }
}
