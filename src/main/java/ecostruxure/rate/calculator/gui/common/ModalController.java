package ecostruxure.rate.calculator.gui.common;

import ecostruxure.rate.calculator.gui.common.Controller;
import ecostruxure.rate.calculator.gui.common.TabOrder;
import ecostruxure.rate.calculator.gui.system.modal.ModalModel;

public interface ModalController extends Controller, TabOrder {
    void onSave(Runnable postTaskGuiActions);
    Runnable onClose();
    ModalModel modalModel();
}
