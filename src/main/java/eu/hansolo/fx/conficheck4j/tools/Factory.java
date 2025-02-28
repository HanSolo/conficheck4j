package eu.hansolo.fx.conficheck4j.tools;

import eu.hansolo.fx.conficheck4j.fonts.Fonts;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public final class Factory {

    public static final Button createButton(final String text, final String tooltipText, final double fontSize) {
        Button button = new Button(text);
        button.setFont(Fonts.avenirNextLtProMedium(fontSize));
        button.setTooltip(new Tooltip(tooltipText));
        return button;
    }

    public static final ToggleButton createToggleButton(final String text, final double fontSize) {
        ToggleButton toggleButton = new ToggleButton(text);
        toggleButton.setFont(Fonts.avenirNextLtProMedium(fontSize));
        return toggleButton;
    }

    public static final Label createRegularLabel(final String text, final Color textColor, final double fontSize, final Pos textAlignment) {
        return createLabel(text, textColor, Fonts.avenirNextLtProRegular(fontSize), textAlignment);
    }
    public static final Label createMediumLabel(final String text, final Color textColor, final double fontSize, final Pos textAlignment) {
        return createLabel(text, textColor, Fonts.avenirNextLtProMedium(fontSize), textAlignment);
    }
    public static final Label createDemiLabel(final String text, final Color textColor, final double fontSize, final Pos textAlignment) {
        return createLabel(text, textColor, Fonts.avenirNextLtProDemi(fontSize), textAlignment);
    }
    public static final Label createBoldLabel(final String text, final Color textColor, final double fontSize, final Pos textAlignment) {
        return createLabel(text, textColor, Fonts.avenirNextLtProBold(fontSize), textAlignment);
    }
    public static final Label createHeavyLabel(final String text, final Color textColor, final double fontSize, final Pos textAlignment) {
        return createLabel(text, textColor, Fonts.avenirNextLtProHeavy(fontSize), textAlignment);
    }
    public static final Label createLabel(final String text, final Color textColor, final Font font, final Pos textAlignment) {
        Label label = new Label(text);
        label.setFont(font);
        label.setTextFill(textColor);
        label.setAlignment(textAlignment);
        return label;
    }

    public static final Text createTextMono(final String text, final Color textColor, final double fontSize, final Pos textAlignment) {
        //Label label = new Label(text);
        //label.setFont(Fonts.notoSansMono(fontSize));
        //label.setTextFill(textColor);
        //label.setAlignment(textAlignment);
        Text txt = new Text(text);
        txt.setFont(Fonts.notoSansMono(fontSize));
        txt.setFill(textColor);
        //txt.getStyleClass().add("text");
        return txt;
    }

    public static final Text createRegularText(final String text, final Color textColor, final double fontSize) {
        return createText(text, textColor, Fonts.avenirNextLtProRegular(fontSize));
    }
    public static final Text createMediumText(final String text, final Color textColor, final double fontSize) {
        return createText(text, textColor, Fonts.avenirNextLtProMedium(fontSize));
    }
    public static final Text createDemiText(final String text, final Color textColor, final double fontSize) {
        return createText(text, textColor, Fonts.avenirNextLtProDemi(fontSize));
    }
    public static final Text createBoldText(final String text, final Color textColor, final double fontSize) {
        return createText(text, textColor, Fonts.avenirNextLtProBold(fontSize));
    }
    public static final Text createHeavyText(final String text, final Color textColor, final double fontSize) {
        return createText(text, textColor, Fonts.avenirNextLtProHeavy(fontSize));
    }
    public static final Text createText(final String text, final Color textColor, final Font font) {
        Text txt = new Text(text);
        txt.setFont(font);
        txt.setFill(textColor);
        txt.getStyleClass().add("text");
        return txt;
    }

    public static final TextField createRegularSelectableText(final String text, final Color textColor, final double fontSize, final Pos textAlignment) {
        return createSelectableText(text, textColor, Fonts.avenirNextLtProRegular(fontSize), textAlignment);
    }
    public static final TextField createMediumSelectableText(final String text, final Color textColor, final double fontSize, final Pos textAlignment) {
        return createSelectableText(text, textColor, Fonts.avenirNextLtProMedium(fontSize), textAlignment);
    }
    public static final TextField createDemiSelectableText(final String text, final Color textColor, final double fontSize, final Pos textAlignment) {
        return createSelectableText(text, textColor, Fonts.avenirNextLtProDemi(fontSize), textAlignment);
    }
    public static final TextField createBoldSelectableText(final String text, final Color textColor, final double fontSize, final Pos textAlignment) {
        return createSelectableText(text, textColor, Fonts.avenirNextLtProBold(fontSize), textAlignment);
    }
    public static final TextField createHeavySelectableText(final String text, final Color textColor, final double fontSize, final Pos textAlignment) {
        return createSelectableText(text, textColor, Fonts.avenirNextLtProHeavy(fontSize), textAlignment);
    }
    public static final TextField createSelectableText(final String text, final Color textColor, final Font font, final Pos textAlignment) {
        final TextField textField = new TextField(text);
        textField.getStyleClass().add("selectable-text");
        textField.setEditable(false);
        textField.setFont(font);
        textField.setAlignment(textAlignment);
        textField.setFocusTraversable(false);
        return textField;
    }

    public static final TextArea createRegularTextArea(final String text, final Color textColor, final double fontSize) {
        final TextArea textArea = new TextArea(text);
        textArea.setFont(Fonts.avenirNextLtProRegular(fontSize));
        textArea.setWrapText(true);
        textArea.setEditable(true);
        textArea.setFocusTraversable(false);
        textArea.setPrefRowCount(5);
        return textArea;
    }

    public static final Region createRegion(final String styleClass) {
        Region region = new Region();
        region.getStyleClass().add(styleClass);
        return region;
    }

    public static final TextField createTextField(final String promptText, final String tooltipText, final double fontSize) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setTooltip(new Tooltip(tooltipText));
        textField.setFont(Fonts.avenirNextLtProMedium(fontSize));
        return textField;
    }

    public static final SearchTextField createSearchTextField(final String tooltipText, final double fontSize) {
        SearchTextField searchTextField = new SearchTextField();
        searchTextField.setTooltip(new Tooltip(tooltipText));
        searchTextField.setFont(Fonts.avenirNextLtProMedium(fontSize));
        return searchTextField;
    }

    public static final CheckBox createCheckBox(final String text, final double fontSize) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setFont(Fonts.avenirNextLtProMedium(fontSize));
        return checkBox;
    }

    public static final Region createSpacer(final Orientation orientation) {
        Region spacer = new Region();
        switch (orientation) {
            case HORIZONTAL ->  HBox.setHgrow(spacer, Priority.ALWAYS);
            case VERTICAL   -> VBox.setVgrow(spacer, Priority.ALWAYS);
        }
        return spacer;
    }
}
