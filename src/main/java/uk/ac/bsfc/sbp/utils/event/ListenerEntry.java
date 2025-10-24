package uk.ac.bsfc.sbp.utils.event;

import java.lang.reflect.Method;

public record ListenerEntry(SBEventHandler instance, Method method, boolean async) {}