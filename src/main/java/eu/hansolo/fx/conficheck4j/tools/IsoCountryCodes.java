package eu.hansolo.fx.conficheck4j.tools;

import java.util.Optional;


public class IsoCountryCodes {
    public static final Optional<IsoCountryInfo> find(final String key) {
        return IsoCountries.ALL_COUNTRIES.parallelStream()
                                  .filter(isoCountryInfo -> isoCountryInfo.alpha2().equals(key.toUpperCase()) ||
                                                                         isoCountryInfo.alpha3().equals(key.toUpperCase()) ||
                                                                         isoCountryInfo.numeric().equals(key.toUpperCase()))
                                  .findFirst();
    }

    public static final Optional<IsoCountryInfo> searchByName(final String name) {
        return IsoCountries.ALL_COUNTRIES.parallelStream().filter(isoCountryInfo -> isoCountryInfo.name().equalsIgnoreCase(name)).findFirst();
    }
}
