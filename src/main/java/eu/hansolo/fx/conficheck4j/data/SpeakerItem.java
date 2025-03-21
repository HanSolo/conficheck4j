package eu.hansolo.fx.conficheck4j.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.hansolo.toolbox.Constants;

import java.util.Objects;


public class SpeakerItem {
    public static final String FIELD_TITLE      = "title";
    public static final String FIELD_FIRST_NAME = "first_name";
    public static final String FIELD_LAST_NAME  = "last_name";
    public static final String FIELD_BLUESKY    = "bluesky";
    public static final String FIELD_BIO        = "bio";
    public static final String FIELD_EXPERIENCE = "experience";
    private             String title            = "";
    private             String firstName        = "";
    private             String lastName         = "";
    private             String bluesky          = "";
    private             String bio              = "";
    private             String experience       = "";


    public SpeakerItem(final String title, final String firstName, final String lastName, final String bluesky, final String bio, final String experience) {
        this.title      = title;
        this.firstName  = firstName;
        this.lastName   = lastName;
        this.bluesky    = bluesky;
        this.bio        = bio;
        this.experience = experience;
    }

    public String getTitle() { return title; }
    public void setTitle(final String title) { this.title = title; }

    public String getFirstName() { return firstName; }
    public void setFirstName(final String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(final String lastName) { this.lastName = lastName; }

    public String getBluesky() { return bluesky; }
    public void setBluesky(final String bluesky) { this.bluesky = bluesky; }

    public String getBio() { return bio; }
    public void setBio(final String bio) { this.bio = bio; }

    public String getExperience() { return experience; }
    public void setExperience(final String experience) { this.experience = experience; }

    public final String toJsonString() {
        return new StringBuilder().append(Constants.CURLY_BRACKET_OPEN)
                                  .append(Constants.QUOTES).append(FIELD_TITLE).append(Constants.QUOTES_COLON_QUOTES).append(this.title).append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_FIRST_NAME).append(Constants.QUOTES_COLON_QUOTES).append(this.firstName).append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_LAST_NAME).append(Constants.QUOTES_COLON_QUOTES).append(this.lastName).append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_BLUESKY).append(Constants.QUOTES_COLON_QUOTES).append(this.bluesky).append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_BIO).append(Constants.QUOTES_COLON_QUOTES).append(this.bio).append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_EXPERIENCE).append(Constants.QUOTES_COLON_QUOTES).append(this.experience).append(Constants.QUOTES)
                                  .append(Constants.CURLY_BRACKET_CLOSE)
                                  .toString();
    }

    public static final SpeakerItem fromJsonString(final String jsonText) {
        if (null == jsonText || jsonText.isEmpty()) { return null; }
        final Gson        gson        = new Gson();
        final JsonElement jsonElement = gson.fromJson(jsonText, JsonElement.class);
        if (null == jsonElement) { return null; }
        if (jsonElement.isJsonObject()) {
            final JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (null == jsonObject) { return null; }
            return fromJsonObject(jsonObject);
        } else {
            return null;
        }
    }
    public static final SpeakerItem fromJsonObject(final JsonObject jsonObject) {
        final String title      = jsonObject.has(FIELD_TITLE)      ? jsonObject.get(FIELD_TITLE).getAsString()      : "";
        final String firstName  = jsonObject.has(FIELD_FIRST_NAME) ? jsonObject.get(FIELD_FIRST_NAME).getAsString() : "";
        final String lastName   = jsonObject.has(FIELD_LAST_NAME)  ? jsonObject.get(FIELD_LAST_NAME).getAsString()  : "";
        final String bluesky    = jsonObject.has(FIELD_BLUESKY)    ? jsonObject.get(FIELD_BLUESKY).getAsString()    : "";
        final String bio        = jsonObject.has(FIELD_BIO)        ? jsonObject.get(FIELD_BIO).getAsString()        : "";
        final String experience = jsonObject.has(FIELD_EXPERIENCE) ? jsonObject.get(FIELD_EXPERIENCE).getAsString() : "";

        return new SpeakerItem(title, firstName, lastName, bluesky, bio, experience);
    }

    @Override public boolean equals(final Object obj) {
        if (obj == null || getClass() != obj.getClass()) { return false; }
        SpeakerItem that = (SpeakerItem) obj;
        return Objects.equals(title, that.title) &&
               Objects.equals(firstName, that.firstName) &&
               Objects.equals(lastName, that.lastName) &&
               Objects.equals(bluesky, that.bluesky);
    }
    @Override public int hashCode() {
        return Objects.hash(title, firstName, lastName, bluesky);
    }
}
