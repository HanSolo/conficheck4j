package eu.hansolo.fx.conficheck4j.views;

import eu.hansolo.fx.conficheck4j.Main;
import eu.hansolo.fx.conficheck4j.data.ProposalItem;
import eu.hansolo.fx.conficheck4j.fonts.Fonts;
import eu.hansolo.fx.conficheck4j.tools.Constants;
import eu.hansolo.fx.conficheck4j.tools.Factory;
import eu.hansolo.fx.conficheck4j.tools.Helper;
import javafx.beans.DefaultProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static eu.hansolo.toolbox.Constants.NEW_LINE;


@DefaultProperty("children")
public class ProposalView extends Region {
    public static final double                       PREFERRED_WIDTH  = 360;
    public static final double                       PREFERRED_HEIGHT = 400;
    public static final double                       MINIMUM_WIDTH    = 360;
    public static final double                       MINIMUM_HEIGHT   = 50;
    public static final double                       MAXIMUM_WIDTH    = 1024;
    public static final double                       MAXIMUM_HEIGHT   = 400;
    private             Main                         main;
    private             StackPane                    copiedFeedbackPane;
    private             ProposalItem                 proposal;
    private             ObservableList<ProposalItem> proposals;
    private             double                       width;
    private             double                       height;
    private             VBox                         vBox;
    private             Clipboard                    clipboard;
    private             ClipboardContent             clipboardContent;



    // ******************** Constructors **************************************
    public ProposalView(final Main main, final StackPane copiedFeedbackPane, final ProposalItem proposal, final ObservableList<ProposalItem> proposals, final Clipboard clipboard, final ClipboardContent clipboardContent) {
        this.main               = main;
        this.copiedFeedbackPane = copiedFeedbackPane;
        this.proposal           = null == proposal ? new ProposalItem("", "", "") : proposal;
        this.proposals          = proposals;
        this.clipboard          = clipboard;
        this.clipboardContent   = clipboardContent;

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

        // Proposal
        Label     proposalTitleLabel     = Factory.createLabel("Title", Constants.GRAY, Fonts.avenirNextLtProDemi(Constants.STD_FONT_SIZE), Pos.CENTER_LEFT);
        TextField proposalTitleTextField = Factory.createTextField("Title", "Title of session proposal", Constants.STD_FONT_SIZE);
        Text      noOfWordsInTitleText   = new Text("(" + proposal.getTitle().length() + " characters, " + Helper.countWords(proposal.getTitle()) + " words)");
        noOfWordsInTitleText.setFont(Fonts.avenirNextLtProRegular(10));
        VBox      proposalTitleBox       = new VBox(2, proposalTitleLabel, proposalTitleTextField, noOfWordsInTitleText);
        proposalTitleBox.setAlignment(Pos.CENTER_LEFT);
        proposalTitleTextField.setText(proposal.getTitle());
        proposalTitleTextField.focusedProperty().addListener((o, ov, nv) -> {
            if (!nv) {
                this.proposal.setTitle(proposalTitleTextField.getText());
                noOfWordsInTitleText.setText("(" + proposalTitleTextField.getText().length() + " characters, " + Helper.countWords(proposalTitleTextField.getText()) + " words)");
            }
        });

        Label    proposalAbstractLabel    = Factory.createLabel("Abstract", Constants.GRAY, Fonts.avenirNextLtProDemi(Constants.STD_FONT_SIZE), Pos.CENTER_LEFT);
        TextArea proposalAbstractTextArea = Factory.createRegularTextArea("Abstract of session proposal", Constants.BLACK, Constants.STD_FONT_SIZE);
        Text     noOfWordsInAbstractText  = new Text("(" + proposal.getAbstract().length() + " characters, " + Helper.countWords(proposal.getAbstract()) + " words)");
        noOfWordsInAbstractText.setFont(Fonts.avenirNextLtProRegular(10));
        VBox     proposalAbstractBox      = new VBox(2, proposalAbstractLabel, proposalAbstractTextArea, noOfWordsInAbstractText);
        proposalAbstractBox.setAlignment(Pos.CENTER_LEFT);
        proposalAbstractTextArea.setPrefRowCount(8);
        proposalAbstractTextArea.setText(proposal.getAbstract());
        proposalAbstractTextArea.focusedProperty().addListener((o, ov, nv) -> {
            if (!nv) {
                this.proposal.setAbstract(proposalAbstractTextArea.getText().replaceAll("\\n", " "));
                noOfWordsInAbstractText.setText("(" + proposalAbstractTextArea.getText().length() + " characters, " + Helper.countWords(proposalAbstractTextArea.getText()) + " words)");
            }
        });

        Label    proposalPitchLabel    = Factory.createLabel("Pitch", Constants.GRAY, Fonts.avenirNextLtProDemi(Constants.STD_FONT_SIZE), Pos.CENTER_LEFT);
        TextArea proposalPitchTextArea = Factory.createRegularTextArea("Pitch of session proposal", Constants.BLACK, Constants.STD_FONT_SIZE);
        Text     noOfWordsInPitchText  = new Text("(" + proposal.getPitch().length() + " characters, " + Helper.countWords(proposal.getPitch()) + " words)");
        noOfWordsInPitchText.setFont(Fonts.avenirNextLtProRegular(10));
        VBox     proposalPitchBox      = new VBox(2, proposalPitchLabel, proposalPitchTextArea, noOfWordsInPitchText);
        proposalPitchBox.setAlignment(Pos.CENTER_LEFT);
        proposalPitchTextArea.setPrefRowCount(8);
        proposalPitchTextArea.setText(proposal.getPitch());
        proposalPitchTextArea.focusedProperty().addListener((o, ov, nv) -> {
            if (!nv) {
                this.proposal.setPitch(proposalPitchTextArea.getText().replaceAll("\\n", " "));
                noOfWordsInPitchText.setText("(" + proposalPitchTextArea.getText().length() + " characters, " + Helper.countWords(proposalPitchTextArea.getText()) + " words)");
            }
        });

        Region arrowDownLeftIcon = new Region();
        arrowDownLeftIcon.getStyleClass().add("arrow-down-icon");
        arrowDownLeftIcon.setFocusTraversable(false);
        arrowDownLeftIcon.setPrefSize(20, 20);
        arrowDownLeftIcon.setMinSize(20, 20);
        arrowDownLeftIcon.setMaxSize(20, 20);

        Region copyProposalIcon = new Region();
        copyProposalIcon.getStyleClass().add("copy-icon");
        copyProposalIcon.setFocusTraversable(false);
        copyProposalIcon.setPrefSize(20, 20);
        copyProposalIcon.setMinSize(20, 20);
        copyProposalIcon.setMaxSize(20, 20);
        copyProposalIcon.setStyle("-fx-background-color: white;");

        Label copyProposalLabel = Factory.createLabel("Copy proposal", Color.WHITE, Fonts.avenirNextLtProRegular(Constants.STD_FONT_SIZE), Pos.CENTER);
        copyProposalLabel.setGraphic(copyProposalIcon);

        Region arrowDownRightIcon = new Region();
        arrowDownRightIcon.getStyleClass().add("arrow-down-icon");
        arrowDownRightIcon.setFocusTraversable(false);
        arrowDownRightIcon.setPrefSize(20, 20);
        arrowDownRightIcon.setMinSize(20, 20);
        arrowDownRightIcon.setMaxSize(20, 20);

        HBox proposalHeaderBox = new HBox(arrowDownLeftIcon, Factory.createSpacer(Orientation.HORIZONTAL), copyProposalLabel, Factory.createSpacer(Orientation.HORIZONTAL), arrowDownRightIcon);
        proposalHeaderBox.setBackground(new Background(new BackgroundFill(Constants.GRAY, new CornerRadii(10, 10, 00, 0, false), Insets.EMPTY)));
        proposalHeaderBox.setPadding(new Insets(10));
        proposalHeaderBox.setAlignment(Pos.CENTER);
        proposalHeaderBox.setOnMousePressed(e -> {
            final StringBuilder proposalBuilder = new StringBuilder();
            proposalBuilder.append("Title").append(NEW_LINE)
                           .append(proposalTitleTextField.getText()).append(NEW_LINE)
                           .append(NEW_LINE)
                           .append("Abstract").append(NEW_LINE)
                           .append(proposalAbstractTextArea.getText()).append(NEW_LINE)
                           .append(NEW_LINE)
                           .append("Pitch").append(NEW_LINE)
                           .append(proposalPitchTextArea.getText());

            clipboard.clear();
            clipboardContent.clear();
            clipboardContent.putString(proposalBuilder.toString());
            clipboard.setContent(clipboardContent);
            main.fadeOutPane(copiedFeedbackPane);
        });
        Tooltip.install(proposalHeaderBox, new Tooltip("Copy proposal to clipboard"));
        //VBox.setMargin(proposalHeaderBox, new Insets(0, 0, -10, 0));

        // Delete proposal
        Region trashIcon = new Region();
        trashIcon.getStyleClass().add("trash-icon");
        trashIcon.setFocusTraversable(false);
        trashIcon.setPrefSize(20, 20);
        trashIcon.setMinSize(20, 20);
        trashIcon.setMaxSize(20, 20);
        trashIcon.setOnMousePressed(e -> this.proposals.remove(this.proposal));
        Tooltip.install(trashIcon, new Tooltip("Remove proposal"));

        HBox headerBox = new HBox(5, proposalHeaderBox, trashIcon);
        headerBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(proposalHeaderBox, Priority.ALWAYS);
        vBox = new VBox(10, headerBox, proposalTitleBox, proposalAbstractBox, proposalPitchBox);
        vBox.setFillWidth(true);

        getChildren().setAll(vBox);
        setPadding(new Insets(10));
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double width)  { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
    @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
    @Override protected double computeMaxWidth(final double height)  { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double width)  { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }


    // ******************** Layout *******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (width > 0 && height > 0) {
            vBox.setMaxSize(width, height);
            vBox.setPrefSize(width, height);
            vBox.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);
        }
    }
}
