package eu.hansolo.fx.conficheck4j.tools;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.hansolo.fx.conficheck4j.data.ConferenceItem;
import eu.hansolo.fx.conficheck4j.data.ConfiModel;
import eu.hansolo.fx.conficheck4j.data.JavaConference;
import eu.hansolo.fx.conficheck4j.data.ProposalItem;
import eu.hansolo.fx.conficheck4j.data.SpeakerItem;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
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
import java.nio.ByteBuffer;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.concurrent.CompletionException;
import java.util.logging.Level;
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

    public static final List<ConferenceItem> parseConferenceItemsJson(final String jsonText, final ConfiModel model) {
        final List<ConferenceItem> conferences          = new ArrayList<>();
        if (null == jsonText || jsonText.isEmpty()) { return conferences; }
        final Gson      gson                = new Gson();
        final JsonArray conferenceItemArray = gson.fromJson(jsonText, JsonElement.class).getAsJsonArray();
        conferenceItemArray.forEach(jsonElement -> {
            final JsonObject     jsonObject = jsonElement.getAsJsonObject();
            final ConferenceItem conference = ConferenceItem.fromJsonObject(jsonObject, model);
            if (null != conference) { conferences.add(conference); }
        });
        return conferences;
    }

    public static final List<ProposalItem> parseProposalItemsJson(final String jsonText) {
        final List<ProposalItem> proposals = new ArrayList<>();
        if (null == jsonText || jsonText.isEmpty()) { return proposals; }
        final Gson      gson              = new Gson();
        final JsonArray proposalItemArray = gson.fromJson(jsonText, JsonElement.class).getAsJsonArray();
        proposalItemArray.forEach(jsonElement -> {
            final JsonObject   jsonObject = jsonElement.getAsJsonObject();
            final ProposalItem proposal   = ProposalItem.fromJsonObject(jsonObject);
            if (null != proposal) { proposals.add(proposal); }
        });
        return proposals;
    }

    public static final List<ProposalItem> loadProposals() {
        if (new File(Constants.PROPOSAL_ITEMS_PATH).exists()) {
            System.out.println("File found");
            try {
                final String jsonText = readTextFile(Constants.PROPOSAL_ITEMS_PATH, Charset.defaultCharset());
                return parseProposalItemsJson(jsonText);
            } catch (IOException e) {
                return new ArrayList<>();
            }
        } else {
            System.out.println("File not found");
            return new ArrayList<>();
        }
    }

    public static final SpeakerItem loadSpeakerItem() {
        if (new File(Constants.SPEAKER_ITEM_PATH).exists()) {
            try {
                final String jsonText = readTextFile(Constants.SPEAKER_ITEM_PATH, Charset.defaultCharset());
                return SpeakerItem.fromJsonString(jsonText);
            } catch (IOException e) {
                return new SpeakerItem("", "", "", "");
            }
        } else {
            return new SpeakerItem("", "", "", "");
        }
    }
    public static final void saveSpeakerItem(final SpeakerItem speakerItem) {
        final String jsonText = speakerItem.toJsonString();
        saveTextFile(jsonText, Constants.SPEAKER_ITEM_PATH);
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
        Collectors.joining(eu.hansolo.toolbox.Constants.COMMA, eu.hansolo.toolbox.Constants.SQUARE_BRACKET_OPEN, eu.hansolo.toolbox.Constants.SQUARE_BRACKET_CLOSE))).toString(), eu.hansolo.toolbox.Constants.HOME_FOLDER + Constants.APP_NAME + File.separator + eu.hansolo.fx.conficheck4j.tools.Constants.CONFERENCE_ITEMS_FILENAME);
    }

    public static final void saveProposalItems(final List<ProposalItem> proposals) {
        Helper.saveTextFile(new StringBuilder().append(proposals.stream().map(proposalItem -> proposalItem.toJsonString()).collect(
        Collectors.joining(eu.hansolo.toolbox.Constants.COMMA, eu.hansolo.toolbox.Constants.SQUARE_BRACKET_OPEN, eu.hansolo.toolbox.Constants.SQUARE_BRACKET_CLOSE))).toString(), Constants.PROPOSAL_ITEMS_PATH);
    }

    public static final Optional<Image> loadSpeakerImage() {
        return new File(Constants.SPEAKER_IMAGE_PATH).exists() ? Optional.of(new Image("file://" + Constants.SPEAKER_IMAGE_PATH)) : Optional.empty();
    }
    public static final Optional<Image> selectImage(final Stage stage) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Image File","*.jpg"));
        final File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            // where my problem is
            final Image img     = new Image(file.toURI().toString());
            final File  newFile = new File(Constants.HOME_FOLDER + Constants.APP_NAME + File.separator + Constants.SPEAKER_IMAGE_FILENAME);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(img, null), "jpg", newFile);
            } catch (IOException ex) {
                return Optional.empty();
            }
            return Optional.of(img);
        }
        return Optional.empty();
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

    public static int countWords(final String text) {
        StringTokenizer st = new StringTokenizer(text);
        return st.countTokens();
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
