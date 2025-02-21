package eu.hansolo.fx.conficheck4j.data;


import eu.hansolo.fx.conficheck4j.tools.Constants.ConferenceType;
import eu.hansolo.fx.conficheck4j.tools.Helper;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public record JavaConference(String cfpEndDate, Coordinates coordinates, String locationName, Boolean hybrid, String cfpLink, String date, String name, String link) {

    public ConferenceItem convertToConferenceItem() {
        final String name         = this.name;
        final String location     = this.locationName;
        final String cityName     = this.locationName == null ? "" : Helper.getCityFromEventItem(this.locationName);
        final String countryName  = this.coordinates  == null ? "" : coordinates.countryName();
        final ConferenceType type;
        if (countryName.toLowerCase().equals("online")) {
            type = ConferenceType.VIRTUAL;
        } else if (this.hybrid) {
            type = ConferenceType.HYBRID;
        } else {
            type = ConferenceType.IN_PERSON;
        }
        final Optional<Instant>[] dates      = Helper.getDatesFromJavaConferenceDate(this.date);
        final Instant             date       = dates[0].isPresent() ? dates[0].get() : Instant.MIN;
        final Instant             endDate    = dates[1].isPresent() ? dates[1].get() : date;
        final double              days       = Helper.getDaysBetweenDates(date, endDate);
        final String              city       = ConferenceType.VIRTUAL == type ? "ONLINE"         : cityName;
        final String              country    = ConferenceType.VIRTUAL == type ? ""               : countryName;
        final String              url        = this.link              == null ? ""               : this.link;
        final Optional<String>    cfpUrl     = this.cfpLink           == null ? Optional.empty() : Optional.of(cfpLink);
        final Optional<String>    cfpEndDate = this.cfpEndDate        == null ? Optional.empty() : Optional.of(this.cfpEndDate);
        final Optional<Double>    lat        = this.coordinates       == null ? Optional.empty() : Optional.of(this.coordinates.lat());
        final Optional<Double>    lon        = this.coordinates       == null ? Optional.empty() : Optional.of(this.coordinates.lon());

        return new ConferenceItem(name, location, city, country, url, date, days, type.apiString, cfpUrl, cfpEndDate, lat, lon, List.of(), Map.of());
    }
}
