package kdg.be.riskbackend.game.util;


import lombok.Getter;

@Getter
public enum ContinentName {
    AFRICA("Africa"),
    ASIA("Asia"),
    AUSTRALIA("Australia"),
    EUROPE("Europe"),
    NORTH_AMERICA("North America"),
    SOUTH_AMERICA("South America");
    private final String name;

    /**
     * class constructor
     *
     * @param value the name of the continent
     */
    ContinentName(final String value) {
        this.name = value;
    }
}
