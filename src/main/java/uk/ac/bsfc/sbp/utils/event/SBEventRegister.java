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
            SBLogger.err("&cException occurred while registering events!\n" + e.getMessage());
        }

        Main.of().getServer().getPluginManager().registerEvents(new EventBridge(), Main.of());
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
                        SBLogger.err("&cError while running event.call() for " + eventClass.getSimpleName());
                        ex.printStackTrace();
                    }
                } catch (Exception e) {
                    SBLogger.err("&cError during event execution: " + e.getMessage());
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
            SBLogger.err("&cFailed to instantiate handler " + handlerClass.getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
