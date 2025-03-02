package eu.hansolo.fx.conficheck4j.data;

import eu.hansolo.fx.conficheck4j.tools.Constants;
import eu.hansolo.fx.conficheck4j.tools.Constants.ProposalStatus;
import eu.hansolo.fx.conficheck4j.tools.Helper;
import eu.hansolo.fx.conficheck4j.tools.NetworkMonitor;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.io.File;
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
    public final NetworkMonitor                                 networkMonitor          = NetworkMonitor.INSTANCE;
    public final ObservableList<ConferenceItem>                 conferences             = FXCollections.observableArrayList();
    public final ObservableMap<Integer, List<ConferenceItem>>   conferencesPerMonth     = FXCollections.observableHashMap();
    public final ObservableMap<Integer, List<ConferenceItem>>   conferencesPerContinent = FXCollections.observableHashMap();
    public final ObservableMap<Integer, List<ConferenceItem>>   filteredConferences     = FXCollections.observableHashMap();
    public       BooleanProperty                                update                  = new SimpleBooleanProperty(false);
    public       InvalidationListener                           attendenceListener      = _ -> Helper.saveConferenceItems(this.conferences);
    public       MapChangeListener<ProposalItem,ProposalStatus> proposalListener        = _ -> Helper.saveConferenceItems(conferences);
    public       ObservableList<ProposalItem>                   allProposals            = FXCollections.observableArrayList();

    public ConfiModel() {
        this.allProposals.setAll(Helper.loadProposals());
        loadConferenceItems(ConfiModel.this);
        registerListeners();
    }


    private void registerListeners() {
        conferences.addListener((ListChangeListener<ConferenceItem>) change -> {
            while (change.next()) {
                change.getAddedSubList().forEach(conference -> {
                    conference.attendenceProperty().addListener(attendenceListener);
                    conference.getProposals().addListener(proposalListener);
                });
                change.getRemoved().forEach(conference -> {
                    conference.attendenceProperty().removeListener(attendenceListener);
                    conference.getProposals().removeListener(proposalListener);
                });
            }
        });
    }

    public void loadConferenceItems(final ConfiModel model) {
        try {
            final String jsonText = Helper.readTextFile(Constants.HOME_FOLDER + Constants.APP_NAME + File.separator + Constants.CONFERENCE_ITEMS_FILENAME, Charset.forName("UTF-8"));
            final List<ConferenceItem> conferenceItems = Helper.parseConferenceItemsJson(jsonText, model);
            conferenceItems.forEach(conference -> {
                conference.attendenceProperty().addListener(attendenceListener);
                conference.getProposals().addListener(proposalListener);
            });
            conferences.addAll(conferenceItems);
        } catch (IOException e) { }

        if (this.networkMonitor.isOnline()) {
            final String               javaConferencesJsonText = Helper.getTextFromUrl(Constants.JAVA_CONFERENCES_JSON_URL);
            final List<JavaConference> conferences             = Helper.parseJavaConferencesJson(javaConferencesJsonText);
            this.update(conferences);
        }
        this.update.set(!this.update.get());
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
                conferencesToAdd.add(javaConference.convertToConferenceItem(ConfiModel.this));
            }
        });
        this.conferences.addAll(conferencesToAdd);
        Helper.saveConferenceItems(this.conferences);
        this.conferencesPerMonth.clear();
        this.conferences.forEach(conference -> {
            final ZonedDateTime date  = ZonedDateTime.ofInstant(conference.getDate(), ZoneId.systemDefault());
            final Integer       month = date.get(ChronoField.MONTH_OF_YEAR);
            if (!conferencesPerMonth.containsKey(month)) { conferencesPerMonth.put(month, new ArrayList<>()); }
            conferencesPerMonth.get(month).add(conference);
        });
        this.conferencesPerContinent.clear();
        this.filteredConferences.clear();
        this.filteredConferences.putAll(conferencesPerMonth);
        this.update.set(!this.update.get());
    }
}
