package eu.hansolo.fx.conficheck4j.tools;

import javafx.collections.ListChangeListener;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;


public class PersistentToggleGroup extends ToggleGroup {
    public PersistentToggleGroup() {
        super();
        getToggles().addListener((ListChangeListener<Toggle>) c -> {
            while (c.next()) {
                for (final Toggle addedToggle : c.getAddedSubList()) {
                    ((ToggleButton) addedToggle).addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
                        if (addedToggle.equals(getSelectedToggle())) mouseEvent.consume();
                    });
                }
            }
        });
    }
}
