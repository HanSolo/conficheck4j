package eu.hansolo.fx.conficheck4j.tools;

import javafx.scene.paint.Color;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public final class Constants {
    public static final String  APP_NAME                     = "ConfiCheck";
    public static final int     APP_REFRESH_INTERVAL         = 3600;
    public static final String  JAVA_CONFERENCES_JSON_URL    = "https://javaconferences.org/conferences.json";

    public static final boolean IS_DARK_MODE                 = Helper.isDarkMode();

    public static final String  HOME_FOLDER                  = new StringBuilder(System.getProperty("user.home")).append(File.separator).toString();
    public static final String  CONFERENCE_ITEMS_FILENAME    = "conference_items.json";
    public static final String  PROPOSAL_ITEMS_FILENAME      = "proposal_items.json";
    public static final String  PROPOSAL_ITEMS_PATH          = HOME_FOLDER + APP_NAME + File.separator + PROPOSAL_ITEMS_FILENAME;
    public static final String  SPEAKER_ITEM_FILENAME        = "speaker_item.json";
    public static final String  SPEAKER_IMAGE_FILENAME       = "speaker_image.jpg";
    public static final String  SPEAKER_IMAGE_PATH           = HOME_FOLDER + APP_NAME + File.separator + SPEAKER_IMAGE_FILENAME;
    public static final String  SPEAKER_ITEM_PATH            = HOME_FOLDER + APP_NAME + File.separator + SPEAKER_ITEM_FILENAME;

    public static final Pattern JAVA_CONFERENCE_DATE_REGEX   = Pattern.compile("(([0-9]{1,2})\\s+([A-Za-z]+)\\s+([0-9]{4}))|(([0-9]{1,2})[-–]([0-9]{1,2})\\s+([a-zA-Z]+)\\s+([0-9]{4}))|(([0-9]{1,2})\\s+([a-zA-Z]+)\\s-\\s+([0-9]{1,2})\\s([a-zA-Z]+)\\s+([0-9]{4}))");
    public static final Matcher JAVA_CONFERENCE_DATE_MATCHER = JAVA_CONFERENCE_DATE_REGEX.matcher("");
    public static final Pattern EVENT_ITEM_LOCATION_REGEX    = Pattern.compile("(@\\s([a-zA-Z\\s]+)\\()");
    public static final Matcher EVENT_ITEM_LOCATION_MATCHER  = EVENT_ITEM_LOCATION_REGEX.matcher("");
    public static final Pattern EVENT_ITEM_CITY_REGEX        = Pattern.compile("([A-Za-z0-9\\w\\.\\-\\s]+),");
    public static final Matcher EVENT_ITEM_CITY_MATCHER      = EVENT_ITEM_CITY_REGEX.matcher("");
    public static final Pattern EVENT_ITEM_COUNTRY_REGEX     = Pattern.compile("(@\\s([a-zA-Z\\s]+)\\(([a-zA-Z\\s-]+)\\))");
    public static final Matcher EVENT_ITEM_COUNTRY_MATCHER   = EVENT_ITEM_COUNTRY_REGEX.matcher("");
    public static final Pattern EVENT_ITEM_DATE_REGEX        = Pattern.compile("(\\s-\\s(([A-Za-z]{3})\\s([A-Za-z]{3})\\s([0-9]{1,2})\\s([0-9]{4})))");
    public static final Matcher EVENT_ITEM_DATE_MATCHER      = EVENT_ITEM_DATE_REGEX.matcher("");

    public static final String  CITY_DELIMITER               = ",";
    public static final String  PROFILE_IMAGE_NAME           = "ProfileImage";

    public static final int     SECONDS_PER_HOUR              = 3600;
    public static final int     SECONDS_PER_DAY               = 86400;
    public static final int     SECONDS_PER_WEEK              = 604800;

    public static final long    PING_INTERVAL_IN_SEC          = 2;
    public static final String  TEST_CONNECTIVITY_URL         = "https://apple.com";

    public static final double  STD_FONT_SIZE                 = 12;

    public static final Color   BLACK                         = Color.BLACK;
    public static final Color   WHITE                         = Color.WHITE;
    public static final Color   GRAY                          = Color.GRAY;
    public static final Color   DARK_GRAY                     = Color.DARKGRAY;
    public static final Color   RED                           = Color.color(0.996, 0.000, 0.000, 1.00); // RGB 254,   0, 0
    public static final Color   ORANGE                        = Color.color(1.000, 0.365, 0.004, 1.00); // RGB 255,  93, 0
    public static final Color   YELLOW                        = Color.color(1.000, 0.659, 0.000, 1.00); // RGB 255, 168, 0
    public static final Color   GREEN                         = Color.color(0.000, 0.761, 0.004, 1.00); //   0, 194, 1
    public static final Color   PURPLE                        = Color.color(0.620, 0.120, 0.640, 1.00);

    public static final String[] MONTHS                       = { "JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER" };

    public enum ConferenceType {
        IN_PERSON("In-Person", "in_person"),
        VIRTUAL("Virtual", "virtual"),
        HYBRID("Hybrid", "hybrid"),;

        public final String uiString;
        public final String apiString;

        ConferenceType(final String uiString, final String apiString) {
            this.uiString  = uiString;
            this.apiString = apiString;
        }


        public static ConferenceType fromText(final String text) {
            switch(text) {
                case "in_person" -> { return IN_PERSON; }
                case "virtual"   -> { return VIRTUAL; }
                case "hybrid"    -> { return HYBRID; }
                default          -> { return null; }
            }
        }
    }

    public enum AttendingStatus {
        NOT_ATTENDING(0, "Not Attending", "not_attending", Helper.getSecondaryColor()),
        ATTENDING(1, "Attending", "attending", Helper.getPrimaryColor()),
        SPEAKING(2, "Speaking", "speaking", GREEN);

        public final int    id;
        public final String uiString;
        public final String apiString;
        public final Color  color;


        AttendingStatus(final int id, final String uiString, final String apiString, final Color color) {
            this.id        = id;
            this.uiString  = uiString;
            this.apiString = apiString;
            this.color     = color;
        }


        public static final AttendingStatus fromText(final String text) {
            switch(text) {
                case "not_attending" -> { return NOT_ATTENDING; }
                case "attending"     -> { return ATTENDING; }
                case "speaking"      -> { return SPEAKING; }
                default              -> { return NOT_ATTENDING; }
            }
        }

        public static final int getIndexFromText(final String text) {
            switch(text) {
                case "not_attending" -> { return NOT_ATTENDING.id; }
                case "attending"     -> { return ATTENDING.id; }
                case "speaking"      -> { return SPEAKING.id; }
                default              -> { return NOT_ATTENDING.id; }
            }
        }

        public static final String[] getUiStrings() {
            return new String[] { NOT_ATTENDING.uiString, ATTENDING.uiString, SPEAKING.uiString };
        }
    }

    public enum ProposalStatus {
        NOT_SUBMITTED(0, "Not Submitted", "not_submitted", Helper.getSecondaryColor()),
        SUBMITTED(1, "Submitted", "submitted", Helper.getPrimaryColor()),
        ACCEPTED(2, "Accepted", "accepted", GREEN),
        REJECTED(3, "Rejected", "rejected", RED);

        public final int    id;
        public final String uiString;
        public final String apiString;
        public final Color  color;


        ProposalStatus(final int id, final String uiString, final String apiString, final Color color) {
            this.id        = id;
            this.uiString  = uiString;
            this.apiString = apiString;
            this.color     = color;
        }

        public static final ProposalStatus fromText(final String text) {
            switch(text) {
                case "not_submitted" -> { return NOT_SUBMITTED; }
                case "submitted"     -> { return SUBMITTED; }
                case "accepted"      -> { return ACCEPTED; }
                case "rejected"      -> { return REJECTED; }
                default              -> { return NOT_SUBMITTED; }
            }
        }

        public static final ProposalStatus fromId(final int id) {
            switch(id) {
                case 0  -> { return NOT_SUBMITTED; }
                case 1  -> { return SUBMITTED; }
                case 2  -> { return ACCEPTED; }
                case 3  -> { return REJECTED; }
                default -> { return NOT_SUBMITTED; }
            }
        }

        public static final int getIndexFromText(final String text) {
            switch(text) {
                case "not_submitted" -> { return NOT_SUBMITTED.id; }
                case "submitted"     -> { return SUBMITTED.id; }
                case "accepted"      -> { return ACCEPTED.id; }
                case "rejected"      -> { return REJECTED.id; }
                default              -> { return NOT_SUBMITTED.id; }
            }
        }

    }

    public enum Continent {
        ALL(0, "ALL", "All Continents"),
        AFRICA(1, "AF", "Africa"),
        ANTARCTICA(2, "AN", "Antarctica"),
        ASIA(3, "AS", "Asia"),
        EUROPE(4,  "EU", "Europe"),
        NORTH_AMERICA(5,  "NA", "North America"),
        OCEANIA(6,  "OC", "Oceania"),
        SOUTH_AMERICA(7,  "SA", "South America");

        public final int    id;
        public final String code;
        public final String name;


        Continent(final int id, final String code, final String name) {
            this.id   = id;
            this.code = code;
            this.name = name;
        }

        public static final Continent fromText(final String text) {
            switch(text) {
                case "All Continents" -> { return ALL; }
                case "Africa"         -> { return AFRICA; }
                case "Antarctica"     -> { return ANTARCTICA; }
                case "Asia"           -> { return ASIA; }
                case "Europe"         -> { return EUROPE; }
                case "North America"  -> { return NORTH_AMERICA; }
                case "Oceania"        -> { return OCEANIA; }
                case "South America"  -> { return SOUTH_AMERICA; }
                default               -> { return ALL; }
            }
        }
    }

    public enum Filter {
        ALL("All"),
        SPEAKING("Speaking"),
        ATTENDING("Attending"),
        CFP_OPEN("CFP Open");

        private String name;

        Filter(final String name) {
            this.name = name;
        }


        public String getName() { return this.name; }
    }
}
