package eu.hansolo.fx.conficheck4j.data;

import eu.hansolo.fx.conficheck4j.Main;
import eu.hansolo.fx.conficheck4j.tools.Constants;
import eu.hansolo.fx.conficheck4j.tools.Constants.AttendingStatus;
import eu.hansolo.fx.conficheck4j.tools.Constants.Continent;
import eu.hansolo.fx.conficheck4j.tools.Constants.Filter;
import eu.hansolo.fx.conficheck4j.tools.Constants.ProposalStatus;
import eu.hansolo.fx.conficheck4j.tools.Helper;
import eu.hansolo.fx.conficheck4j.tools.IsoCountries;
import eu.hansolo.fx.conficheck4j.tools.NetworkMonitor;
import eu.hansolo.fx.conficheck4j.views.ConferenceView;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class ConfiModel {
    public final NetworkMonitor                                 networkMonitor          = NetworkMonitor.INSTANCE;
    public final ObservableList<JavaConference>                 javaConferences         = FXCollections.observableArrayList();
    public final ObservableList<ConferenceItem>                 conferences             = FXCollections.observableArrayList();
    public final ObservableMap<Integer, List<ConferenceItem>>   conferencesPerMonth     = FXCollections.observableHashMap();
    public final ObservableMap<Integer, List<ConferenceItem>>   conferencesPerContinent = FXCollections.observableHashMap();
    public final ObservableMap<Integer, List<ConferenceItem>>   filteredConferences     = FXCollections.observableHashMap();
    public       BooleanProperty                                update                  = new SimpleBooleanProperty(false);
    public       InvalidationListener                           attendenceListener      = _ -> Helper.saveConferenceItems(this.conferences);
    public       MapChangeListener<ProposalItem,ProposalStatus> proposalListener        = _ -> Helper.saveConferenceItems(conferences);
    public       ObservableList<ProposalItem>                   allProposals            = FXCollections.observableArrayList();
    public final ObjectProperty<Filter>                         selectedFilter;
    public final ObjectProperty<Continent>                      selectedContinent;

    public ConfiModel() {
        this.selectedFilter     = new ObjectPropertyBase<>(Filter.ALL) {
            @Override protected void invalidated() { update(); }
            @Override public Object getBean() { return ConfiModel.this; }
            @Override public String getName() { return "selectedFilter"; }
        };
        this.selectedContinent  = new ObjectPropertyBase<>(Continent.ALL) {
            @Override protected void invalidated() { update(); }
            @Override public Object getBean() { return ConfiModel.this; }
            @Override public String getName() { return "selectedContinent"; }
        };

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
            conferences.setAll(conferenceItems);
        } catch (IOException e) { }

        if (this.networkMonitor.isOnline()) {
            final String               javaConferencesJsonText = Helper.getTextFromUrl(Constants.JAVA_CONFERENCES_JSON_URL);
            this.javaConferences.setAll(Helper.parseJavaConferencesJson(javaConferencesJsonText));

            final ZonedDateTime now  = ZonedDateTime.now(ZoneId.systemDefault());
            final int           year = now.get(ChronoField.YEAR);
            List<ConferenceItem> conferencesToAdd    = new ArrayList<>();
            List<ConferenceItem> conferencesToRemove = new ArrayList<>();
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
                    if (year == date.get(ChronoField.YEAR)) {

                    } if (year > date.get(ChronoField.YEAR) && date.get(ChronoField.MONTH_OF_YEAR) > 9) {

                    } else if (year < date.get(ChronoField.YEAR) && date.get(ChronoField.MONTH_OF_YEAR) < 7) {

                    } else {
                        conferencesToRemove.add(optConference.get());
                    }

                } else {
                    ConferenceItem conference = javaConference.convertToConferenceItem(ConfiModel.this);
                    if (year == conference.getDate().get(ChronoField.YEAR)) {
                        conferencesToAdd.add(conference);
                    } if (year > conference.getDate().get(ChronoField.YEAR) && conference.getDate().get(ChronoField.MONTH_OF_YEAR) > 9) {
                        conferencesToAdd.add(conference);
                    } else if (year < conference.getDate().get(ChronoField.YEAR) && conference.getDate().get(ChronoField.MONTH_OF_YEAR) < 7) {
                        conferencesToAdd.add(conference);
                    }
                }
            });
            this.conferences.addAll(conferencesToAdd);
            this.conferences.removeAll(conferencesToRemove);
            Helper.saveConferenceItems(this.conferences);

            this.update();
        }
        this.update.set(!this.update.get());
    }

    public final void update() {
        List<String>         countriesInContinent   = Continent.ALL == this.selectedContinent.get() ? IsoCountries.ALL_COUNTRIES.stream().map(isoCountryInfo -> isoCountryInfo.name()).toList() : IsoCountries.ALL_COUNTRIES.stream().filter(country -> country.continent().equals(this.selectedContinent.get().code)).map(isoCountryInfo -> isoCountryInfo.name()).toList();
        List<ConferenceItem> conferencesInContinent = this.conferences.stream().filter(conference -> countriesInContinent.contains(conference.getCountry())).toList();
        this.conferencesPerMonth.clear();
        this.conferencesPerContinent.clear();
        conferencesInContinent.forEach(conference -> {
            final ZonedDateTime date  = ZonedDateTime.ofInstant(conference.getDate(), ZoneId.systemDefault());
            final Integer       month = date.get(ChronoField.MONTH_OF_YEAR);
            if (!this.conferencesPerMonth.containsKey(month)) {
                this.conferencesPerMonth.put(month, new ArrayList<>());
            }
            if (!this.conferencesPerContinent.containsKey(month)) {
                this.conferencesPerContinent.put(month, new ArrayList<>());
            }
            this.conferencesPerMonth.get(month).add(conference);
            this.conferencesPerContinent.get(month).add(conference);
        });

        switch (this.selectedFilter.get()) {
            case ALL       -> {
                this.filteredConferences.clear();
                this.filteredConferences.putAll(this.conferencesPerContinent);
            }
            case SPEAKING  -> {
                this.filteredConferences.clear();
                conferencesInContinent.stream().filter(conference -> conference.getAttendence() == AttendingStatus.SPEAKING)
                                      .forEach(conference -> {
                                          final ZonedDateTime date  = ZonedDateTime.ofInstant(conference.getDate(), ZoneId.systemDefault());
                                          final Integer       month = date.get(ChronoField.MONTH_OF_YEAR);
                                          if (!this.filteredConferences.containsKey(month)) { this.filteredConferences.put(month, new ArrayList<>()); }
                                          this.filteredConferences.get(month).add(conference);
                                      });
            }
            case ATTENDING -> {
                this.filteredConferences.clear();
                conferencesInContinent.stream().filter(conference -> conference.getAttendence() == AttendingStatus.ATTENDING)
                                      .forEach(conference -> {
                                          final ZonedDateTime date  = ZonedDateTime.ofInstant(conference.getDate(), ZoneId.systemDefault());
                                          final Integer       month = date.get(ChronoField.MONTH_OF_YEAR);
                                          if (!this.filteredConferences.containsKey(month)) { this.filteredConferences.put(month, new ArrayList<>()); }
                                          this.filteredConferences.get(month).add(conference);
                                      });
            }
            case CFP_OPEN  -> {
                this.filteredConferences.clear();
                for (Integer month : this.conferencesPerContinent.keySet()) {
                    if (this.conferencesPerContinent.get(month).isEmpty()) { continue; }
                    this.filteredConferences.put(month, new ArrayList<>(this.conferencesPerContinent.get(month)
                                                                                                    .stream()
                                                                                                    .filter(conference -> conference.getCfpDate().isPresent())
                                                                                                    .filter(conference -> Helper.getDatesFromJavaConferenceDate(conference.getCfpDate().get()).length > 0 && Helper.getDatesFromJavaConferenceDate(conference.getCfpDate().get())[0].isPresent())
                                                                                                    .filter(conference -> Helper.isCfpOpen(ZonedDateTime.ofInstant(Helper.getDatesFromJavaConferenceDate(conference.getCfpDate().get())[0].get(), ZoneId.systemDefault()).toLocalDate()))
                                                                                                    .collect(Collectors.toSet())));
                }
            }
        }
        this.update.set(!this.update.get());
    }
}
