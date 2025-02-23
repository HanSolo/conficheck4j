package eu.hansolo.fx.conficheck4j;

import eu.hansolo.fx.conficheck4j.data.ConferenceItem;
import eu.hansolo.fx.conficheck4j.data.ConfiModel;
import eu.hansolo.fx.conficheck4j.data.JavaConference;
import eu.hansolo.fx.conficheck4j.fonts.Fonts;
import eu.hansolo.fx.conficheck4j.tools.Constants;
import eu.hansolo.fx.conficheck4j.tools.Constants.AttendingStatus;
import eu.hansolo.fx.conficheck4j.tools.Constants.Continent;
import eu.hansolo.fx.conficheck4j.tools.Constants.Filter;
import eu.hansolo.fx.conficheck4j.tools.Factory;
import eu.hansolo.fx.conficheck4j.tools.Helper;
import eu.hansolo.fx.conficheck4j.tools.IsoCountries;
import eu.hansolo.fx.conficheck4j.tools.PersistentToggleGroup;
import eu.hansolo.fx.conficheck4j.tools.PropertyManager;
import eu.hansolo.fx.conficheck4j.views.ConferenceView;
import eu.hansolo.jdktools.versioning.VersionNumber;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


public class Main extends Application {
    public static final VersionNumber             VERSION               = PropertyManager.INSTANCE.getVersionNumber();
    private             ConfiModel                model;
    private             Popup                     searchResultPopup;
    private             ComboBox<String>          continentsComboBox;
    private             PersistentToggleGroup     filterToggleGroup;
    private             ToggleButton              allToggleButton       = Factory.createToggleButton(Filter.ALL.getName(), Constants.STD_FONT_SIZE);
    private             ToggleButton              speakingToggleButton  = Factory.createToggleButton(Filter.SPEAKING.getName(), Constants.STD_FONT_SIZE);
    private             ToggleButton              attendingToggleButton = Factory.createToggleButton(Filter.ATTENDING.getName(), Constants.STD_FONT_SIZE);
    private             ToggleButton              cfpOpenToggleButton   = Factory.createToggleButton(Filter.CFP_OPEN.getName(), Constants.STD_FONT_SIZE);
    private             VBox                      conferencesVBox;
    private             VBox                      vBox;
    private             StackPane                 pane;
    private             ObjectProperty<Continent> selectedContinent;
    private             ObjectProperty<Filter>    selectedFilter;


    @Override public void init() {
        this.model = new ConfiModel();

        // Continents
        Text continentText = Factory.createText("Continent", Color.BLACK, Fonts.avenirNextLtProRegular(Constants.STD_FONT_SIZE));

        continentsComboBox = new ComboBox<>();
        for(Continent continent : Continent.values()) {
            continentsComboBox.getItems().add(continent.name);
        }
        continentsComboBox.getSelectionModel().select(0);
        continentsComboBox.getItems().forEach(continent -> continentText.setFont(Fonts.avenirNextLtProRegular(Constants.STD_FONT_SIZE)));
        HBox continentBox = new HBox(5, continentText, continentsComboBox);
        continentBox.setAlignment(Pos.BASELINE_LEFT);

        // Filters
        allToggleButton.getStyleClass().add("left-pill");
        speakingToggleButton.getStyleClass().add("center-pill");
        attendingToggleButton.getStyleClass().add("center-pill");
        cfpOpenToggleButton.getStyleClass().add("right-pill");

        filterToggleGroup = new PersistentToggleGroup();
        filterToggleGroup.getToggles().add(allToggleButton);
        filterToggleGroup.getToggles().add(speakingToggleButton);
        filterToggleGroup.getToggles().add(attendingToggleButton);
        filterToggleGroup.getToggles().add(cfpOpenToggleButton);

        allToggleButton.setSelected(true);
        HBox filterButtons = new HBox(0, Factory.createSpacer(Orientation.HORIZONTAL), allToggleButton, speakingToggleButton, attendingToggleButton, cfpOpenToggleButton, Factory.createSpacer(Orientation.HORIZONTAL));


        // Conferences
        conferencesVBox = new VBox(0);
        conferencesVBox.setAlignment(Pos.CENTER);
        conferencesVBox.setFillWidth(true);
        ScrollPane scrollPane = new ScrollPane(conferencesVBox);
        //scrollPane.setFitToHeight(true);
        //scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        vBox = new VBox(10, continentBox, filterButtons, scrollPane);

        this.pane = new StackPane(vBox);
        pane.getStyleClass().add("confi-check");
        //pane.setStyle("-fx-base: " + (eu.hansolo.fx.conficheck4j.tools.Constants.IS_DARK_MODE ? "#202020" : "#ececec"));
        pane.setPadding(new Insets(10, 10, 10, 10));

        this.selectedContinent = new ObjectPropertyBase<>(Continent.ALL) {
            @Override protected void invalidated() { updateView(); }
            @Override public Object getBean() { return Main.this; }
            @Override public String getName() { return "selectedContinent"; }
        };
        this.selectedFilter = new ObjectPropertyBase<>(Filter.ALL) {
            @Override protected void invalidated() { updateView(); }
            @Override public Object getBean() { return Main.this; }
            @Override public String getName() { return "selectedFilter"; }
        };

        registerListeners();
    }

    private void registerListeners() {
        this.model.update.addListener(o -> updateView());
        this.continentsComboBox.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> this.selectedContinent.set(Constants.Continent.fromText(nv)));
        this.allToggleButton.selectedProperty().addListener((o, ov, nv) -> this.selectedFilter.set(Filter.ALL));
        this.speakingToggleButton.selectedProperty().addListener((o, ov, nv) -> this.selectedFilter.set(Filter.SPEAKING));
        this.attendingToggleButton.selectedProperty().addListener((o, ov, nv) -> this.selectedFilter.set(Filter.ATTENDING));
        this.cfpOpenToggleButton.selectedProperty().addListener((o, ov, nv) -> this.selectedFilter.set(Filter.CFP_OPEN));
    }

    private void initOnFXApplicationThread() {
        searchResultPopup   = new Popup();
        searchResultPopup.getScene().getStylesheets().add(Main.class.getResource("conficheck4j.css").toExternalForm());
    }

    @Override public void start(final Stage stage) {
        initOnFXApplicationThread();

        Scene scene = new Scene(pane, 600, 400);
        scene.getStylesheets().add(Main.class.getResource("conficheck4j.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("ConfiCheck " + Main.VERSION);
        stage.setResizable(false);
        stage.show();
        stage.centerOnScreen();

        fetchConferences();
    }

    @Override public void stop() {
        Platform.exit();
        System.exit(0);
    }

    private void updateView() {
        Platform.runLater(() -> {
            List<String>         countriesInContinent   = Continent.ALL == this.selectedContinent.get() ? IsoCountries.ALL_COUNTRIES.stream().map(isoCountryInfo -> isoCountryInfo.name()).toList() : IsoCountries.ALL_COUNTRIES.stream().filter(country -> country.continent().equals(selectedContinent.get().code)).map(isoCountryInfo -> isoCountryInfo.name()).toList();
            List<ConferenceItem> conferencesInContinent = this.model.conferences.stream().filter(conference -> countriesInContinent.contains(conference.getCountry())).toList();
            this.model.conferencesPerMonth.clear();
            this.model.conferencesPerContinent.clear();
            conferencesInContinent.forEach(conference -> {
                final ZonedDateTime date  = ZonedDateTime.ofInstant(conference.getDate(), ZoneId.systemDefault());
                final Integer       month = date.get(ChronoField.MONTH_OF_YEAR);
                if (!this.model.conferencesPerMonth.containsKey(month)) {
                    this.model.conferencesPerMonth.put(month, new ArrayList<>());
                    this.model.conferencesPerContinent.put(month, new ArrayList<>());
                }
                this.model.conferencesPerMonth.get(month).add(conference);
                this.model.conferencesPerContinent.get(month).add(conference);
            });

            switch (this.selectedFilter.get()) {
                case ALL       -> {
                    this.model.filteredConferences.clear();
                    this.model.filteredConferences.putAll(this.model.conferencesPerContinent);
                }
                case SPEAKING  -> {
                    this.model.filteredConferences.clear();
                    conferencesInContinent.forEach(conference -> {
                        final ZonedDateTime date  = ZonedDateTime.ofInstant(conference.getDate(), ZoneId.systemDefault());
                        final Integer       month = date.get(ChronoField.MONTH_OF_YEAR);
                        if (this.model.attendence.containsKey(conference.getId())) {
                            if (this.model.attendence.get(conference.getId()) != AttendingStatus.SPEAKING.id) { return; }
                            if (!this.model.filteredConferences.containsKey(month)) { this.model.filteredConferences.put(month, new ArrayList<>()); }
                            this.model.filteredConferences.get(month).add(conference);
                        }
                    });
                }
                case ATTENDING -> {
                    this.model.filteredConferences.clear();
                    conferencesInContinent.forEach(conference -> {
                        final ZonedDateTime date  = ZonedDateTime.ofInstant(conference.getDate(), ZoneId.systemDefault());
                        final Integer       month = date.get(ChronoField.MONTH_OF_YEAR);
                        if (this.model.attendence.containsKey(conference.getId())) {
                            if (this.model.attendence.get(conference.getId()) != AttendingStatus.ATTENDING.id) { return; }
                            if (!this.model.filteredConferences.containsKey(month)) { this.model.filteredConferences.put(month, new ArrayList<>()); }
                            this.model.filteredConferences.get(month).add(conference);
                        }
                    });
                }
                case CFP_OPEN  -> {
                    for (Integer month : this.model.conferencesPerContinent.keySet()) {
                        if (this.model.conferencesPerContinent.get(month).isEmpty()) { continue; }
                        this.model.filteredConferences.put(month, this.model.conferencesPerContinent.get(month)
                                                                                                      .stream()
                                                                                                      .filter(conference -> conference.getCfpDate().isPresent())
                                                                                                      .filter(conference -> Helper.getDatesFromJavaConferenceDate(conference.getCfpDate().get()).length > 0 && Helper.getDatesFromJavaConferenceDate(conference.getCfpDate().get())[0].isPresent())
                                                                                                      .filter(conference -> Helper.isCfpOpen(ZonedDateTime.ofInstant(Helper.getDatesFromJavaConferenceDate(conference.getCfpDate().get())[0].get(), ZoneId.systemDefault()).toLocalDate()))
                                                                                                      .toList());
                    }
                }
            }

            final int currentMonth = LocalDate.now().getMonthValue();
            List<TitledPane> filtered  = new ArrayList<>();
            this.model.filteredConferences.entrySet().forEach(entry -> {
                final Integer              month              = entry.getKey();
                final List<ConferenceItem> conferencesInMonth = entry.getValue();
                VBox monthBox = new VBox();
                conferencesInMonth.forEach(conference -> monthBox.getChildren().add(new ConferenceView(Main.this, this.model, conference)));
                TitledPane monthPane = new TitledPane(Constants.MONTHS[month - 1], monthBox);
                monthPane.setCollapsible(true);
                switch (this.selectedFilter.get()) {
                    case ALL                            -> monthPane.setExpanded(month == currentMonth);
                    case SPEAKING, ATTENDING, CFP_OPEN  -> monthPane.setExpanded(!this.model.filteredConferences.get(month).isEmpty());
                }
                filtered.add(monthPane);
            });
            conferencesVBox.getChildren().setAll(filtered);
        });
    }

    private void fetchConferences() {
        try {
            String               jsonText        = Helper.readTextFile("/Users/hansolo/Desktop/javaconferences.json", Charset.defaultCharset());
            //String               jsonText        = Helper.getTextFromUrl(Constants.JAVA_CONFERENCES_JSON_URL);
            List<JavaConference> conferences     = Helper.parseJavaConferencesJson(jsonText);
            this.model.update(conferences);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}