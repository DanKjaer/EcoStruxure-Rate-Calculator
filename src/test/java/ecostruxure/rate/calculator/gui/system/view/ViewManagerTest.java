/*
package ecostruxure.rate.calculator.gui.system.view;

import ecostruxure.rate.calculator.gui.common.Controller;
import ecostruxure.rate.calculator.gui.system.event.EventBus;
import ecostruxure.rate.calculator.gui.system.view.ViewManager;
import javafx.scene.layout.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.times;

class ViewManagerTest {
    private ViewManager viewManager;
    private Controller controllerA;
    private Controller controllerB;

    private static class ControllerA implements Controller {
        @Override
        public void activate(Object data) {

        }

        @Override
        public Region view() {
            return null;
        }
    }

    private static class ControllerB implements Controller {
        @Override
        public void activate(Object data) {

        }

        @Override
        public Region view() {
            return null;
        }
    }

    @BeforeEach
    void setUp() {
        controllerA = Mockito.spy(new ControllerA());
        controllerB = Mockito.spy(new ControllerB());

        viewManager = new ViewManager(Mockito.mock(EventBus.class));
        viewManager.addController(ControllerA.class, controllerA);
        viewManager.addController(ControllerB.class, controllerB);
    }

    @Test
    void it_shouldChangeView() {
        viewManager.changeView(ControllerA.class, "Data for A");

        assertThat(viewManager.activeControllerClassProperty().get()).isEqualTo(ControllerA.class);
        Mockito.verify(controllerA).activate("Data for A");
        assertThat(viewManager.history()).isEmpty();
        assertThat(viewManager.forwardHistory()).isEmpty();
    }

    @Test
    void it_shouldPushCurrentViewToHistoryOnViewChange() {
        viewManager.changeView(ControllerA.class, "Data for A");
        viewManager.changeView(ControllerB.class, "Data for B");

        assertThat(viewManager.activeControllerClassProperty().get()).isEqualTo(ControllerB.class);
        Mockito.verify(controllerB).activate("Data for B");
        assertThat(viewManager.history()).hasSize(1);
        assertThat(viewManager.forwardHistory()).isEmpty();
    }

    @Test
    void it_shouldNotChangeToSameView() {
        viewManager.changeView(ControllerA.class, "Data for A");
        viewManager.changeView(ControllerA.class, "Data for A");

        assertThat(viewManager.activeControllerClassProperty().get()).isEqualTo(ControllerA.class);
        Mockito.verify(controllerA, times(1)).activate("Data for A");
        assertThat(viewManager.history()).isEmpty();
        assertThat(viewManager.forwardHistory()).isEmpty();
    }

    @Test
    void it_shouldGotoPreviousView() {
        viewManager.changeView(ControllerA.class, "Data for A");
        viewManager.changeView(ControllerB.class, "Data for B");
        viewManager.previousView();

        assertThat(viewManager.activeControllerClassProperty().get()).isEqualTo(ControllerA.class);
        Mockito.verify(controllerA, times(2)).activate("Data for A");
        assertThat(viewManager.history()).isEmpty();
        assertThat(viewManager.forwardHistory()).hasSize(1);
    }

    @Test
    void it_shouldGotoNextView() {
        viewManager.changeView(ControllerA.class, "Data for A");
        viewManager.changeView(ControllerB.class, "Data for B");
        viewManager.previousView();
        viewManager.nextView();

        assertThat(viewManager.activeControllerClassProperty().get()).isEqualTo(ControllerB.class);
        Mockito.verify(controllerB, times(2)).activate("Data for B");
        assertThat(viewManager.history()).hasSize(1);
        assertThat(viewManager.forwardHistory()).isEmpty();
    }
}*/
