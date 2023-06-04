package kdg.be.riskbackend.game.repositories;

import kdg.be.riskbackend.game.domain.map.Continent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContinentRepository extends JpaRepository<Continent, Long> {
    /**
     * finds all continents with all their territories
     *
     * @return List of all continents with all their territories
     */
    @Query("SELECT DISTINCT c from continents c JOIN FETCH c.territories  t where c.game.gameId = ?1")
    List<Continent> findContinentWithTerritories(long gameId);
}
