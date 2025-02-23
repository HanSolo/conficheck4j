package eu.hansolo.fx.conficheck4j.tools;

import eu.hansolo.fx.conficheck4j.flag.Flag;


public record IsoCountryInfo(String name, String numeric, String alpha2, String alpha3, String calling, String currency, String continent, int fractionDigits) {
    public Flag getFlag() {
        return Flag.getAsList().parallelStream().filter(flag -> flag.getIso2().equals(alpha2)).findFirst().orElse(Flag.NOT_FOUND);
    }
}
