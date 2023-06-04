package kdg.be.riskbackend.identity.repositories;

import java.util.List;
import java.util.Optional;
import kdg.be.riskbackend.identity.domain.user.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * This interface is used to communicate with the database for players.
 */
@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    /**
     * finds a player by his email
     *
     * @param email the email of the player
     * @return an optional of the player
     */
    Optional<Player> findByEmail(String email);

    /**
     * enables a player by his email
     *
     * @param email the email of the player
     */
    @Modifying
    @Query("Update players  set enabled=true WHERE email = ?1")
    void enablePlayer(String email);

    /**
     * finds a player by his username
     *
     * @param username the username of the player
     * @return an optional of the player
     */
    Optional<Player> findByUsername(String username);

    /**
     * finds a player with his friends by his username
     *
     * @param username the username of the player
     * @return an optional of the player
     */
    @Query("SELECT p FROM players p LEFT JOIN FETCH p.friends WHERE p.username = ?1 ")
    Optional<Player> findUserByUsernameWithFriends(String username);

    /**
     * finds all the AI players
     *
     * @return a player
     */
    @Query(value = "SELECT * FROM players p WHERE p.is_ai = true  ORDER BY RAND()  LIMIT 1", nativeQuery = true)
    Player findAllAiPlayer();

    /**
     * finds all the players you recently played with
     *
     * @param username the username of the player
     * @return a list of players
     */
    @Query("SELECT DISTINCT pig1.player FROM player_in_games pig1  where pig1.player.isAi=false AND pig1.player.username <> ?1 AND pig1.game IN (SELECT pig2.game FROM player_in_games pig2 WHERE pig2.player.username = ?1)")
    List<Player> findRecentlyPlayedWith(String username);

    /**
     * finds all local players except the current player
     *
     * @param ssidName the ssid of the player
     * @param username the username of the player
     * @return a list of players
     */
    @Query("SELECT p FROM players p WHERE p.ssid = ?1 AND p.username <> ?2")
    List<Player> findAllLocalPlayersExceptCurrent(String ssidName, String username);

    /**
     * finds player with its shop items by their id
     *
     * @param id the id of the player
     * @return an optional of the player
     */
    @Query("SELECT p FROM players p JOIN FETCH p.shopItems WHERE p.playerId = ?1")
    Optional<Player> findWithShopItems(long id);

    /**
     * finds player with its shop items by their username
     *
     * @param username the username of the player
     * @return an optional of the player
     */
    @Query("SELECT p FROM players p LEFT JOIN FETCH p.shopItems WHERE   p.username = ?1")
    Optional<Player> findByUsernameWithShopItems(String username);

    /**
     * finds all players sorted by their amount of winning games
     *
     * @return a list of players
     */
    @Query(value = "SELECT * FROM players p ORDER BY  p.games_won DESC  LIMIT 10", nativeQuery = true)
    List<Player> findLeaderboard();

    /**
     * finds a player with its achievements by there id
     *
     * @param playerId the id of the player
     * @return an optional of the player
     */
    @Query("SELECT p FROM players p LEFT JOIN FETCH p.achievements WHERE p.playerId = ?1")
    Optional<Player> findByIdWithAchievements(long playerId);

    /**
     * finds a player with its friend requests
     *
     * @param name the name of the player
     * @return an optional of the player
     */
    @Query("SELECT p FROM players p LEFT JOIN FETCH p.friendRequests WHERE p.username = ?1")
    Optional<Player> findPlayerWithFriendRequests(String name);
}
