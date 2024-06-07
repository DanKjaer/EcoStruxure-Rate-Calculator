package ecostruxure.rate.calculator.gui.widget.control;

import atlantafx.base.theme.Styles;
import ecostruxure.rate.calculator.gui.widget.skin.AutocompleteComboBoxSkin;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Skin;

/**
 * Edited SearchableComboBox from ControlsFX to use own AutocompleteComboBoxSkin.
 */
public class AutocompleteComboBox<T> extends ComboBox<T> {
    private static final PseudoClass SUCCESS_PSEUDO_CLASS = Styles.STATE_SUCCESS;
    private static final PseudoClass DANGER_PSEUDO_CLASS = Styles.STATE_DANGER;

    private static final String DEFAULT_STYLE_CLASS = "searchable-combo-box";

    public AutocompleteComboBox() {
        this(FXCollections.observableArrayList());
    }

    public AutocompleteComboBox(ObservableList<T> items) {
        super(items);
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        setupPseudoClassListeners();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new AutocompleteComboBoxSkin<>(this);
    }

    private void setupPseudoClassListeners() {
        this.pseudoClassStateChanged(SUCCESS_PSEUDO_CLASS, false);
        this.pseudoClassStateChanged(DANGER_PSEUDO_CLASS, false);

        this.getPseudoClassStates().addListener((Observable o) -> {
            if (getSkin() instanceof AutocompleteComboBoxSkin) {
                ((AutocompleteComboBoxSkin<?>) getSkin()).updatePseudoClassStates();
            }
        });
    }
}