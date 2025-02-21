package eu.hansolo.fx.conficheck4j;

import eu.hansolo.fx.conficheck4j.data.ConferenceItem;
import eu.hansolo.fx.conficheck4j.data.JavaConference;
import eu.hansolo.fx.conficheck4j.tools.Helper;
import eu.hansolo.toolbox.Constants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Main extends Application {

    private StackPane pane;

    @Override public void init() {
        Label label = new Label("Hello World!");

        pane = new StackPane(label);
    }

    @Override public void start(final Stage stage) {
        Scene scene = new Scene(pane);

        stage.setScene(scene);
        stage.setTitle("ConfiCheck");
        stage.show();
        stage.centerOnScreen();
    }

    @Override public void stop() {
        Platform.exit();
        System.exit(0);
    }



    public static void main(String[] args) {
        try {
            String               jsonText        = Helper.readTextFile("/Users/hansolo/Desktop/javaconferences.json", Charset.defaultCharset());
            List<JavaConference> conferences     = Helper.parseJavaConferencesJson(jsonText);
            List<ConferenceItem> conferenceItems = conferences.stream().map(conference -> conference.convertToConferenceItem()).collect(Collectors.toList());;
            Helper.saveTextFile(new StringBuilder().append(conferenceItems.stream().map(conferenceItem -> conferenceItem.toJsonString()).collect(Collectors.joining(Constants.COMMA, Constants.SQUARE_BRACKET_OPEN, Constants.SQUARE_BRACKET_CLOSE))).toString(), Constants.HOME_FOLDER + eu.hansolo.fx.conficheck4j.tools.Constants.CONFERENCE_ITEMS_FILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }

        launch(args);
    }
}