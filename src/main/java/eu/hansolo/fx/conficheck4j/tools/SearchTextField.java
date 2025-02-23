package eu.hansolo.fx.conficheck4j.tools;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;


public class SearchTextField extends TextField {
    private StringProperty searchTextProperty = new SimpleStringProperty("");


    public SearchTextField() {
        super();
        if (Platform.isFxApplicationThread()) {
            setSkin(new SearchTextFieldSkin(this));
        } else {
            Platform.runLater(() -> setSkin(new SearchTextFieldSkin(this)));
        }
    }
    public SearchTextField(final String text) {
        this();
    }

    public String getSearchText() { return searchTextProperty.get(); }
    public void setSearchText(final String searchText) { searchTextProperty.set(searchText); }
    public StringProperty searchTextProperty() { return searchTextProperty; }
}
