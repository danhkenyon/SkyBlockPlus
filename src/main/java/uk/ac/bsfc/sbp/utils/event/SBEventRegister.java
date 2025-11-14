package uk.ac.bsfc.sbp.utils.event;

import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
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
            if (!method.isAnnotationPresent(uk.ac.bsfc.sbp.utils.event.Event.class)) continue;
            if (method.getParameterCount() != 1) continue;

            Class<?> paramType = method.getParameterTypes()[0];
            method.setAccessible(true);

            if (SBEvent.class.isAssignableFrom(paramType)) {
                uk.ac.bsfc.sbp.utils.event.Event annotation = method.getAnnotation(uk.ac.bsfc.sbp.utils.event.Event.class);
                Class<? extends SBEvent> eventClass = (Class<? extends SBEvent>) paramType;

                LISTENERS.computeIfAbsent(eventClass, k -> new CopyOnWriteArrayList<>())
                        .add(new ListenerEntry(handler, method, annotation.async()));
            }
            else if (org.bukkit.event.Event.class.isAssignableFrom(paramType)) {
                try {
                    registerBukkitEventHandler(handler, method, (Class<? extends org.bukkit.event.Event>) paramType);
                    SBLogger.info("Registered Bukkit event handler: " + handler.getClass().getSimpleName() + "." + method.getName() + " for " + paramType.getSimpleName());
                } catch (Exception e) {
                    SBLogger.err("<red>Failed to register Bukkit event handler: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private <T extends org.bukkit.event.Event> void registerBukkitEventHandler(SBEventHandler handler, Method method, Class<T> eventClass) {
        uk.ac.bsfc.sbp.utils.event.Event annotation = method.getAnnotation(uk.ac.bsfc.sbp.utils.event.Event.class);
        boolean async = annotation.async();

        Listener listener = new Listener() {};

        EventExecutor executor = (bukkitListener, bukkitEvent) -> {
            if (!eventClass.isInstance(bukkitEvent)) return;

            Runnable task = () -> {
                try {
                    SBEventHandler handlerInstance = createHandlerInstance(handler.getClass());
                    if (handlerInstance == null) return;
                    method.invoke(handlerInstance, bukkitEvent);
                } catch (Exception e) {
                    SBLogger.err("<red>Error during Bukkit event execution: " + e.getMessage());
                    e.printStackTrace();
                }
            };

            if (async) EXECUTOR.submit(task);
            else task.run();
        };

        Main.getInstance().getServer().getPluginManager().registerEvent(
                eventClass,
                listener,
                org.bukkit.event.EventPriority.NORMAL,
                executor,
                Main.getInstance()
        );
    }

    public void register() {
        try {
            List<Class<?>> potential = SBReflectionUtils.find("uk.ac.bsfc.sbp.core", SBEventHandler.class);
            for (Class<?> clazz : potential) {
                SBLogger.info("Scanning event handler: " + clazz.getSimpleName());
                if (!Modifier.isAbstract(clazz.getModifiers())) {
                    SBEventHandler handler = (SBEventHandler) clazz.getDeclaredConstructor().newInstance();
                    SBLogger.info("Registered event handler: " + handler.getClass().getSimpleName());
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