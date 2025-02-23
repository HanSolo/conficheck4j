package eu.hansolo.fx.conficheck4j.tools;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.hansolo.fx.conficheck4j.data.ConferenceItem;
import eu.hansolo.fx.conficheck4j.data.JavaConference;
import eu.hansolo.fx.conficheck4j.data.ProposalItem;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import static eu.hansolo.jdktools.util.Helper.getOperatingSystem;
import static java.nio.charset.StandardCharsets.UTF_8;


public class Helper {
    private static final String     REGQUERY_UTIL  = "reg query ";
    private static final String     REGDWORD_TOKEN = "REG_DWORD";
    private static final String     DARK_THEME_CMD = REGQUERY_UTIL + "\"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize\"" + " /v AppsUseLightTheme";
    private static       HttpClient httpClient;

    public static final double clamp(final double min, final double max, final double value) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    public static final Color getPrimaryColor() {
        return isDarkMode() ? Constants.WHITE : Constants.BLACK;
    }

    public static final Color getSecondaryColor() {
        return isDarkMode() ? Constants.GRAY : Constants.DARK_GRAY;
    }

    public static final List<JavaConference> parseJavaConferencesJson(final String jsonText) {
        final List<JavaConference> conferences          = new ArrayList<>();
        if (null == jsonText || jsonText.isEmpty()) { return conferences; }
        final Gson                 gson                 = new Gson();
        final JsonArray            javaConferencesArray = gson.fromJson(jsonText, JsonElement.class).getAsJsonArray();
        javaConferencesArray.forEach(jsonElement -> {
            final JsonObject     jsonObject = jsonElement.getAsJsonObject();
            final JavaConference conference = gson.fromJson(jsonObject, JavaConference.class);
            conferences.add(conference);
        });
        return conferences;
    }

    public static final List<ConferenceItem> parseConferenceItemsJson(final String jsonText) {
        final List<ConferenceItem> conferences          = new ArrayList<>();
        if (null == jsonText || jsonText.isEmpty()) { return conferences; }
        final Gson                 gson                 = new Gson();
        final JsonArray            conferenceItemArray = gson.fromJson(jsonText, JsonElement.class).getAsJsonArray();
        conferenceItemArray.forEach(jsonElement -> {
            final JsonObject jsonObject    = jsonElement.getAsJsonObject();
            final String     name          = jsonObject.has(ConferenceItem.FIELD_NAME)      ? jsonObject.get(ConferenceItem.FIELD_NAME).getAsString()                      : "";
            final String     location      = jsonObject.has(ConferenceItem.FIELD_LOCATION)  ? jsonObject.get(ConferenceItem.FIELD_LOCATION).getAsString()                  : "";
            final String     city          = jsonObject.has(ConferenceItem.FIELD_CITY)      ? jsonObject.get(ConferenceItem.FIELD_CITY).getAsString()                      : "";
            final String     country       = jsonObject.has(ConferenceItem.FIELD_COUNTRY)   ? jsonObject.get(ConferenceItem.FIELD_COUNTRY).getAsString()                   : "";
            final String     url           = jsonObject.has(ConferenceItem.FIELD_URL)       ? jsonObject.get(ConferenceItem.FIELD_URL).getAsString()                       : "";
            final Instant    date          = jsonObject.has(ConferenceItem.FIELD_DATE)      ? Instant.ofEpochSecond(jsonObject.get(ConferenceItem.FIELD_DATE).getAsLong()) : Instant.MIN;
            final double     days          = jsonObject.has(ConferenceItem.FIELD_DAYS)      ? jsonObject.get(ConferenceItem.FIELD_DAYS).getAsDouble()                      : -1;
            final String     type          = jsonObject.has(ConferenceItem.FIELD_TYPE)      ? jsonObject.get(ConferenceItem.FIELD_TYPE).getAsString()                      : "";
            final Optional<String> cfpUrl  = jsonObject.has(ConferenceItem.FIELD_CFP_URL)   ? Optional.of(jsonObject.get(ConferenceItem.FIELD_CFP_URL).getAsString())                   : Optional.empty();
            final Optional<String> cfpDate = jsonObject.has(ConferenceItem.FIELD_CFP_DATE)  ? Optional.of(jsonObject.get(ConferenceItem.FIELD_CFP_DATE).getAsString()) : Optional.empty();
            final Optional<Double> lat           = jsonObject.has(ConferenceItem.FIELD_LAT)       ? Optional.of(jsonObject.get(ConferenceItem.FIELD_LAT).getAsDouble())                      : Optional.empty();
            final Optional<Double> lon           = jsonObject.has(ConferenceItem.FIELD_LON)       ? Optional.of(jsonObject.get(ConferenceItem.FIELD_LON).getAsDouble())                       : Optional.empty();
            final JsonArray  proposalArray = jsonObject.has(ConferenceItem.FIELD_PROPOSALS) ? jsonObject.getAsJsonArray(ConferenceItem.FIELD_PROPOSALS)                    : null;
            final List<ProposalItem> proposals = new ArrayList<>();
            if (null != proposalArray) {
                for (final JsonElement proposalElement : proposalArray) {
                    final JsonObject proposalObject = proposalElement.getAsJsonObject();
                    final String     title          = proposalObject.has(ProposalItem.FIELD_TITLE) ? proposalObject.get(ProposalItem.FIELD_TITLE).getAsString() : "";
                    final String     abstrakt       = proposalObject.has(ProposalItem.FIELD_ABSTRACT) ? proposalObject.get(ProposalItem.FIELD_ABSTRACT).getAsString() : "";
                    final String     pitch          = proposalObject.has(ProposalItem.FIELD_PITCH) ? proposalObject.get(ProposalItem.FIELD_PITCH).getAsString() : "";
                    if (!title.isBlank() && !abstrakt.isBlank()) {
                        proposals.add(new ProposalItem(title, abstrakt, pitch));
                    }
                }
            }
            final JsonArray proposalStatesArray = jsonObject.has(ConferenceItem.FIELD_PROPOSAL_STATES) ? jsonObject.getAsJsonArray(ConferenceItem.FIELD_PROPOSALS) : null;
            final Map<String, String> proposalStates = new HashMap<>();
            if (null != proposalStatesArray) {
                for (final JsonElement proposalStateElement : proposalStatesArray) {
                    final JsonObject proposalStateObject = proposalStateElement.getAsJsonObject();
                    final String title = proposalStateObject.has(ProposalItem.FIELD_TITLE) ? proposalStateObject.get(ProposalItem.FIELD_TITLE).getAsString() : "";
                    final String state = proposalStateObject.has(ProposalItem.FIELD_STATE) ? proposalStateObject.get(ProposalItem.FIELD_STATE).getAsString() : "";
                    if (!title.isBlank() && !state.isBlank()) {
                        proposalStates.put(title, state);
                    }
                }
            }
            if (!name.isBlank() && !url.isBlank()) {
                conferences.add(new ConferenceItem(name, location, city, country, url, date, days, type, cfpUrl, cfpDate, lat, lon, proposals, proposalStates));
            }
        });
        return conferences;
    }

    public static final String readTextFile(final String filename, final Charset charset) throws IOException {
        if (null == filename || filename.isEmpty()) { throw new IllegalArgumentException("Filename cannot be null or empty"); }
        return Files.readString(Paths.get(filename), null == charset ? Charset.forName("UTF-8") : charset);
    }

    public static final boolean saveTextFile(final String text, final String filename) {
        if (null == filename || filename.isEmpty()) { throw new IllegalArgumentException("Filename cannot be null or empty"); }
        if (null == text || text.isEmpty()) { throw new IllegalArgumentException("Text cannot be null or empty"); }
        try {
            Files.write(Paths.get(filename), text.getBytes());
            return true;
        } catch (IOException e) {
            System.out.println("Error saving text file " + filename + ". " + e);
            return false;
        }
    }

    public static final void saveConferenceItems(final List<ConferenceItem> conferences) {
        Helper.saveTextFile(new StringBuilder().append(conferences.stream().map(conferenceItem -> conferenceItem.toJsonString()).collect(
        Collectors.joining(eu.hansolo.toolbox.Constants.COMMA, eu.hansolo.toolbox.Constants.SQUARE_BRACKET_OPEN, eu.hansolo.toolbox.Constants.SQUARE_BRACKET_CLOSE))).toString(), eu.hansolo.toolbox.Constants.HOME_FOLDER + eu.hansolo.fx.conficheck4j.tools.Constants.CONFERENCE_ITEMS_FILENAME);
    }

    public static final Optional<Instant>[] getDatesFromJavaConferenceDate(final String date) {
        if (null == date || date.isEmpty()) { return new Optional[] { Optional.empty(), Optional.empty() }; }
        Constants.JAVA_CONFERENCE_DATE_MATCHER.reset(date);
        if (Constants.JAVA_CONFERENCE_DATE_MATCHER.matches()) {
            if (Constants.JAVA_CONFERENCE_DATE_MATCHER.group(1) != null) {
                final int           day1   = Integer.parseInt(Constants.JAVA_CONFERENCE_DATE_MATCHER.group(2));
                final int           month1 = Month.valueOf(Constants.JAVA_CONFERENCE_DATE_MATCHER.group(3).toUpperCase()).getValue();
                final int           year1  = Integer.parseInt(Constants.JAVA_CONFERENCE_DATE_MATCHER.group(4));
                final ZonedDateTime date1  = ZonedDateTime.of(LocalDate.of(year1, month1, day1), LocalTime.of(0, 0), ZoneOffset.UTC);
                return new Optional[] { Optional.of(Instant.from(date1)), Optional.empty() };
            } else if (Constants.JAVA_CONFERENCE_DATE_MATCHER.group(5) != null) {
                final int           day1   = Integer.parseInt(Constants.JAVA_CONFERENCE_DATE_MATCHER.group(6));
                final int           day2   = Integer.parseInt(Constants.JAVA_CONFERENCE_DATE_MATCHER.group(7));
                final int           month1 = Month.valueOf(Constants.JAVA_CONFERENCE_DATE_MATCHER.group(8).toUpperCase()).getValue();
                final int           year1  = Integer.parseInt(Constants.JAVA_CONFERENCE_DATE_MATCHER.group(9));
                final ZonedDateTime date1  = ZonedDateTime.of(LocalDate.of(year1, month1, day1), LocalTime.of(0, 0), ZoneOffset.UTC);
                final ZonedDateTime date2  = ZonedDateTime.of(LocalDate.of(year1, month1, day2), LocalTime.of(0, 0), ZoneOffset.UTC);
                return new Optional[] { Optional.of(Instant.from(date1)), Optional.of(Instant.from(date2)) };
            } else if (Constants.JAVA_CONFERENCE_DATE_MATCHER.group(10) != null) {
                final int           day1   = Integer.parseInt(Constants.JAVA_CONFERENCE_DATE_MATCHER.group(11));
                final int           month1 = Month.valueOf(Constants.JAVA_CONFERENCE_DATE_MATCHER.group(12).toUpperCase()).getValue();
                final int           day2   = Integer.parseInt(Constants.JAVA_CONFERENCE_DATE_MATCHER.group(13));
                final int           month2 = Month.valueOf(Constants.JAVA_CONFERENCE_DATE_MATCHER.group(14).toUpperCase()).getValue();
                final int           year   = Integer.parseInt(Constants.JAVA_CONFERENCE_DATE_MATCHER.group(15));
                final ZonedDateTime date1  = ZonedDateTime.of(LocalDate.of(year, month1, day1), LocalTime.of(0, 0), ZoneOffset.UTC);
                final ZonedDateTime date2  = ZonedDateTime.of(LocalDate.of(year, month2, day2), LocalTime.of(0, 0), ZoneOffset.UTC);
                return new Optional[] { Optional.of(Instant.from(date1)), Optional.of(Instant.from(date2)) };
            } else {
                return new Optional[] { Optional.empty(), Optional.empty() };
            }
        }
        return new Optional[] { Optional.empty(), Optional.empty() };
    }

    public static final String getLocationFromEventItem(final String text) {
        if (text == null || text.isEmpty()) { return ""; }
        Constants.EVENT_ITEM_LOCATION_MATCHER.reset(text);
        if (Constants.EVENT_ITEM_LOCATION_MATCHER.matches()) {
            if (Constants.EVENT_ITEM_LOCATION_MATCHER.group(2) != null) {
                return Constants.EVENT_ITEM_LOCATION_MATCHER.group(2);
            }
        }
        return "";
    }

    public static final String getCityFromEventItem(final String text) {
        if (text == null || text.isEmpty()) { return ""; }
        final String[] parts = text.split(Constants.CITY_DELIMITER);
        return parts[0].trim();
    }

    public static final double getDaysBetweenDates(final Instant dateFrom, final Instant dateTo) {
        if (null == dateFrom || null == dateTo) { throw new IllegalArgumentException("Dates cannot be null"); }
        return ChronoUnit.DAYS.between(dateFrom, dateTo) + 1;
    }

    public static final Color getColorForCfpDate(final LocalDate date) {
        final LocalDate now           = LocalDate.now();
        final long      daysBetween   = ChronoUnit.DAYS.between(now, date);
        final long      monthsBetween = ChronoUnit.MONTHS.between(now, date);
        final long      yearsBetween  = ChronoUnit.YEARS.between(now, date);

        if (monthsBetween < 0 && daysBetween <= 0) {
            return Constants.GRAY;
        } else if (monthsBetween == 0 && daysBetween < 0) {
            return Constants.GRAY;
        } else if (monthsBetween == 0 && daysBetween > 21) {
            return Constants.YELLOW;
        } else if (monthsBetween == 0 && daysBetween > 14) {
            return Constants.ORANGE;
        } else if (monthsBetween == 0 && daysBetween <= 7) {
            return Constants.RED;
        } else {
            return Constants.GREEN;
        }
    }

    public static final boolean isCfpOpen(final LocalDate date) {
        final LocalDate     now           = LocalDate.now();
        final ZonedDateTime startOfDayUTC = now.atStartOfDay(ZoneOffset.UTC);
        final ZonedDateTime endOfCfpUTC   = date.atStartOfDay(ZoneOffset.UTC);
        return startOfDayUTC.toEpochSecond() <= endOfCfpUTC.toEpochSecond();
    }

    public static final boolean isDarkMode() {
        switch(getOperatingSystem()) {
            case WINDOWS -> { return isWindowsDarkMode(); }
            case MACOS   -> { return isMacOsDarkMode(); }
            default      -> { return false; }
        }
    }

    private static final boolean isMacOsDarkMode() {
        try {
            final Runtime           runtime = Runtime.getRuntime();
            final Process           process    = runtime.exec("defaults read -g AppleInterfaceStyle");
            final InputStreamReader isr        = new InputStreamReader(process.getInputStream());
            final BufferedReader    rdr        = new BufferedReader(isr);
            boolean                 isDarkMode = false;
            String                  line;
            while((line = rdr.readLine()) != null) {
                if (line.equals("Dark")) { isDarkMode = true; }
            }
            int rc = process.waitFor();  // Wait for the process to complete
            return 0 == rc && isDarkMode;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    private static final boolean isWindowsDarkMode() {
        try {
            final Process      process = Runtime.getRuntime().exec(DARK_THEME_CMD);
            final StreamReader reader  = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            final String result = reader.getResult();
            final int    p      = result.indexOf(REGDWORD_TOKEN);

            if (p == -1) { return false; }

            // 1 == Light Mode, 0 == Dark Mode
            final String temp = result.substring(p + REGDWORD_TOKEN.length()).trim();
            return ((Integer.parseInt(temp.substring("0x".length()), 16))) == 0;
        }
        catch (Exception e) {
            return false;
        }
    }


    // ******************** REST calls ****************************************
    public static HttpClient createHttpClient() {
        return HttpClient.newBuilder()
                         .connectTimeout(Duration.ofSeconds(20))
                         .followRedirects(Redirect.NORMAL)
                         .version(java.net.http.HttpClient.Version.HTTP_2)
                         .build();
    }

    public static final HttpResponse<String> get(final String uri) {
        if (null == httpClient) { httpClient = createHttpClient(); }
        HttpRequest request = HttpRequest.newBuilder()
                                         .GET()
                                         .uri(URI.create(uri))
                                         .setHeader("Accept", "application/json")
                                         .timeout(Duration.ofSeconds(60))
                                         .build();
        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response;
            } else {
                // Problem with url request
                System.out.println("Error connecting to " + uri + " with response code: " + response.statusCode());
                return response;
            }
        } catch (CompletionException | InterruptedException | IOException e) {
            System.out.println("Error connecting to " + uri + " with exception: " + e);
            return null;
        }
    }
    public static final HttpResponse<String> get(final String uri, final String apiKey) { return get(uri, apiKey, ""); }
    public static final HttpResponse<String> get(final String uri, final String apiKey, final String userAgent) {
        if (null == httpClient) { httpClient = createHttpClient(); }
        final String userAgentText = (null == userAgent || userAgent.isEmpty()) ? "ConfiCheck" : "ConfiCheck (" + userAgent + ")";
        HttpRequest request = HttpRequest.newBuilder()
                                         .GET()
                                         .uri(URI.create(uri))
                                         .setHeader("Accept", "application/json")
                                         .setHeader("User-Agent", userAgentText)
                                         .setHeader("x-api-key", apiKey) // needed for Intelligence Cloud authentification
                                         .timeout(Duration.ofSeconds(60))
                                         .build();
        //System.out.println(request.toString());
        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response;
            } else if (response.statusCode() == 503) {
                System.out.println("Rate limited");
                return response;
            } else if (response.statusCode() == 403) {
                System.out.println("Forbidden");
                return response;
            } else if (response.statusCode() == 404) {
                System.out.println("Not Found");
                return response;
            } else {
                // Problem with url request
                return response;
            }
        } catch (CompletionException | InterruptedException | IOException e) {
            return null;
        }
    }

    public static final HttpResponse<String> httpHeadRequestSync(final String uri) {
        if (null == httpClient) { httpClient = createHttpClient(); }

        final HttpRequest request = HttpRequest.newBuilder()
                                               .HEAD()
                                               .method("HEAD", HttpRequest.BodyPublishers.noBody())
                                               .uri(URI.create(uri))
                                               .build();

        try {
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            return response;
        } catch (CompletionException | InterruptedException | IOException e) {
            return null;
        }
    }

    public static final String getTextFromUrl(final String uri) {
        // Get all text from given uri
        try (var stream = URI.create(uri).toURL().openStream()) {
            return new String(stream.readAllBytes(), UTF_8);
        } catch(Exception e) {
            System.out.println("Error reading text from uri: " + uri);
            return "";
        }
    }


    // ******************** Internal Classes **********************************
    static class StreamReader extends Thread {
        private final InputStream  is;
        private final StringWriter sw;

        StreamReader(final InputStream is) {
            this.is = is;
            sw = new StringWriter();
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1) { sw.write(c); }
            } catch (IOException e) { }
        }

        String getResult() { return sw.toString(); }
    }
}
