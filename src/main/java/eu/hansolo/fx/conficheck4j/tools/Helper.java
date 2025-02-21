package eu.hansolo.fx.conficheck4j.tools;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.hansolo.fx.conficheck4j.data.JavaConference;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static eu.hansolo.jdktools.util.Helper.getOperatingSystem;


public class Helper {
    private static final String   REGQUERY_UTIL      = "reg query ";
    private static final String   REGDWORD_TOKEN     = "REG_DWORD";
    private static final String   DARK_THEME_CMD     = REGQUERY_UTIL + "\"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize\"" + " /v AppsUseLightTheme";

    public static final Color getPrimaryColor() {
        return isDarkMode() ? Constants.WHITE : Constants.BLACK;
    }

    public static final Color getSecondaryColor() {
        return isDarkMode() ? Constants.DARK_GRAY : Constants.GRAY;
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
        return ChronoUnit.DAYS.between(dateFrom, dateTo);
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
