package kdg.be.riskbackend.game.repositories;

import kdg.be.riskbackend.game.domain.game.PlayerInGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlayerInGameRepository extends JpaRepository<PlayerInGame, Long> {
    /**
     * finds a player in game with his cards by its id
     *
     * @param playerInGameId the id of the player in game
     * @return an optional of a player in game with all his cards
     */
    @Query("SELECT p FROM player_in_games p LEFT JOIN FETCH p.playerCards WHERE p.playerInGameId = ?1")
    Optional<PlayerInGame> findByIdWithCards(long playerInGameId);

    /**
     * finds all the players in game with all their cards from a game
     *
     * @param gameId the id of the game
     * @return List of all players in game with their cards from the given game
     */
    @Query("SELECT DISTINCT p FROM player_in_games p LEFT JOIN FETCH p.playerCards WHERE p.game.gameId = ?1")
    List<PlayerInGame> findByGameIdWithCards(long gameId);

    /**
     * finds all the players in game from a game
     *
     * @param gameId the id of the game
     * @return List of all players in game from the given game
     */
    @Query("SELECT DISTINCT p FROM player_in_games p WHERE p.game.gameId = ?1")
    List<PlayerInGame> findByGameId(long gameId);
}
