package eu.hansolo.fx.conficheck4j.data;

import eu.hansolo.fx.conficheck4j.tools.Constants;
import eu.hansolo.fx.conficheck4j.tools.Helper;
import eu.hansolo.fx.conficheck4j.tools.NetworkMonitor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;


public class ConfiModel {
    public final NetworkMonitor                                  networkMonitor          = NetworkMonitor.INSTANCE;
    public final ObservableList<ConferenceItem>                  conferences             = FXCollections.observableArrayList();
    public final ObservableMap<Integer, TreeSet<ConferenceItem>> conferencesPerMonth     = FXCollections.observableHashMap();
    public final ObservableMap<Integer, TreeSet<ConferenceItem>> conferencesPerContinent = FXCollections.observableHashMap();
    public final ObservableMap<Integer, TreeSet<ConferenceItem>> conferencesWithOpenCfp  = FXCollections.observableHashMap();
    public final ObservableMap<Integer, TreeSet<ConferenceItem>> filteredConferences     = FXCollections.observableHashMap();
    public final ObservableList<ProposalItem>                    proposals               = FXCollections.observableArrayList();
    public final ObservableMap<String, Integer>                  attendence              = FXCollections.observableHashMap();
    public       ObjectProperty<ConferenceItem>                  selectedConference      = new ObjectPropertyBase<>() {
        @Override protected void invalidated() { }
        @Override public Object getBean()      { return ConfiModel.this; }
        @Override public String getName()      { return "selectedConference"; }
    };
    public       ObjectProperty<ProposalItem>                 selectedProposal        = new ObjectPropertyBase<>() {
        @Override protected void invalidated() { }
        @Override public Object getBean()      { return ConfiModel.this; }
        @Override public String getName()      { return "selectedProposal"; }
    };
    public       BooleanProperty                              update                  = new SimpleBooleanProperty(false);


    public ConfiModel() {
        loadConferenceItems();
        registerListeners();
    }


    private void registerListeners() {
        conferences.addListener(new ListChangeListener<ConferenceItem>() {
            @Override public void onChanged(final Change<? extends ConferenceItem> change) {
                /*
                while (change.next()) {
                    change.getAddedSubList().forEach(conference -> {});
                    change.getRemoved().forEach(conference -> {});
                }
                */
                //Helper.saveConferenceItems(conferences);
            }
        });
    }


    private void loadConferenceItems() {
        String jsonText = "";
        try {
            jsonText = Helper.readTextFile(Constants.HOME_FOLDER + Constants.CONFERENCE_ITEMS_FILENAME, Charset.forName("UTF-8"));
        } catch (IOException e) { }
        if (null == jsonText || jsonText.isBlank()) { return; }
        final List<ConferenceItem> conferenceItems = Helper.parseConferenceItemsJson(jsonText);
        conferences.setAll(conferenceItems);
        return;
    }

    public final void update(final List<JavaConference> javaConferences) {
        List<ConferenceItem> conferencesToAdd = new ArrayList<>();
        javaConferences.forEach(javaConference -> {
            Optional<ConferenceItem> optConference = this.conferences.stream()
                                                                     .filter(conference -> conference.getName().equals(javaConference.name()))
                                                                     .filter(conference -> conference.getUrl().equals(javaConference.link()))
                                                                     .findFirst();
            if (optConference.isPresent()) {
                final Optional<Instant>[] dates   = Helper.getDatesFromJavaConferenceDate(javaConference.date());
                final Instant             date    = dates[0].isPresent() ? dates[0].get() : Instant.MIN;
                final Instant             endDate = dates[1].isPresent() ? dates[1].get() : date;
                final double              days    = Helper.getDaysBetweenDates(date, endDate);
                optConference.get().setUrl(javaConference.link());
                optConference.get().setCfpUrl(Optional.of(javaConference.cfpLink()));
                optConference.get().setCfpDate(Optional.of(javaConference.cfpEndDate()));
                optConference.get().setDate(date);
                optConference.get().setDays(days);
            } else {
                conferencesToAdd.add(javaConference.convertToConferenceItem());
            }
        });
        this.conferences.addAll(conferencesToAdd);
        Helper.saveConferenceItems(this.conferences);
        this.conferencesPerMonth.clear();
        this.conferences.forEach(conference -> {
            final ZonedDateTime date  = ZonedDateTime.ofInstant(conference.getDate(), ZoneId.systemDefault());
            final Integer       month = date.get(ChronoField.MONTH_OF_YEAR);
            if (!conferencesPerMonth.containsKey(month)) { conferencesPerMonth.put(month, new TreeSet<>()); }
            conferencesPerMonth.get(month).add(conference);
        });
        this.conferencesPerContinent.clear();
        this.filteredConferences.clear();
        this.filteredConferences.putAll(conferencesPerMonth);
        this.update.set(!this.update.get());
    }
}
