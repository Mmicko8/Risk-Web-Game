package kdg.be.riskbackend.game.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for territories
 */
public class TerritoryUtil {
    /**
     * gets a map of all the continents which contain territories and their surrounding neighbors
     *
     * @return a map of all the continents and their surrounding territories
     */

    public static Map<ContinentName, Map<String, List<String>>> generateAllContinentsMap() {
        Map<ContinentName, Map<String, List<String>>> continentMap = new HashMap<>();
        //all countries
        continentMap.put(ContinentName.EUROPE, new HashMap<>());
        continentMap.put(ContinentName.AFRICA, new HashMap<>());
        continentMap.put(ContinentName.ASIA, new HashMap<>());
        continentMap.put(ContinentName.AUSTRALIA, new HashMap<>());
        continentMap.put(ContinentName.NORTH_AMERICA, new HashMap<>());
        continentMap.put(ContinentName.SOUTH_AMERICA, new HashMap<>());
        //africa and surrounding territories
        continentMap.get(ContinentName.AFRICA).put("EastAfrica", List.of("Egypt", "NorthAfrica", "Congo", "SouthAfrica", "Madagascar", "MiddleEast"));
        continentMap.get(ContinentName.AFRICA).put("Egypt", List.of("NorthAfrica", "EastAfrica", "MiddleEast", "SouthEurope"));
        continentMap.get(ContinentName.AFRICA).put("NorthAfrica", List.of("Egypt", "EastAfrica", "Congo", "Brazil", "WestEurope", "SouthEurope"));
        continentMap.get(ContinentName.AFRICA).put("Congo", List.of("NorthAfrica", "EastAfrica", "SouthAfrica"));
        continentMap.get(ContinentName.AFRICA).put("SouthAfrica", List.of("Congo", "EastAfrica", "Madagascar"));
        continentMap.get(ContinentName.AFRICA).put("Madagascar", List.of("SouthAfrica", "EastAfrica"));
        //asia and surrounding territories
        continentMap.get(ContinentName.ASIA).put("Ural", List.of("Siberia", "China", "Afghanistan", "Ukraine"));
        continentMap.get(ContinentName.ASIA).put("Siberia", List.of("Ural", "China", "Mongolia", "Irkutsk", "Yakutsk"));
        continentMap.get(ContinentName.ASIA).put("China", List.of("Siberia", "Mongolia", "India", "Siam", "Afghanistan", "Ural"));
        continentMap.get(ContinentName.ASIA).put("Mongolia", List.of("Siberia", "China", "Japan", "Irkutsk", "Kamchatka"));
        continentMap.get(ContinentName.ASIA).put("Irkutsk", List.of("Siberia", "Mongolia", "Yakutsk", "Kamchatka"));
        continentMap.get(ContinentName.ASIA).put("Yakutsk", List.of("Siberia", "Irkutsk", "Kamchatka"));
        continentMap.get(ContinentName.ASIA).put("Kamchatka", List.of("Yakutsk", "Irkutsk", "Japan", "Alaska", "Mongolia"));
        continentMap.get(ContinentName.ASIA).put("Japan", List.of("Mongolia", "Kamchatka"));
        continentMap.get(ContinentName.ASIA).put("Afghanistan", List.of("Ural", "China", "India", "MiddleEast", "Ukraine"));
        continentMap.get(ContinentName.ASIA).put("India", List.of("Afghanistan", "China", "Siam", "MiddleEast"));
        continentMap.get(ContinentName.ASIA).put("Siam", List.of("India", "China", "Indonesia"));
        continentMap.get(ContinentName.ASIA).put("MiddleEast", List.of("Ukraine", "Afghanistan", "India", "Egypt", "SouthEurope", "EastAfrica"));
        //australia and surrounding territories
        continentMap.get(ContinentName.AUSTRALIA).put("Indonesia", List.of("Siam", "PapuaNewGuinea", "WestAustralia", "EastAustralia"));
        continentMap.get(ContinentName.AUSTRALIA).put("PapuaNewGuinea", List.of("Indonesia", "WestAustralia", "EastAustralia"));
        continentMap.get(ContinentName.AUSTRALIA).put("WestAustralia", List.of("PapuaNewGuinea", "Indonesia", "EastAustralia"));
        continentMap.get(ContinentName.AUSTRALIA).put("EastAustralia", List.of("PapuaNewGuinea", "WestAustralia"));
        //europe and surrounding territories
        continentMap.get(ContinentName.EUROPE).put("Ukraine", List.of("Ural", "Afghanistan", "MiddleEast", "SouthEurope", "NorthEurope", "Scandinavia"));
        continentMap.get(ContinentName.EUROPE).put("WestEurope", List.of("GreatBritain", "NorthEurope", "SouthEurope", "NorthAfrica"));
        continentMap.get(ContinentName.EUROPE).put("NorthEurope", List.of("Ukraine", "WestEurope", "SouthEurope", "Scandinavia", "GreatBritain"));
        continentMap.get(ContinentName.EUROPE).put("SouthEurope", List.of("NorthEurope", "WestEurope", "NorthAfrica", "Egypt", "MiddleEast", "Ukraine"));
        continentMap.get(ContinentName.EUROPE).put("Scandinavia", List.of("NorthEurope", "GreatBritain", "Iceland", "Ukraine"));
        continentMap.get(ContinentName.EUROPE).put("GreatBritain", List.of("Scandinavia", "NorthEurope", "Iceland", "WestEurope"));
        continentMap.get(ContinentName.EUROPE).put("Iceland", List.of("Scandinavia", "GreatBritain", "Greenland"));
        //north america and surrounding territories
        continentMap.get(ContinentName.NORTH_AMERICA).put("Alaska", List.of("Kamchatka", "Alberta", "NorthWest"));
        continentMap.get(ContinentName.NORTH_AMERICA).put("Alberta", List.of("Alaska", "NorthWest", "Ontario", "WestUS"));
        continentMap.get(ContinentName.NORTH_AMERICA).put("NorthWest", List.of("Alaska", "Alberta", "Ontario", "Greenland"));
        continentMap.get(ContinentName.NORTH_AMERICA).put("Ontario", List.of("NorthWest", "Alberta", "WestUS", "EastUS", "Greenland", "Quebec"));
        continentMap.get(ContinentName.NORTH_AMERICA).put("Greenland", List.of("NorthWest", "Ontario", "Iceland", "Quebec"));
        continentMap.get(ContinentName.NORTH_AMERICA).put("Quebec", List.of("Greenland", "Ontario", "EastUS"));
        continentMap.get(ContinentName.NORTH_AMERICA).put("WestUS", List.of("Alberta", "Ontario", "EastUS", "CentralAmerica"));
        continentMap.get(ContinentName.NORTH_AMERICA).put("EastUS", List.of("WestUS", "Ontario", "CentralAmerica", "Quebec"));
        continentMap.get(ContinentName.NORTH_AMERICA).put("CentralAmerica", List.of("WestUS", "EastUS", "Venezuela"));
        //south america and surrounding territories
        continentMap.get(ContinentName.SOUTH_AMERICA).put("Venezuela", List.of("CentralAmerica", "Brazil", "Peru"));
        continentMap.get(ContinentName.SOUTH_AMERICA).put("Brazil", List.of("Venezuela", "Peru", "NorthAfrica", "Argentina"));
        continentMap.get(ContinentName.SOUTH_AMERICA).put("Peru", List.of("Venezuela", "Brazil", "Argentina"));
        continentMap.get(ContinentName.SOUTH_AMERICA).put("Argentina", List.of("Peru", "Brazil"));
        return continentMap;
    }

    /**
     * gets all the territories
     *
     * @return a list of all territories
     */
    public static List<String> getAllTerritoryNames() {
        return Arrays.asList("EastAfrica", "Egypt", "Madagascar", "SouthAfrica", "NorthAfrica",
                "Afghanistan", "China", "India", "Irkutsk", "Japan",
                "Kamchatka", "MiddleEast", "Mongolia", "Siam", "Siberia", "Ural", "Yakutsk"
                , "EastAustralia", "PapuaNewGuinea", "Indonesia", "WestAustralia"
                , "GreatBritain", "Iceland", "NorthEurope", "Scandinavia", "SouthEurope", "Ukraine", "WestEurope"
                , "Alaska", "Alberta", "CentralAmerica", "EastUS", "Greenland", "NorthWest", "Ontario", "Congo", "WestUS"
                , "Argentina", "Brazil", "Peru", "Venezuela", "Quebec");
    }
}
