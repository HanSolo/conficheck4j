package eu.hansolo.fx.conficheck4j.views;

import eu.hansolo.fx.conficheck4j.Main;
import eu.hansolo.fx.conficheck4j.data.ConferenceItem;
import eu.hansolo.fx.conficheck4j.data.ConfiModel;
import eu.hansolo.fx.conficheck4j.fonts.Fonts;
import eu.hansolo.fx.conficheck4j.tools.Constants;
import javafx.beans.DefaultProperty;
import javafx.collections.ObservableList;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.List;


@DefaultProperty("children")
public class CalendarView extends Region {
    private static final double            PREFERRED_WIDTH  = 540;
    private static final double            PREFERRED_HEIGHT = 150;
    private static final double            MINIMUM_WIDTH    = 400;
    private static final double            MINIMUM_HEIGHT   = 50;
    private static final double            MAXIMUM_WIDTH    = 1024;
    private static final double            MAXIMUM_HEIGHT   = 1024;
    private              Main              main;
    private              ConfiModel        model;
    private              DateTimeFormatter formatter;
    private              double            width;
    private              double            height;
    private              Canvas            canvas;
    private              GraphicsContext   ctx;
    private              ScrollPane        scrollPane;


    // ******************** Constructors **************************************
    public CalendarView(final Main main, final ConfiModel model) {
        this.main      = main;
        this.model     = model;
        this.formatter = DateTimeFormatter.ofPattern("d M");
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

        canvas     = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ctx        = canvas.getGraphicsContext2D();
        scrollPane = new ScrollPane();
        scrollPane.setContent(canvas);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setMaxHeight(630);

        getChildren().setAll(scrollPane);
        //setPadding(new Insets(10));
    }

    private void registerListeners() {
        widthProperty().addListener(o -> resize());
        heightProperty().addListener(o -> resize());
        setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                scrollPane.setHvalue(0.25);
            }
        });
    }


    // ******************** Methods *******************************************
    @Override protected double computeMinWidth(final double height) { return MINIMUM_WIDTH; }
    @Override protected double computeMinHeight(final double width)  { return MINIMUM_HEIGHT; }
    @Override protected double computePrefWidth(final double height) { return super.computePrefWidth(height); }
    @Override protected double computePrefHeight(final double width) { return super.computePrefHeight(width); }
    @Override protected double computeMaxWidth(final double height)  { return MAXIMUM_WIDTH; }
    @Override protected double computeMaxHeight(final double width)  { return MAXIMUM_HEIGHT; }

    @Override public ObservableList<Node> getChildren() { return super.getChildren(); }

    public void setToInitialPosition() { scrollPane.setHvalue(0.25); }

    private void openUrlInExternalBrowser(final String url) {
        if (this.model.networkMonitor.isOnline()) {
            try {
                new ProcessBuilder("x-www-browser", url).start();
            } catch (IOException ex) {
                this.main.getHostServices().showDocument(url);
            }
        }
    }


    // ******************** Layout *******************************************
    private void resize() {
        width  = getWidth() - getInsets().getLeft() - getInsets().getRight();
        height = getHeight() - getInsets().getTop() - getInsets().getBottom();

        if (width > 0 && height > 0) {
            canvas.setWidth(width / 4.0 * 16.0);
            canvas.setHeight(height);
            scrollPane.setPrefSize(width, height);
            scrollPane.relocate((getWidth() - width) * 0.5, (getHeight() - height) * 0.5);
            redraw();
        }
    }

    private void redraw() {
        final Color  fgdColor            = Constants.BLACK;
        final double width               = canvas.getWidth();
        final double height              = canvas.getHeight();
        final double scaleXFontSize      = height * 0.08;
        final Font   scaleXFont          = Fonts.avenirNextLtProRegular(scaleXFontSize);
        final double topY                = scaleXFontSize * 1.8;
        final int    numberOfVisibleDays = 16 * 7;
        final double tickStepX           = width / numberOfVisibleDays;
        final long   minDate             = LocalDate.now().atStartOfDay().toEpochSecond(ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now())) - (Constants.SECONDS_PER_WEEK * 4);
        final double fourWeeks           = 28 * tickStepX;
        int maxNoOfConferencesPerMonth = 0;
        for (final List<ConferenceItem> conferences : this.model.conferencesPerMonth.values()) {
            maxNoOfConferencesPerMonth = Math.max(conferences.size(), maxNoOfConferencesPerMonth);
        }

        final double availableHeight = height - topY;
        final double scaleY          = availableHeight / maxNoOfConferencesPerMonth; //Helper.clamp(2, 10, availableHeight / maxNoOfConferencesPerMonth);
        final double rectOffsetY     = scaleY * 0.1;
        final double rectHeight      = scaleY * 0.8;
        final double valueFontSize   = scaleY;
        final Font   valueFont       = Fonts.avenirNextLtProRegular(valueFontSize);

        ctx.clearRect(0, 0, width, height);
        // Draw the top xAxis
        ctx.setStroke(Constants.GRAY);
        ctx.strokeLine(0, topY, width, topY);

        for (int n = 0 ; n < numberOfVisibleDays ; n++) {
            final ZonedDateTime date      = ZonedDateTime.ofInstant(Instant.ofEpochSecond(minDate + n * Constants.SECONDS_PER_DAY), ZoneId.systemDefault());
            final int           day       = date.get(ChronoField.DAY_OF_MONTH);
            final double        x         = n * tickStepX;
            final boolean       isToday   = (int) x == (int) fourWeeks;
            final double        lineWidth = day == 1 ? 1.0 : isToday ? 0.5 : 0.25;
            ctx.setStroke(isToday ? Constants.RED : Constants.GRAY);
            ctx.setLineWidth(lineWidth);
            ctx.strokeLine(x, topY, x, topY + availableHeight);

            if (n % 3 == 0 && n != 0 && n != numberOfVisibleDays - 1) {
                ctx.setFont(scaleXFont);
                ctx.setTextAlign(TextAlignment.CENTER);
                ctx.setTextBaseline(VPos.CENTER);
                ctx.setFill(fgdColor);
                ctx.fillText(formatter.format(date), x, topY * 0.5);
            }

            final int  month      = date.getMonth().getValue();
            final long startOfDay = date.toLocalDate().atStartOfDay().toEpochSecond(ZoneId.systemDefault().getRules().getOffset(date.toLocalDateTime()));
            int  confCount  = 0;
            if (this.model.conferencesPerMonth.isEmpty()) { return; }

            for (final ConferenceItem conference : this.model.conferencesPerMonth.get(month)) {
                ZonedDateTime d = ZonedDateTime.ofInstant(conference.getDate(), ZoneId.systemDefault());
                final long   startDate = d.toLocalDate().atStartOfDay().toEpochSecond(ZoneOffset.systemDefault().getRules().getOffset(d.toLocalDateTime()));
                final double length    = tickStepX * conference.getDays();
                //System.out.println(startDate + " >= " + startDate + " && " + (startOfDay + Constants.SECONDS_PER_DAY) + " <= " + (startOfDay + Constants.SECONDS_PER_DAY));
                if (startDate >= startOfDay && startDate + Constants.SECONDS_PER_DAY <= startOfDay + Constants.SECONDS_PER_DAY) {
                    Color fillColor;
                    switch (conference.getAttendence()) {
                        case  ATTENDING -> fillColor = Constants.ORANGE;
                        case  SPEAKING  -> fillColor = Constants.GREEN;
                        default         -> fillColor = Constants.PURPLE;
                    }
                    final double y = topY + confCount * scaleY;
                    ctx.setFill(fillColor);
                    ctx.beginPath();
                    ctx.rect(x, y + rectOffsetY, length, rectHeight);
                    ctx.closePath();
                    ctx.fill();

                    ctx.setTextAlign(TextAlignment.LEFT);
                    ctx.setFont(valueFont);
                    ctx.setFill(fgdColor);
                    ctx.fillText(conference.getName(), x + length + 0.5, y + scaleY * 0.5);
                }
                confCount += 1.0;
            }
        }
    }
}
