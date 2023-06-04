package kdg.be.riskbackend.game.services;

import kdg.be.riskbackend.game.domain.map.Neighbor;
import kdg.be.riskbackend.game.repositories.NeighborRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles all the logic for the neighbors
 */
@Slf4j
@Service
@AllArgsConstructor
public class NeighborService {
    private final NeighborRepository neighborRepository;

    /**
     * generates all the neighbors of the territories
     *
     * @param surroundingTerritoryNames the names of the surrounding territories
     * @return a list of neighbors
     */
    public List<Neighbor> generateNeighbors(List<String> surroundingTerritoryNames) {
        return neighborRepository.findByNames(surroundingTerritoryNames);
    }

    /**
     * gets all the neighbors of a territory
     * @param territoryId the id of the territory
     * @return a list of neighbors
     */
    public List<Neighbor> getAllNeighborsOfTerritory(long territoryId) {
        return neighborRepository.findNeighborsOfTerritory(territoryId).getNeighbors();
    }
}
