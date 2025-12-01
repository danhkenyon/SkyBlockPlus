package uk.ac.bsfc.sbp.utils.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.event.player.UserJoinEvent;
import uk.ac.bsfc.sbp.utils.user.SBUser;

/**
 * The EventBridge class acts as a bridge for handling Bukkit events and firing custom events
 * registered in the application. It implements the Listener interface to listen for specific
 * Bukkit events and triggers the corresponding custom events during its execution.
 *
 * This class is designed to integrate the Minecraft Bukkit event system with a custom event
 * registration and handling system. When a supported Bukkit event, such as PlayerJoinEvent,
 * occurs, it initializes a custom event (e.g., UserJoinEvent) and passes the relevant player
 * data to it. The custom event is then fired through the custom event handling logic.
 *
 * Features:
 * - Listens for player join events in the Bukkit API.
 * - Suppresses the default join message when a player joins.
 * - Triggers a custom UserJoinEvent, passing in the player data encapsulated as an SBUser object.
 *
 * Dependencies:
 * - Requires the Main instance to access the custom event registration system.
 * - Uses SBEvent and related infrastructure for custom event handling.
 * - Relies on the PlayerJoinEvent from the Bukkit API for detecting player join events.
 *
 * Methods:
 * - onPlayerJoin(PlayerJoinEvent e): Handles the PlayerJoinEvent, sends a blank join message,
 *   and triggers a custom UserJoinEvent.
 */
public class EventBridge implements Listener {


}
