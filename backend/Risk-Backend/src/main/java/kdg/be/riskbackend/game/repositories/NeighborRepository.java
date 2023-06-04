package kdg.be.riskbackend.game.repositories;

import kdg.be.riskbackend.game.domain.map.Neighbor;
import kdg.be.riskbackend.game.domain.map.Territory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NeighborRepository extends JpaRepository<Neighbor, Long> {
    /**
     * finds all neighbors of a territory
     *
     * @param surroundingTerritoryNames the names of the territories
     * @return a list of neighbors of the territory
     */
    @Query("SELECT n FROM neighbors n WHERE n.name IN ?1")
    List<Neighbor> findByNames(List<String> surroundingTerritoryNames);

    /**
     * finds all neighbors of a territory
     *
     * @param territoryId the id of the territory
     * @return a territory that is a neighbor of the given territory
     */
    @Query("SELECT t FROM territories t join fetch t.neighbors n WHERE t.territoryId = ?1")
    Territory findNeighborsOfTerritory(long territoryId);
}
