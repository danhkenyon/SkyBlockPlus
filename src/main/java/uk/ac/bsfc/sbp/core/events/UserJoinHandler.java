package uk.ac.bsfc.sbp.core.events;

import uk.ac.bsfc.sbp.core.skyblock.Member;
import uk.ac.bsfc.sbp.core.skyblock.Rank;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandMemberTable;
import uk.ac.bsfc.sbp.utils.data.database.tables.UserTable;
import uk.ac.bsfc.sbp.utils.event.Event;
import uk.ac.bsfc.sbp.utils.event.SBEventHandler;
import uk.ac.bsfc.sbp.utils.event.player.UserJoinEvent;
import uk.ac.bsfc.sbp.utils.user.SBPlayer;

public class UserJoinHandler extends SBEventHandler {

    @Event()
    public void onPlayerJoin(UserJoinEvent event) {
        if (UserTable.getInstance().getRow(event.user().getUniqueID()) == null) {
            UserTable.getInstance().insert(
                    event.user().getUniqueID(),
                    event.user().getName()
            );
        }

        if (IslandMemberTable.getInstance().getRow("player_uuid", event.user().getUniqueID()) == null) {
            Member member = Member.of(event.user().to(SBPlayer.class), Rank.RECRUIT);
            IslandMemberTable.getInstance().insertOrUpdate(member);
        }
    }
}