package ecostruxure.rate.calculator.gui.system.background;

import ecostruxure.rate.calculator.gui.system.event.EventBus;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BackgroundManager {
    private final ExecutorService executorService;

    public BackgroundManager(EventBus eventBus) {
        executorService = Executors.newCachedThreadPool();
        eventBus.subscribe(BackgroundTaskEvent.class, this::handleBackgroundTaskEvent);
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void handleBackgroundTaskEvent(BackgroundTaskEvent<?> event) {
        executorService.submit(event.task());
    }

    public void shutdown() {
        try {
            System.out.println("Attempting to shutdown background manager");
            executorService.shutdown();
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                System.err.println("Executor did not terminate in the specified time.");
                List<Runnable> droppedTasks = executorService.shutdownNow();
                System.err.println("Executor was abruptly shut down. " + droppedTasks.size() + " tasks will not be executed.");
            }
        } catch (InterruptedException e) {
            System.err.println("Tasks interrupted");
            executorService.shutdownNow();
        }
        System.out.println("Shutdown finished\n");
    }
}
