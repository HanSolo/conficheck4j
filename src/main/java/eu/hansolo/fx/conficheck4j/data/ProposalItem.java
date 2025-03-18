package eu.hansolo.fx.conficheck4j.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.hansolo.toolbox.Constants;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;


public class ProposalItem {
    public static final String FIELD_ID       = "id";
    public static final String FIELD_TITLE    = "title";
    public static final String FIELD_ABSTRACT = "abstract";
    public static final String FIELD_PITCH    = "pitch";
    public static final String FIELD_STATE    = "state";
    private             String id;
    private             String title          = "";
    private             String abstrakt       = "";
    private             String pitch          = "";


    public ProposalItem(final String title, final String abstrakt, final String pitch) {
        this(UUID.randomUUID().toString(), title, abstrakt, pitch);
    }
    ProposalItem(final String id, final String title, final String abstrakt, final String pitch) {
        this.id       = id;
        this.title    = title;
        this.abstrakt = abstrakt;
        this.pitch    = pitch;
    }

    public String getId() { return this.id; }
    private void setId(final String id) { this.id = id; }

    public String getTitle() { return this.title; }
    public void setTitle(final String title) { this.title = title; }

    public String getAbstract() { return this.abstrakt; }
    public void setAbstract(final String abstrakt) { this.abstrakt = abstrakt; }

    public String getPitch() { return this.pitch; }
    public void setPitch(final String pitch) { this.pitch = pitch; }

    public final String toJsonString() {
        return new StringBuilder().append(Constants.CURLY_BRACKET_OPEN)
                                  .append(Constants.QUOTES).append(FIELD_ID).append(Constants.QUOTES_COLON_QUOTES).append(this.id).append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_TITLE).append(Constants.QUOTES_COLON_QUOTES).append(this.title.replaceAll("\\n", " ").replaceAll("\"", Matcher.quoteReplacement("\\\""))).append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_ABSTRACT).append(Constants.QUOTES_COLON_QUOTES).append(this.abstrakt.replaceAll("\\n", " ").replaceAll("\"", Matcher.quoteReplacement("\\\""))).append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_PITCH).append(Constants.QUOTES_COLON_QUOTES).append(this.pitch.replaceAll("\\n", " ").replaceAll("\"", Matcher.quoteReplacement("\\\""))).append(Constants.QUOTES)
                                  .append(Constants.CURLY_BRACKET_CLOSE)
                                  .toString();
    }

    public static final ProposalItem fromJsonString(final String jsonText) {
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
    public static final ProposalItem fromJsonObject(final JsonObject jsonObject) {
        final String id       = jsonObject.has(FIELD_ID)       ? jsonObject.get(FIELD_ID).getAsString()       : UUID.randomUUID().toString();
        final String title    = jsonObject.has(FIELD_TITLE)    ? jsonObject.get(FIELD_TITLE).getAsString()    : "";
        final String abstrakt = jsonObject.has(FIELD_ABSTRACT) ? jsonObject.get(FIELD_ABSTRACT).getAsString() : "";
        final String pitch    = jsonObject.has(FIELD_PITCH)    ? jsonObject.get(FIELD_PITCH).getAsString()    : "";
        if (!title.isBlank()) {
            return new ProposalItem(id, title, abstrakt, pitch);
        } else {
            return null;
        }
    }

    @Override public boolean equals(final Object obj) {
        if (obj == null || getClass() != obj.getClass()) { return false; }
        ProposalItem that = (ProposalItem) obj;
        return Objects.equals(getId(), that.getId());
    }
    @Override public int hashCode() {
        return Objects.hash(title, abstrakt);
    }
}
