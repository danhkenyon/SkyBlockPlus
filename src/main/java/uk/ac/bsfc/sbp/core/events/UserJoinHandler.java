package uk.ac.bsfc.sbp.core.events;

import uk.ac.bsfc.sbp.core.skyblock.Rank;
import uk.ac.bsfc.sbp.utils.data.database.tables.IslandMemberTable;
import uk.ac.bsfc.sbp.utils.data.database.tables.UserTable;
import uk.ac.bsfc.sbp.utils.event.Event;
import uk.ac.bsfc.sbp.utils.event.SBEventHandler;
import uk.ac.bsfc.sbp.utils.event.player.UserJoinEvent;

/**
 * Handles the event related to a user joining the server. This class is an implementation of
 * {@link SBEventHandler} and provides functionality to manage user and island member data
 * upon user join events.
 *
 * Responsibilities of this handler include:
 * - Checking if a joining user is already present in the {@link UserTable}. If not, the user is added to the table.
 * - Ensuring that the joining user has a corresponding entry in the {@link IslandMemberTable}.
 *   If absent, the user is assigned a default rank of {@link Rank#RECRUIT}.
 *
 * This class uses the {@link Event} annotation to mark its event handling method, and it listens for
 * {@link UserJoinEvent}.
 */
public class UserJoinHandler extends SBEventHandler {

    @Event()
    public void onPlayerJoin(UserJoinEvent event) {
        /*
        if (UserTable.getInstance().getRow(event.user().getUniqueID()) == null) {
            UserTable.getInstance().insert(
                    event.user().getUniqueID(),
                    event.user().getName()
            );
        }

        if (IslandMemberTable.getInstance().getRow("player_uuid", event.user().getUniqueID()) == null) {

        }

         */
    }
}