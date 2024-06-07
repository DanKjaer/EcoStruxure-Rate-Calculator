package ecostruxure.rate.calculator.gui.util.property;

import javafx.beans.property.BooleanPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.Node;

public class ValidationProperty extends BooleanPropertyBase {
    private final Node node;
    private final PseudoClass successClass;
    private final PseudoClass dangerClass;

    public ValidationProperty(Node node, PseudoClass successClass, PseudoClass dangerClass, boolean initialValue) {
        super(initialValue);
        this.node = node;
        this.successClass = successClass;
        this.dangerClass = dangerClass;
        set(initialValue);
        invalidated();
    }

    public ValidationProperty(Node node, PseudoClass successClass, PseudoClass dangerClass) {
        this(node, successClass, dangerClass, false);
    }

    @Override
    protected void invalidated() {
        boolean isActive = get();
        node.pseudoClassStateChanged(successClass, isActive);
        node.pseudoClassStateChanged(dangerClass, !isActive);
    }

    @Override
    public Object getBean() {
        return node;
    }

    @Override
    public String getName() {
        return "success: " + successClass.getPseudoClassName() + ", danger: " + dangerClass.getPseudoClassName();
    }
}
