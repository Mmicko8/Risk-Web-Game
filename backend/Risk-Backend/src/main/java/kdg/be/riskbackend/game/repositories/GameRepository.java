package kdg.be.riskbackend.game.repositories;

import kdg.be.riskbackend.game.domain.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    /**
     * finds a game with all his players by the game id
     *
     * @param id the id of the game
     * @return een optional of the game
     */
    @Query("SELECT g FROM games g LEFT JOIN FETCH g.playersInGame WHERE g.gameId = ?1")
    Optional<Game> findGameByIdWithPlayers(long id);

    /**
     * finds the latest game
     *
     * @return een optional of the game
     */
    @Query(value = "SELECT * FROM games g  ORDER BY g.game_id DESC LIMIT 1", nativeQuery = true)
    Optional<Game> findLatestGame();

    /**
     * finds all games with their players from a given user that are not finished
     *
     * @param fromDate the date to compare the end date with
     * @param username the username of the user
     * @return List of games
     */
    @Query("SELECT g FROM games g JOIN FETCH g.playersInGame pig WHERE g.endTime > ?1 AND pig.player.username= ?2 ")
    List<Game> findGamesAfterEndDateWithUsername(LocalDateTime fromDate, String username);

    /**
     * finds the game with all its cards
     *
     * @param gameId the id of the game
     * @return An optional of the game
     */
    @Query("SELECT g FROM games g LEFT JOIN FETCH g.gameCards WHERE g.gameId = ?1")
    Optional<Game> findGameWithGameCards(long gameId);

    /**
     * finds all active games with all its users from a user
     *
     * @param username the username of the user
     * @return a list of games
     */
    @Query("select g from games g join fetch g.playersInGame pig where g.endTime is null and pig.player.username= ?1")
    List<Game> findActiveGamesWithUsernamesFromUser(String username);

    /**
     * finds all active games with all its players
     *
     * @return a list of games
     */
    @Query("select distinct g from games g join fetch g.playersInGame where g.endTime is null")
    List<Game> findAllActiveGamesWithPlayers();

    /**
     * counts all active games
     *
     * @return the amount of active games
     */
    @Query("select count(g) from games g where g.endTime is null")
    int countActiveGames();

    /**
     * counts all finished games
     *
     * @return the amount of finished games
     */
    @Query("select count(g) from games g where g.endTime is not null")
    int countFinishedGames();
}
