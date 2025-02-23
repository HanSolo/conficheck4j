package eu.hansolo.fx.conficheck4j.tools;

import javafx.event.ActionEvent;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;


public class SearchTextFieldSkin extends TextFieldSkin {
    private Region searchIcon;
    private Text   searchText;
    private Region closeIcon;


    public SearchTextFieldSkin(final SearchTextField control){
        super(control);

        initGraphics();
        registerListeners();
    }


    private void initGraphics() {
        searchIcon = new Region();
        searchIcon.getStyleClass().addAll("search-icon");
        searchIcon.setFocusTraversable(false);

        searchText = new Text("Search");
        searchText.getStyleClass().addAll("search-text");
        searchText.setFocusTraversable(false);

        closeIcon = new Region();
        closeIcon.getStyleClass().addAll("close-icon");
        closeIcon.setFocusTraversable(false);
        closeIcon.setVisible(false);

        getChildren().addAll(searchIcon, searchText, closeIcon);
    }

    private void registerListeners() {
        SearchTextField searchTextField = (SearchTextField) getSkinnable();
        searchTextField.searchTextProperty().addListener((o, ov, nv) -> searchText.setText(nv));

        closeIcon.setOnMouseClicked(event -> getSkinnable().setText(""));
        getSkinnable().textProperty().addListener(o -> {
            closeIcon.setVisible(getSkinnable().getText().isEmpty() ? false : true);
            getSkinnable().fireEvent(new ActionEvent());
        });

        getSkinnable().focusedProperty().addListener(o -> {
            searchIcon.setVisible(!getSkinnable().isFocused() && getSkinnable().getText().isEmpty());
            searchText.setVisible(!getSkinnable().isFocused() && getSkinnable().getText().isEmpty());
            closeIcon.setVisible(getSkinnable().isFocused() && !getSkinnable().getText().isEmpty() ? true : false);
        });

        getSkinnable().widthProperty().addListener(o -> {
            final double size = searchIcon.getMaxWidth() < 0 ? 20.8 : searchIcon.getWidth();
            searchIcon.setTranslateX(-getSkinnable().getWidth() * 0.5 + size * 0.7);
            searchText.setTranslateX(-getSkinnable().getWidth() * 0.5 + size * 2.25);
            closeIcon.setTranslateX(getSkinnable().getWidth() * 0.5 - size * 0.7);
        });

        getSkinnable().heightProperty().addListener(observable -> {
            closeIcon.setMaxSize(getSkinnable().getHeight() * 0.8, getSkinnable().getHeight() * 0.8);
            searchIcon.setMaxSize(getSkinnable().getHeight() * 0.8, getSkinnable().getHeight() * 0.8);
        });

        getSkinnable().sceneProperty().addListener(observable -> {
            searchIcon.setTranslateX(-getSkinnable().getWidth() * 0.5 + closeIcon.getWidth() * 0.7);
            searchText.setTranslateX(-getSkinnable().getWidth() * 0.5 + closeIcon.getWidth() * 2.25);
            closeIcon.setTranslateX(getSkinnable().getWidth() * 0.5 - searchIcon.getWidth() * 0.7);
        });
    }
}
