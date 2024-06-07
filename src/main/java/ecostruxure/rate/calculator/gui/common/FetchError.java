package ecostruxure.rate.calculator.gui.common;

import javafx.beans.value.ObservableStringValue;

public record FetchError(Boolean success, ObservableStringValue reason) { }
