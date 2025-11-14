package uk.ac.bsfc.sbp.utils.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a method as an event listener in a custom event system.
 * Methods annotated with this annotation are automatically registered as listeners
 * for events of the corresponding type defined in their method parameters.
 *
 * By default, the event listener method is executed asynchronously, unless specified otherwise.
 *
 * Methods annotated with {@code @Event} must meet the following conditions:
 * 1. They must take exactly one parameter, which is a subclass of {@link SBEvent}.
 * 2. The method containing this annotation will be invoked when the specified event type occurs.
 *
 * This annotation is processed by the {@link SBEventRegister} class during the registration
 * of event handlers.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Event {
    boolean async() default true;
}