package ecostruxure.rate.calculator.gui.system.event;

import java.util.function.Consumer;

public interface EventBus {
    /**
     * Publishes an event to all subscribers.
     */
    <T extends Event> void publish(T event);

    /**
     * Subscribe to an event type.
     */
    <T extends Event> void subscribe(Class<? extends T> eventType, Consumer<T> subscriber);

    /**
     * Unsubscribe from an event type.
     */
    <T extends Event> void unsubscribe(Class<? extends T> eventType, Consumer<T> subscriber);
}