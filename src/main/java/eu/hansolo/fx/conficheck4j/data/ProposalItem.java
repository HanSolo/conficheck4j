package eu.hansolo.fx.conficheck4j.data;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.hansolo.toolbox.Constants;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class ProposalItem {
    public static final String FIELD_TITLE    = "title";
    public static final String FIELD_ABSTRACT = "abstract";
    public static final String FIELD_PITCH    = "pitch";
    public static final String FIELD_STATE    = "state";
    private             String title          = "";
    private             String abstrakt       = "";
    private             String pitch          = "";


    public ProposalItem(final String title, final String abstrakt, final String pitch) {
        this.title    = title;
        this.abstrakt = abstrakt;
        this.pitch    = pitch;
    }


    public String getId() { return this.title; }

    public String getTitle() { return this.title; }
    public void setTitle(final String title) { this.title = title; }

    public String getAbstract() { return this.abstrakt; }
    public void setAbstract(final String abstrakt) { this.abstrakt = abstrakt; }

    public String getPitch() { return this.pitch; }
    public void setPitch(final String pitch) { this.pitch = pitch; }

    public final String toJsonString() {
        return new StringBuilder().append(Constants.CURLY_BRACKET_OPEN)
                                  .append(Constants.QUOTES).append(FIELD_TITLE).append(Constants.QUOTES_COLON_QUOTES).append(this.title).append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_TITLE).append(Constants.QUOTES_COLON_QUOTES).append(this.title).append(Constants.QUOTES).append(Constants.COMMA)
                                  .append(Constants.QUOTES).append(FIELD_TITLE).append(Constants.QUOTES_COLON_QUOTES).append(this.title).append(Constants.QUOTES)
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
        final String title    = jsonObject.has(FIELD_TITLE)    ? jsonObject.get(FIELD_TITLE).getAsString()    : "";
        final String abstrakt = jsonObject.has(FIELD_ABSTRACT) ? jsonObject.get(FIELD_ABSTRACT).getAsString() : "";
        final String pitch    = jsonObject.has(FIELD_PITCH)    ? jsonObject.get(FIELD_PITCH).getAsString()    : "";

        if (!title.isBlank() && !abstrakt.isBlank()) {
            return new ProposalItem(title, abstrakt, pitch);
        } else {
            return null;
        }
    }

    @Override public boolean equals(final Object obj) {
        if (obj == null || getClass() != obj.getClass()) { return false; }
        ProposalItem that = (ProposalItem) obj;
        return Objects.equals(title, that.title);
    }
    @Override public int hashCode() {
        return Objects.hash(title, abstrakt);
    }
}
