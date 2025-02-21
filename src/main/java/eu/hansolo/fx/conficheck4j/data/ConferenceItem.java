package eu.hansolo.fx.conficheck4j.data;

import eu.hansolo.fx.conficheck4j.tools.Constants.ProposalStatus;
import eu.hansolo.toolbox.Constants;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


public class ConferenceItem {
    public static final String              FIELD_NAME            = "name";
    public static final String              FIELD_LOCATION        = "location";
    public static final String              FIELD_CITY            = "city";
    public static final String              FIELD_COUNTRY         = "country";
    public static final String              FIELD_URL             = "url";
    public static final String              FIELD_DATE            = "date";
    public static final String              FIELD_DAYS            = "days";
    public static final String              FIELD_TYPE            = "type";
    public static final String              FIELD_CFP_URL         = "cfp_url";
    public static final String              FIELD_CFP_DATE        = "cfp_date";
    public static final String              FIELD_LAT             = "lat";
    public static final String              FIELD_LON             = "lon";
    public static final String              FIELD_PROPOSALS       = "proposals";
    public static final String              FIELD_PROPOSAL_STATES = "proposal_states";
    private             String              name                  = "";
    private             String              location              = "";
    private             String              city                  = "";
    private             String              country               = "";
    private             String              url                   = "";
    private             Instant             date                  = Instant.now();
    private             double              days                  = 0.0;
    private             String              type                  = "";
    private             Optional<String>    cfpUrl                = Optional.empty();
    private             Optional<String>    cfpDate               = Optional.empty();
    private             Optional<Double>    lat                   = Optional.empty();
    private             Optional<Double>    lon                   = Optional.empty();
    private             List<ProposalItem>  proposals             = new ArrayList<>();
    private             Map<String, String> proposalStates        = new HashMap<>();


    public ConferenceItem(final String name, final String location, final String city, final String country, final String url, final Instant date, final double days, final String type, final Optional<String> cfpUrl, final Optional<String> cfpDate, final Optional<Double> lat, final Optional<Double> lon, final List<ProposalItem> proposals, final Map<String, String> proposalStates) {
        this.name     = name;
        this.location = location;
        this.city     = city;
        this.country  = country;
        this.url      = url;
        this.date     = date;
        this.days     = days;
        this.type     = type;
        this.cfpUrl   = cfpUrl;
        this.cfpDate  = cfpDate;
        this.lat      = lat;
        this.lon      = lon;
        this.proposals.addAll(proposals);
        this.proposalStates.putAll(proposalStates);
    }


    public String getName() { return name; }
    public void setName(final String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(final String location) { this.location = location; }

    public String getCity() { return city; }
    public void setCity(final String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(final String country) { this.country = country; }

    public String getUrl() { return url; }
    public void setUrl(final String url) { this.url = url; }

    public Instant getDate() { return date; }
    public void setDate(final Instant date) { this.date = date; }

    public double getDays() { return days; }
    public void setDays(final double days) { this.days = days; }

    public String getType() { return type; }
    public void setType(final String type) { this.type = type; }

    public Optional<String> getCfpUrl() { return cfpUrl; }
    public void setCfpUrl(final Optional<String> cfpUrl) { this.cfpUrl = cfpUrl; }

    public Optional<String> getCfpDate() { return cfpDate; }
    public void setCfpDate(final Optional<String> cfpDate) { this.cfpDate = cfpDate; }

    public Optional<Double> getLat() { return lat; }
    public void setLat(final Optional<Double> lat) { this.lat = lat; }

    public Optional<Double> getLon() { return lon; }
    public void setLon(final Optional<Double> lon) { this.lon = lon; }

    public final void addProposal(final ProposalItem proposal) {
        if (this.proposals.contains(proposal)) { return; }
        this.proposals.add(proposal);
        this.proposalStates.put(proposal.getId(), ProposalStatus.NOT_SUBMITTED.apiString);
    }

    public final void removeProposal(final ProposalItem proposal) {
        Optional<ProposalItem> optionalProposal = this.proposals.stream().filter(p -> p.getId().equals(proposal.getId())).findFirst();
        if (optionalProposal.isPresent()) {
            this.proposals.remove(optionalProposal.get());
            this.proposalStates.remove(optionalProposal.get().getId());
        }
    }

    public final String toJsonString() {
        return new StringBuilder().append(Constants.CURLY_BRACKET_OPEN)
                                  .append(Constants.QUOTES).append(FIELD_NAME).append(Constants.QUOTES_COLON_QUOTES).append(this.name).append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_LOCATION).append(Constants.QUOTES_COLON_QUOTES).append(this.location).append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_CITY).append(Constants.QUOTES_COLON_QUOTES).append(this.city).append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_COUNTRY).append(Constants.QUOTES_COLON_QUOTES).append(this.country).append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_URL).append(Constants.QUOTES_COLON_QUOTES).append(this.url).append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_DATE).append(Constants.QUOTES_COLON_QUOTES).append(this.date).append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_DAYS).append(Constants.QUOTES_COLON).append(this.days).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_TYPE).append(Constants.QUOTES_COLON_QUOTES).append(this.type).append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_CFP_URL).append(Constants.QUOTES_COLON_QUOTES).append(this.cfpUrl.isPresent() ? this.cfpUrl.get() : "").append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_CFP_DATE).append(Constants.QUOTES_COLON_QUOTES).append(this.cfpDate.isPresent() ? this.cfpDate.get() : "").append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_LAT).append(Constants.QUOTES_COLON).append(this.lat.isPresent() ? this.lat.get() : 0.0).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_LON).append(Constants.QUOTES_COLON).append(this.lon.isPresent() ? this.lon.get() : 0.0).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_PROPOSALS).append(Constants.QUOTES_COLON).append(this.proposals.stream().map(p -> p.toString()).collect(Collectors.joining(Constants.COMMA, Constants.SQUARE_BRACKET_OPEN, Constants.SQUARE_BRACKET_CLOSE))).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_PROPOSAL_STATES).append(Constants.QUOTES_COLON)
                                  .append(this.proposalStates.entrySet().stream().map(entry -> new StringBuilder(Constants.COMMA).append(entry.getKey()).append(Constants.QUOTES_COLON_QUOTES).append(entry.getValue()).append(Constants.QUOTES).toString()).collect(Collectors.joining(Constants.COMMA, Constants.SQUARE_BRACKET_OPEN, Constants.SQUARE_BRACKET_CLOSE)))
                                  .append(Constants.CURLY_BRACKET_CLOSE)
                                  .toString();
    }

    public static final ConferenceItem fromJsonString(String jsonText) {
        return null;
    }

    @Override public String toString() {
        return new StringBuilder(this.name).append(" -> ").append(this.proposals.size()).toString();
    }

    @Override public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) { return false; }
        ConferenceItem that = (ConferenceItem) o;
        return Objects.equals(name, that.name) && Objects.equals(url, that.url);
    }
    @Override public int hashCode() {
        return Objects.hash(name, url);
    }
}
