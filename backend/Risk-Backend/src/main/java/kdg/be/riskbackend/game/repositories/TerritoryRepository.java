package kdg.be.riskbackend.game.repositories;

import kdg.be.riskbackend.game.domain.map.Neighbor;
import kdg.be.riskbackend.game.domain.map.Territory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TerritoryRepository extends JpaRepository<Territory, Long> {
    /**
     * finds all territories of a game with their neighbors
     *
     * @param gameId the id of the game
     * @return a list of territories of the game
     */
    @Query("select DISTINCT t from territories t join fetch t.neighbors where t.continent.game.gameId = ?1")
    List<Territory> findAllByGameWithNeighbors(long gameId);

    /**
     * finds neighbors of a territory by the territory name and the game id
     *
     * @param territory the name of the territory
     * @param gameId    the id of the game
     * @return a list of neighbors
     */
    @Query("select t.neighbors from territories t where t.name = ?1 and t.continent.game.gameId = ?2")
    List<Neighbor> findTerritoryNeighborsByTerritoryNameAndGameId(String territory, long gameId);

    /**
     * finds a territory by his name and the game id
     *
     * @param name   the name of the territory
     * @param gameId the id of the game
     * @return a territory
     */
    @Query("select t from territories t where t.name = ?1 and t.continent.game.gameId = ?2")
    Territory findByNameAndGameId(String name, long gameId);

    /**
     * finds a territory by his name and the game id
     *
     * @param territoryName the name of the territory
     * @param gameId        the id of the game
     * @return a territory
     */
    @Query("select t from territories t join fetch t.neighbors  where t.name = ?1 and t.continent.game.gameId = ?2")
    Territory findByNameAndGameIdWithNeighbors(String territoryName, long gameId);

    /**
     * finds a territory with his owner by the territory id
     *
     * @param territoryId the id of the territory
     * @return an optional of a territory
     */
    @Query("select t from territories t join fetch t.owner where t.territoryId = ?1")
    Optional<Territory> findByIdWithOwner(long territoryId);

    /**
     * finds territories by the game id
     *
     * @param gameId the id of the game
     * @return a list of territories
     */
    @Query("select t from territories t where t.continent.game.gameId = ?1")
    List<Territory> findByGame(long gameId);

    /**
     * finds all territories with their owners by the game id
     *
     * @param gameId the id of the game
     * @return a list of all territories with their owners
     */
    @Query("select t from territories t join fetch t.owner where t.continent.game.gameId = ?1")
    List<Territory> findAllByGameIdWithOwner(long gameId);

    /**
     * finds all territories with their owners by the game id and the owner id
     *
     * @param gameId the id of the game
     * @param playerId the id of the player
     * @return a list of all territories with their owners from the player
     */
    @Query("select t from territories t join fetch t.owner where t.owner.player.playerId = ?1 AND t.continent.game.gameId = ?2")
    List<Territory> getTerritoriesByOwnerAndGame(long playerId, long gameId);

    /**
     * finds the phase by the territory id
     *
     * @param territoryId the id of the territory
     * @return the phase
     */
    @Query("select t.continent.game.phase from territories t  where t.territoryId = ?1")
    String getPhaseByTerritoryId(long territoryId);
}
