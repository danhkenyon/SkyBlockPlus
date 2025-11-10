package uk.ac.bsfc.sbp.utils.event;

import uk.ac.bsfc.sbp.Main;
import uk.ac.bsfc.sbp.utils.SBLogger;
import uk.ac.bsfc.sbp.utils.SBReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages the registration, invocation, and execution of event listeners within a custom event-handling system.
 * This class enables the registration of event handlers, binding specific methods to event types,
 * and triggering these methods when the associated events occur.
 *
 * Key Features:
 * - Handles the central registration of event listeners, associating methods with specific subclass types of {@link SBEvent}.
 * - Manages synchronous or asynchronous event listener execution based on the {@link Event} annotation settings.
 * - Invokes registered event handlers when an event is fired, providing them with the event instance.
 *
 * Functionalities:
 * - Event Listener Registration:
 *   Provides mechanisms to register event handlers and build the listener registry by scanning methods annotated
 *   with {@link Event} within {@link SBEventHandler} subclasses.
 *
 * - Event Firing:
 *   Facilitates event propagation by invoking all registered listeners that are compatible with the event type.
 *   It respects the synchronous and asynchronous execution settings as per the {@link Event} annotation.
 *
 * - Listener Execution:
 *   Ensures that registered methods are executed in the context of their declared event instances, either synchronously
 *   or asynchronously as defined. Handles potential exceptions during event execution and logs errors appropriately.
 *
 * System Integration:
 * - Utilizes a {@link ConcurrentHashMap} to maintain a list of event-to-listener mappings.
 * - Employs {@link ExecutorService} for running asynchronous handlers.
 * - Works with reflection to instantiate event handler classes and dynamically bind their annotated methods to event types.
 *
 * Core Concepts:
 * - {@link SBEvent}: Represents the base type for all events handled by this system. Each listener targets a subclass of this type.
 * - {@link SBEventHandler}: Represents the base type for all custom event handlers.
 * - {@link Event}: Annotation used to mark methods as eligible for event registration, defining event type and execution mode.
 * - {@link ListenerEntry}: A data structure used internally for managing metadata about each registered listener.
 *
 * Thread Safety:
 * - Leverages thread-safe collections (e.g., {@link CopyOnWriteArrayList} and {@link ConcurrentHashMap})
 *   to ensure safe access to the listener registry from multiple threads.
 * - Properly manages asynchronous tasks using {@link ExecutorService} to execute event handlers on separate threads when required.
 *
 * Responsibilities:
 * - Scanning and registration of {@link SBEventHandler} subclasses and their annotated methods.
 * - Maintaining mappings between event types and their respective listeners.
 * - Invoking appropriate event listeners during the lifecycle of an event.
 * - Handling both synchronous and asynchronous event execution.
 * - Logging and error handling for misconfigured or improperly executed event listeners.
 */
public class SBEventRegister {
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final Map<Class<? extends SBEvent>, List<ListenerEntry>> LISTENERS = new ConcurrentHashMap<>();

    private static SBEventRegister instance;
    public static SBEventRegister getInstance() {
        if (instance == null) instance = new SBEventRegister();
        return instance;
    }

    @SuppressWarnings("unchecked")
    public void register(SBEventHandler handler) {
        for (Method method : handler.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Event.class)) continue;
            if (method.getParameterCount() != 1) continue;

            Class<?> paramType = method.getParameterTypes()[0];
            if (!SBEvent.class.isAssignableFrom(paramType)) continue;

            method.setAccessible(true);
            Event annotation = method.getAnnotation(Event.class);
            Class<? extends SBEvent> eventClass = (Class<? extends SBEvent>) paramType;

            LISTENERS.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>())
                    .add(new ListenerEntry(handler, method, annotation.async()));
        }
    }
    public void register() {
        try {
            List<Class<?>> potential = SBReflectionUtils.find("uk.ac.bsfc.sbp.core", SBEventHandler.class);
            for (Class<?> clazz : potential) {
                if (!Modifier.isAbstract(clazz.getModifiers())) {
                    SBEventHandler handler = (SBEventHandler) clazz.getDeclaredConstructor().newInstance();
                    register(handler);
                }
            }
        } catch (Exception e) {
            SBLogger.err("<red>Exception occurred while registering events!\n" + e.getMessage());
        }

        Main.getInstance().getServer().getPluginManager().registerEvents(new EventBridge(), Main.getInstance());
    }

    public void fire(SBEvent eventInstance) {
        Class<? extends SBEvent> eventClass = eventInstance.getClass();

        List<ListenerEntry> listeners = new ArrayList<>();
        for (Map.Entry<Class<? extends SBEvent>, List<ListenerEntry>> entry : LISTENERS.entrySet()) {
            if (entry.getKey().isAssignableFrom(eventClass)) {
                listeners.addAll(entry.getValue());
            }
        }

        for (ListenerEntry entry : listeners) {
            Runnable task = () -> {
                try {
                    SBEventHandler handlerInstance = createHandlerInstance(entry.instance().getClass());
                    if (handlerInstance == null) return;

                    entry.method().invoke(handlerInstance, eventInstance);

                    try {
                        eventInstance.call();
                    } catch (Exception ex) {
                        SBLogger.err("<red>Error while running event.call() for " + eventClass.getSimpleName());
                        ex.printStackTrace();
                    }
                } catch (Exception e) {
                    SBLogger.err("<red>Error during event execution: " + e.getMessage());
                    e.printStackTrace();
                }
            };

            if (entry.async()) EXECUTOR.submit(task);
            else task.run();
        }
    }
    private SBEventHandler createHandlerInstance(Class<? extends SBEventHandler> handlerClass) {
        try {
            Constructor<? extends SBEventHandler> ctor = handlerClass.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (Exception e) {
            SBLogger.err("<red>Failed to instantiate handler " + handlerClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
