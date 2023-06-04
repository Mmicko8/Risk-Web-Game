package kdg.be.riskbackend.game.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import kdg.be.riskbackend.game.domain.map.Neighbor;
import kdg.be.riskbackend.game.domain.map.Territory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * handles recursive search for territories that can be fortified
 */
@Component
@AllArgsConstructor
public class FortificationTerritoryFinder {
    private final TerritoryService territoryService;

    /**
     * get all territories that can be fortified from a territory
     *
     * @param startTerritoryName the name of the territory from where the fortification starts
     * @param gameId             the id of the game
     * @return list of territories that can be fortified from startTerritory except startTerritory
     */
    public List<String> getAllFortifiableTerritories(String startTerritoryName, long gameId) {
        HashSet<String> fortifiableTerritories = new HashSet<>();
        fortifiableTerritories.add(startTerritoryName);
        var territories = new ArrayList<>(
                getFortifiableTerritories(startTerritoryName, fortifiableTerritories, gameId));
        territories.remove(startTerritoryName);
        return territories;
    }

    /**
     * fortifiableTerritories is a HashSet to prevent duplicates
     * recursive method to get all territories that can be fortified from a territory
     *
     * @param territoryName           the name of the territory from where the fortification starts
     * @param connectedTerritoryNames the names of the territories that can be fortified from territoryName
     * @param gameId                  the id of the game
     * @return set of territories that can be fortified from territoryName
     */
    private HashSet<String> getFortifiableTerritories(String territoryName,
                                                      HashSet<String> connectedTerritoryNames,
                                                      long gameId) {
        List<Territory> newConnectedTerritories = new ArrayList<>();
        Territory startTerritory =
                territoryService.getTerritoryByNameAndGameWithNeighborsAndOwner(territoryName,
                        gameId);
        //loop over neighbors and check that it has the same owner and is not already in the current list
        for (Neighbor neighbor : startTerritory.getNeighbors()) {
            Territory territory =
                    territoryService.getTerritoryByNameAndGame(neighbor.getName(), gameId);
            if (territory == null) {
                throw new RuntimeException("startTerritory not in allTerritories list");
            }
            //checks if the territory is owned by the same player and if it is not already in the list
            if (territory.getOwner().getPlayerInGameId() ==
                    startTerritory.getOwner().getPlayerInGameId() &&
                    !connectedTerritoryNames.contains(territory.getName())) {
                newConnectedTerritories.add(territory);
            }
        }
        connectedTerritoryNames.addAll(
                newConnectedTerritories.stream().map(Territory::getName).toList());
        for (Territory newConnectedTerritory : newConnectedTerritories) {
            var fortifiableTerritories = getFortifiableTerritories(newConnectedTerritory.getName(),
                    connectedTerritoryNames, gameId);
            connectedTerritoryNames.addAll(fortifiableTerritories);
        }
        return connectedTerritoryNames;
    }
}
