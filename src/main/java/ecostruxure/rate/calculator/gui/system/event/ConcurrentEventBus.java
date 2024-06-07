package ecostruxure.rate.calculator.gui.system.event;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ConcurrentEventBus implements EventBus {

    private final Map<Class<?>, Set<Consumer<?>>> subscribers = new ConcurrentHashMap<>();

    @Override
    public <T extends Event> void publish(T event) {
        Objects.requireNonNull(event, "Event cannot be null");

        Set<Consumer<?>> subs = subscribers.get(event.getClass());

        if (subs == null) return;

        subs.forEach(subscriber -> safelyInvokeSubscriber(event, subscriber));
    }

    @Override
    public <T extends Event> void subscribe(Class<? extends T> eventType, Consumer<T> subscriber) {
        subscribers.computeIfAbsent(eventType, k -> ConcurrentHashMap.newKeySet()).add(subscriber);
    }

    @Override
    public <T extends Event> void unsubscribe(Class<? extends T> eventType, Consumer<T> subscriber) {
        Set<Consumer<?>> subs = subscribers.get(eventType);
        if (subs == null) return;

        subs.remove(subscriber);
        if (subs.isEmpty()) subscribers.remove(eventType);
    }

    @SuppressWarnings("unchecked")
    private <T extends Event> void safelyInvokeSubscriber(T event, Consumer<?> subscriber) {
        try {
            ((Consumer<T>) subscriber).accept(event);
        } catch (Exception e) {
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
        }
    }
}
