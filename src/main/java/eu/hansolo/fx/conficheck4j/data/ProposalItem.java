package eu.hansolo.fx.conficheck4j.data;

import java.util.Objects;


public class ProposalItem {
    private String title    = "";
    private String abstrakt = "";
    private String pitch    = "";


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

    @Override public boolean equals(final Object obj) {
        if (obj == null || getClass() != obj.getClass()) { return false; }
        ProposalItem that = (ProposalItem) obj;
        return Objects.equals(title, that.title);
    }
    @Override public int hashCode() {
        return Objects.hash(title, abstrakt);
    }
}
