package uk.ac.bsfc.sbp.utils.event;

/**
 * Represents the base class for all custom event handlers in the application.
 * Subclasses of {@code SBEventHandler} define specific behavior for handling
 * different types of events within the custom event system.
 *
 * Key Features:
 * - Acts as a foundational component for the custom event-handling architecture.
 * - Serves as the base type for all event handlers registered with the custom
 *   {@link SBEventRegister}.
 *
 * Usage:
 * - Subclasses must implement event handling methods annotated with {@link Event}.
 * - Event handling methods in subclasses are invoked when the specified event occurs, and they
 *   must follow the custom event framework's conventions for method parameters and handling logic.
 *
 * Integration:
 * - Works in conjunction with the {@link SBEventRegister} for event
 *   listener registration.
 * - Leverages the {@link SBEvent} hierarchy for defining and propagating
 *   events to appropriate handlers.
 *
 * Subclass Responsibilities:
 * - Define specific methods to react to particular event types.
 * - Ensure methods take a single parameter that is a subclass of {@code SBEvent}.
 * - Optionally specify synchronous or asynchronous execution using the {@link Event} annotation.
 */
public abstract class SBEventHandler {}