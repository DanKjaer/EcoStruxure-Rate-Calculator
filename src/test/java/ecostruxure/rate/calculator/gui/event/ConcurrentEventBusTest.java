package ecostruxure.rate.calculator.gui.event;

import ecostruxure.rate.calculator.gui.system.event.ConcurrentEventBus;
import ecostruxure.rate.calculator.gui.system.event.Event;
import ecostruxure.rate.calculator.gui.system.event.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ConcurrentEventBusTest {
    public static Event anEvent() {
        UUID id = UUID.randomUUID();

        return new Event() {
            @Override
            public UUID id() {
                return id;
            }

            @Override
            public boolean equals(Object other) {
                if (this == other) return true;
                if (!(other instanceof Event event)) return false;
                return id().equals(event.id());
            }

            @Override
            public int hashCode() {
                return id().hashCode();
            }
        };
    }

    private EventBus eventBus;

    @BeforeEach
    void setUp() {
        eventBus = new ConcurrentEventBus();
    }


    @Test
    void it_publishWithNoSubscribersShouldNotThrow() {
        // Setup
        Event testEvent = anEvent();

        // Call & Check
        assertDoesNotThrow(() -> eventBus.publish(testEvent));
    }

    @Test
    void it_publishShouldNotifySubscriber() {
        // Setup
        Event testEvent = anEvent();
        AtomicBoolean subscriberNotified = new AtomicBoolean(false);

        Consumer<Event> subscriber = event -> {
            if (event.equals(testEvent)) {
                subscriberNotified.set(true);
            }
        };

        eventBus.subscribe(testEvent.getClass(), subscriber);

        // Call
        eventBus.publish(testEvent);

        // Check
        assertThat(subscriberNotified.get()).isTrue();
    }

    @Test
    void it_publishShouldNotifyAllSubscribers() {
        // Setup
        Event testEvent = anEvent();
        AtomicBoolean subscriber1Notified = new AtomicBoolean(false);
        AtomicBoolean subscriber2Notified = new AtomicBoolean(false);

        Consumer<Event> subscriber1 = event -> {
            if (event.equals(testEvent)) {
                subscriber1Notified.set(true);
            }
        };

        Consumer<Event> subscriber2 = event -> {
            if (event.equals(testEvent)) {
                subscriber2Notified.set(true);
            }
        };

        eventBus.subscribe(testEvent.getClass(), subscriber1);
        eventBus.subscribe(testEvent.getClass(), subscriber2);

        // Call
        eventBus.publish(testEvent);

        // Check
        assertThat(subscriber1Notified.get()).isTrue();
        assertThat(subscriber2Notified.get()).isTrue();
    }

    @Test
    void it_unsubscribeShouldPreventFurtherNotifications() {
        // Setup
        Event testEvent = anEvent();
        AtomicBoolean subscriberNotified = new AtomicBoolean(false);

        Consumer<Event> subscriber = event -> subscriberNotified.set(true);

        eventBus.subscribe(testEvent.getClass(), subscriber);
        eventBus.unsubscribe(testEvent.getClass(), subscriber);

        // Call
        eventBus.publish(testEvent);

        // Check
        assertThat(subscriberNotified.get()).isFalse();
    }

    @Test
    void it_unsubscribeNonExistentSubscriberShouldNotThrow() {
        // Setup
        Event testEvent = anEvent();
        Consumer<Event> subscriber = event -> {};

        // Call & Check
        assertDoesNotThrow(() -> eventBus.unsubscribe(testEvent.getClass(), subscriber));
    }

    @Test
    void it_resubscribeShouldReceiveNotificationsAgain() {
        // Setup
        Event testEvent = anEvent();
        AtomicBoolean subscriberNotified = new AtomicBoolean(false);

        Consumer<Event> subscriber = event -> subscriberNotified.set(true);

        eventBus.subscribe(testEvent.getClass(), subscriber);
        eventBus.unsubscribe(testEvent.getClass(), subscriber);

        // Call
        eventBus.publish(testEvent);

        // Check
        assertThat(subscriberNotified.get()).isFalse();

        // Call
        eventBus.subscribe(testEvent.getClass(), subscriber);
        eventBus.publish(testEvent);

        // Check
        assertThat(subscriberNotified.get()).isTrue();
    }

    @Test
    void it_shouldHandleMultipleEventTypes() {
        // Setup
        Event event1 = anEvent();
        Event event2 = anEvent();
        AtomicBoolean subscriber1Notified = new AtomicBoolean(false);
        AtomicBoolean subscriber2Notified = new AtomicBoolean(false);

        Consumer<Event> subscriber1 = event -> {
            if (event.equals(event1)) {
                subscriber1Notified.set(true);
            }
        };

        Consumer<Event> subscriber2 = event -> {
            if (event.equals(event2)) {
                subscriber2Notified.set(true);
            }
        };

        eventBus.subscribe(event1.getClass(), subscriber1);
        eventBus.subscribe(event2.getClass(), subscriber2);

        // Call
        eventBus.publish(event1);
        eventBus.publish(event2);

        // Check
        assertThat(subscriber1Notified.get()).isTrue();
        assertThat(subscriber2Notified.get()).isTrue();
    }

    @Test
    void it_threadSatefyTest() throws InterruptedException {
        Event testEvent = anEvent();
        AtomicInteger notificationCount = new AtomicInteger(0);

        Consumer<Event> subscriber = event -> notificationCount.incrementAndGet();
        eventBus.subscribe(testEvent.getClass(), subscriber);

        int numberOfThreads = 100;
        Thread[] threads = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(() -> eventBus.publish(testEvent));
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertThat(notificationCount.get()).isEqualTo(numberOfThreads);
    }
}