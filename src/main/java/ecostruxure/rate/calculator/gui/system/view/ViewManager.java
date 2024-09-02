package ecostruxure.rate.calculator.gui.system.view;

import ecostruxure.rate.calculator.gui.common.Controller;
import ecostruxure.rate.calculator.gui.system.event.EventBus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ViewManager {
    private final Map<Class<? extends Controller>, Controller> controllers = new HashMap<>();
    private final ObjectProperty<Class<? extends Controller>> activeControllerClass = new SimpleObjectProperty<>();
    private final ObjectProperty<Object> currentData = new SimpleObjectProperty<>();
    private final Stack<ViewSnapshot> history = new Stack<>();
    private final Stack<ViewSnapshot> forwardHistory = new Stack<>();

    public ViewManager(EventBus eventBus) {
        eventBus.subscribe(ChangeViewEvent.class, event -> changeView(event.target(), event.data()));
        eventBus.subscribe(PreviousViewEvent.class, event -> previousView());
        eventBus.subscribe(NextViewEvent.class, event -> nextView());
    }

    public void addController(Class<? extends Controller> controllerClass, Controller controller) {
        controllers.put(controllerClass, controller);
    }

    public ObjectProperty<Class<? extends Controller>> activeControllerClassProperty() {
        return activeControllerClass;
    }

    void changeView(Class<? extends Controller> controllerClass, Object data) {
        if (activeControllerClass.get() != null && activeControllerClass.get().equals(controllerClass)) return;

        if (activeControllerClass.get() != null) {
            history.push(new ViewSnapshot(controllers.get(activeControllerClass.get()), currentData.get()));
            forwardHistory.clear();
        }

        Controller controller = controllers.get(controllerClass);
        if (controller == null) return;
        controller.activate(data);
        activeControllerClass.set(controllerClass);
        currentData.set(data);
    }

    void previousView() {
        if (history.isEmpty()) return;

        ViewSnapshot snapshot = history.pop();
        forwardHistory.push(new ViewSnapshot(controllers.get(activeControllerClass.get()), currentData.get()));
        activeControllerClass.set(snapshot.controller.getClass());
        snapshot.controller().activate(snapshot.data());
        currentData.set(snapshot.data());
    }

    void nextView() {
        if (forwardHistory.isEmpty()) return;

        ViewSnapshot snapshot = forwardHistory.pop();
        history.push(new ViewSnapshot(controllers.get(activeControllerClass.get()), currentData.get()));
        activeControllerClass.set(snapshot.controller.getClass());
        snapshot.controller.activate(snapshot.data);
        currentData.set(snapshot.data);
    }

    Stack<ViewSnapshot> history() {
        return history;
    }

    Stack<ViewSnapshot> forwardHistory() {
        return forwardHistory;
    }

    private record ViewSnapshot(Controller controller, Object data) {}
}