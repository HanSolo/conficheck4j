package eu.hansolo.fx.conficheck4j.views;

import eu.hansolo.fx.conficheck4j.Main;
import eu.hansolo.fx.conficheck4j.data.ConferenceItem;
import eu.hansolo.fx.conficheck4j.data.ConfiModel;
import eu.hansolo.fx.conficheck4j.data.ProposalItem;
import eu.hansolo.fx.conficheck4j.flag.Flag;
import eu.hansolo.fx.conficheck4j.fonts.Fonts;
import eu.hansolo.fx.conficheck4j.tools.Constants;
import eu.hansolo.fx.conficheck4j.tools.Constants.AttendingStatus;
import eu.hansolo.fx.conficheck4j.tools.Constants.ProposalStatus;
import eu.hansolo.fx.conficheck4j.tools.Factory;
import eu.hansolo.fx.conficheck4j.tools.Helper;
import eu.hansolo.fx.conficheck4j.tools.IsoCountryCodes;
import eu.hansolo.fx.conficheck4j.tools.IsoCountryInfo;
import javafx.application.Platform;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * User: hansolo
 * Date: 22.02.25
 * Time: 06:55
 */
@DefaultProperty("children")
public class ConferenceView extends Region {
    public static final  double                         PREFERRED_WIDTH    = 540;
    public static final  double                         PREFERRED_HEIGHT   = 180;
    public static final  double                         MINIMUM_WIDTH      = 400;
    public static final  double                         MINIMUM_HEIGHT     = 50;
    public static final  double                         MAXIMUM_WIDTH      = 1024;
    public static final  double                         MAXIMUM_HEIGHT     = 1024;
    private              Main                           main;
    private              ConfiModel                     model;
    private              ObjectProperty<ConferenceItem> conference;
    private              List<ProposalItem>             proposals;
    private              IntegerProperty                selectedAttendenceIndex;
    private              BooleanProperty                proposalSelectionVisible;
    private              BooleanProperty                showAlert;
    private              StringProperty                 alertTitle;
    private              StringProperty                 alertMessage;
    private              Background                     hoverBackground;
    private              double                         size;
    private              String                         id;
    private              DateTimeFormatter              formatter;
    private              DateTimeFormatter              df;
    private              IsoCountryInfo                 isoInfo;
    private              Flag                           flag;
    private              double                         width;
    private              double                         height;
    private              VBox                           vBox;


    // ******************** Constructors **************************************
    public ConferenceView(final Main main, final ConfiModel model, final ConferenceItem conference, final List<ProposalItem> proposals) {
        this.main                     = main;
        this.model                    = model;
        this.conference               = new ObjectPropertyBase<>(conference) {
            @Override protected void invalidated() { }
            @Override public Object getBean()      { return ConferenceView.this; }
            @Override public String getName()      { return "conference"; }
        };
        this.proposals                = proposals;
        this.selectedAttendenceIndex  = new SimpleIntegerProperty(0);
        this.proposalSelectionVisible = new SimpleBooleanProperty(false);
        this.showAlert                = new SimpleBooleanProperty(false);
        this.alertTitle               = new SimpleStringProperty("");
        this.alertMessage             = new SimpleStringProperty("");
        this.id                       = UUID.randomUUID().toString();
        this.formatter                = DateTimeFormatter.ofPattern("dd MMMM");
        this.df                       = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
        this.isoInfo                  = IsoCountryCodes.searchByName(this.conference.get().getCountry()).orElse(null);
        this.flag                     = null == this.isoInfo ? Flag.NOT_FOUND : this.isoInfo.getFlag();
        this.hoverBackground          = new Background(new BackgroundFill(Color.color(0.95, 0.95, 0.95), CornerRadii.EMPTY, Insets.EMPTY));

        initGraphics();
        registerListeners();
    }


    // ******************** Initialization ************************************
    private void initGraphics() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 || Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }
        getStyleClass().add("conference-view");

        // Conference Name
        Text conferenceName = new Text(conference.get().getName());
        conferenceName.setFont(Fonts.avenirNextLtProMedium(16));

        // Country Flag
        Image flag;
        if (this.isoInfo == null) {
            flag = Flag.NOT_FOUND.getImage(); // Replace with network image
        } else {
            flag = this.isoInfo.getFlag().getImage(22);
        }
        ImageView flagImage = new ImageView(flag);
        flagImage.setFitWidth(20);
        flagImage.setFitHeight(20);

        HBox conferenceNameHBox = new HBox(conferenceName, Factory.createSpacer(Orientation.HORIZONTAL), flagImage);
        conferenceNameHBox.setAlignment(Pos.CENTER);

        // Conference Date
        Text daysText;
        ZonedDateTime date = ZonedDateTime.ofInstant(this.conference.get().getDate(), ZoneId.systemDefault());
        if (this.conference.get().getDays() > 1) {
            daysText = new Text(this.formatter.format(date) + " (" + String.format("%.0f", this.conference.get().getDays()) + " days)");
        } else {
            daysText = new Text(this.formatter.format(date));
        }
        daysText.setFont(Fonts.avenirNextLtProRegular(12));

        // City Name
        Text cityText = new Text(this.conference.get().getCity() + ((this.isoInfo != null ? this.isoInfo.name() : this.conference.get().getCountry()).isBlank() ? "" : ","));
        cityText.setFont(Fonts.avenirNextLtProRegular(12));
        cityText.setFill(Helper.getSecondaryColor());

        // Country Name
        Text countryText = new Text(this.isoInfo != null ? this.isoInfo.name() : this.conference.get().getCountry());
        countryText.setFont(Fonts.avenirNextLtProRegular(12));
        countryText.setFill(Helper.getSecondaryColor());

        HBox conferenceDateHBox = new HBox(daysText, Factory.createSpacer(Orientation.HORIZONTAL), cityText, countryText);
        conferenceDateHBox.setAlignment(Pos.CENTER);

        // Link to website
        HBox urlAndMapHBox = new HBox(5);
        urlAndMapHBox.setAlignment(Pos.CENTER);
        if (!this.conference.get().getUrl().isEmpty()) {
            Label webText = new Label("WEB");
            webText.setMinWidth(28);
            webText.setFont(Fonts.avenirNextLtProRegular(12));

            Region urlIcon = new Region();
            urlIcon.getStyleClass().add("url-icon");
            urlIcon.setFocusTraversable(false);
            urlIcon.setMinSize(16, 16);
            urlIcon.setMaxSize(16, 16);
            urlIcon.setPrefSize(16, 16);

            Button urlButton = Factory.createButton("", "Open conference website in default browser", 12);
            urlButton.setGraphic(urlIcon);
            urlButton.setOnAction(e -> openUrlInExternalBrowser(this.conference.get().getUrl()));
            urlButton.disableProperty().bind(this.model.networkMonitor.offlineProperty());

            urlAndMapHBox.getChildren().addAll(webText, urlButton);
        }
        urlAndMapHBox.getChildren().add(Factory.createSpacer(Orientation.HORIZONTAL));
        if (this.conference.get().getLat().isPresent() && this.conference.get().getLon().isPresent()) {
            Region mapIcon = new Region();
            mapIcon.getStyleClass().add("map-icon");
            mapIcon.setFocusTraversable(false);
            mapIcon.setMinSize(16, 16);
            mapIcon.setMaxSize(16, 16);
            mapIcon.setPrefSize(16, 16);

            Button mapButton = Factory.createButton("", "Open conference location in google maps in default browser", 12);
            mapButton.setGraphic(mapIcon);
            mapButton.setOnAction(e -> openUrlInExternalBrowser("https://www.google.com/maps/search/?api=1&query=" + conference.get().getLat().get() + "," + conference.get().getLon().get()));
            mapButton.disableProperty().bind(this.model.networkMonitor.offlineProperty());

            urlAndMapHBox.getChildren().add(mapButton);
        }

        // CfP and Attendence
        HBox cfpAndAttendenceHBox = new HBox(5);
        cfpAndAttendenceHBox.setAlignment(Pos.CENTER);
        if (this.conference.get().getCfpUrl().isPresent()) {
            Label cfpText = new Label("CFP");
            cfpText.setMinWidth(28);
            cfpText.setFont(Fonts.avenirNextLtProRegular(12));

            Region urlIcon = new Region();
            urlIcon.getStyleClass().add("url-icon");
            urlIcon.setFocusTraversable(false);
            urlIcon.setMinSize(16, 16);
            urlIcon.setMaxSize(16, 16);
            urlIcon.setPrefSize(16, 16);

            Button cfpUrlButton = Factory.createButton("", "Open conference cfp website in default browser", 12);
            cfpUrlButton.setGraphic(urlIcon);
            cfpUrlButton.setOnAction(e -> openUrlInExternalBrowser(this.conference.get().getCfpUrl().get()));
            cfpUrlButton.disableProperty().bind(this.model.networkMonitor.offlineProperty());

            cfpAndAttendenceHBox.getChildren().addAll(cfpText, cfpUrlButton);

            if (this.conference.get().getCfpDate().isPresent()) {
                Optional<Instant>[] optCfpDate = Helper.getDatesFromJavaConferenceDate(this.conference.get().getCfpDate().get());
                if (optCfpDate.length > 0 && optCfpDate[0].isPresent()) {
                    ZonedDateTime endDate      = ZonedDateTime.ofInstant(optCfpDate[0].get(), ZoneId.systemDefault());
                    Label         cfpDateLabel = new Label(df.format(endDate));
                    cfpDateLabel.setFont(Fonts.avenirNextLtProRegular(12));
                    cfpDateLabel.setPadding(new Insets(4, 10, 4, 10));
                    cfpDateLabel.setBackground(new Background(new BackgroundFill(Helper.getColorForCfpDate(endDate.toLocalDate()) , new CornerRadii(5), Insets.EMPTY)));
                    cfpDateLabel.setTextFill(Constants.WHITE);
                    cfpAndAttendenceHBox.getChildren().add(cfpDateLabel);
                }
            }
        }

        ComboBox<Constants.AttendingStatus> attendenceComboBox = new ComboBox<>();
        attendenceComboBox.getItems().addAll(Constants.AttendingStatus.values());
        attendenceComboBox.getSelectionModel().select(this.conference.get().getAttendence());
        attendenceComboBox.setCellFactory(_ -> new ListCell<>() {
            @Override protected void updateItem(Constants.AttendingStatus attendence, boolean empty) {
                super.updateItem(attendence, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(attendence.uiString);
                    setFont(Fonts.avenirNextLtProRegular(12));
                }
            }
        });
        attendenceComboBox.setConverter(new StringConverter<>() {
            @Override public String toString(final Constants.AttendingStatus attendence) { return null == attendence ? "" : attendence.uiString; }
            @Override public Constants.AttendingStatus fromString(final String s) { return AttendingStatus.fromText(s); }
        });
        attendenceComboBox.getEditor().setFont(Fonts.avenirNextLtProRegular(12));
        attendenceComboBox.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> this.conference.get().setAttendence(nv));
        cfpAndAttendenceHBox.getChildren().addAll(Factory.createSpacer(Orientation.HORIZONTAL), attendenceComboBox);
        cfpAndAttendenceHBox.setAlignment(Pos.CENTER);

        // Proposals
        Text proposalsText = new Text("Proposals");
        proposalsText.setFont(Fonts.avenirNextLtProMedium(12));

        Region plusIcon = new Region();
        plusIcon.getStyleClass().add("plus-icon");
        plusIcon.setFocusTraversable(false);
        plusIcon.setPrefSize(16, 16);
        plusIcon.setMinSize(16, 16);
        plusIcon.setMaxSize(16, 16);
        Tooltip.install(plusIcon, new Tooltip("Add proposal to conference"));

        Label addProposalLabel = new Label("", plusIcon);
        addProposalLabel.setGraphicTextGap(0);
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.setOnAction(e -> {
            final String selectedProposal = ((MenuItem) e.getTarget()).getText();
            this.proposals.stream()
                          .filter(proposal -> proposal.getTitle().equals(selectedProposal))
                          .findFirst()
                          .ifPresent(proposal -> this.conference.get().getProposals().put(proposal, ProposalStatus.NOT_SUBMITTED));
        });
        this.proposals.forEach(proposal -> {
            MenuItem menuItem = new MenuItem(proposal.getTitle());
            contextMenu.getItems().add(menuItem);
        });
        addProposalLabel.setContextMenu(contextMenu);

        HBox proposalsBox = new HBox(proposalsText, Factory.createSpacer(Orientation.HORIZONTAL), addProposalLabel);
        proposalsBox.setAlignment(Pos.CENTER);

        VBox proposedSessions = new VBox(5);
        proposedSessions.setPadding(new Insets(5, 0, 5, 0));
        updateConferenceProposals(proposedSessions);

        this.conference.get().getProposals().addListener((MapChangeListener<? super ProposalItem, ? super ProposalStatus>) _ -> updateConferenceProposals(proposedSessions));

        vBox = new VBox(10, conferenceNameHBox, conferenceDateHBox, urlAndMapHBox, cfpAndAttendenceHBox, proposalsBox, proposedSessions, new Separator(Orientation.HORIZONTAL));
        vBox.setFillWidth(true);

        getChildren().setAll(vBox);
        setPadding(new Insets(10));
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        addEventFilter(MouseEvent.MOUSE_ENTERED, e -> setBackground(hoverBackground));
        addEventFilter(MouseEvent.MOUSE_EXITED, e -> setBackground(Background.EMPTY));
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double width)  { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
    @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
    @Override protected double computeMaxWidth(final double height)  { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double width)  { return MAXIMUM_HEIGHT; }

    private void openUrlInExternalBrowser(final String url) {
        if (this.model.networkMonitor.isOnline()) {
            try {
                new ProcessBuilder("x-www-browser", url).start();
            } catch (IOException ex) {
                this.main.getHostServices().showDocument(url);
            }
        }
    }

    private void updateConferenceProposals(final VBox proposedSessions) {
        List<HBox> prpsd = new ArrayList<>();
        this.conference.get().getProposals().entrySet().forEach(entry -> {
            Text proposalText = new Text(entry.getKey().getTitle());
            proposalText.setTextAlignment(TextAlignment.LEFT);
            proposalText.setWrappingWidth(300);
            proposalText.setFont(Fonts.avenirNextLtProRegular(12));

            ComboBox<ProposalStatus> proposalStatusComboBox = new ComboBox<>();
            proposalStatusComboBox.getItems().addAll(ProposalStatus.values());
            proposalStatusComboBox.getSelectionModel().select(entry.getValue());
            proposalStatusComboBox.getEditor().setFont(Fonts.avenirNextLtProRegular(12));

            proposalStatusComboBox.setCellFactory(_ -> new ListCell<>() {
                @Override protected void updateItem(ProposalStatus status, boolean empty) {
                    super.updateItem(status, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(status.uiString);
                        setFont(Fonts.avenirNextLtProRegular(12));
                        switch (status) {
                            case NOT_SUBMITTED, SUBMITTED -> { }
                            case ACCEPTED                 -> { }
                            case REJECTED                 -> { }
                        }
                    }
                }
            });

            proposalStatusComboBox.setConverter(new StringConverter<>() {
                @Override public String toString(final ProposalStatus status) { return null == status ? "" : status.uiString; }
                @Override public ProposalStatus fromString(final String s) { return ProposalStatus.fromText(s); }
            });
            proposalStatusComboBox.getSelectionModel().selectedItemProperty().addListener((o, ov, nv) -> this.conference.get().getProposals().put(entry.getKey(), nv));
            HBox proposalStateBox = new HBox(proposalText, Factory.createSpacer(Orientation.HORIZONTAL), proposalStatusComboBox);
            proposalStateBox.setAlignment(Pos.CENTER);
            prpsd.add(proposalStateBox);
            setPrefHeight(PREFERRED_HEIGHT + prpsd.size() * 20);
        });
        proposedSessions.getChildren().setAll(prpsd);
    }


    // ******************** Layout *******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();
        size   = width < height ? width : height;

        if (width > 0 && height > 0) {
            vBox.setPrefSize(width, height);
            vBox.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);
        }
    }
}