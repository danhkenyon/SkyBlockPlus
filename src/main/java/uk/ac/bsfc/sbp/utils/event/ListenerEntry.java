package uk.ac.bsfc.sbp.utils.event;

import java.lang.reflect.Method;

/**
 * Represents a record used to store event listener metadata in the event registration system.
 * It encapsulates the event handler instance, the method annotated with {@link Event},
 * and whether the method should be executed asynchronously.
 *
 * This record is utilized by the {@link SBEventRegister} class to manage
 * and execute event listeners when relevant events occur.
 *
 * Components:
 * - instance: The {@link SBEventHandler} object that contains the event listener method.
 * - method: The {@link Method} annotated with {@link Event}.
 * - async: A boolean flag determining whether the method should execute asynchronously.
 */
public record ListenerEntry(SBEventHandler instance, Method method, boolean async) {}