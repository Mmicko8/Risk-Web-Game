package kdg.be.riskbackend.lobby.repositories;

import kdg.be.riskbackend.lobby.domain.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LobbyRepository extends JpaRepository<Lobby, Long> {
    /**
     * finds a lobby with its players
     *
     * @param lobbyId the id of the lobby
     * @return an optional of the lobby
     */
    @Query("SELECT l FROM lobbies l LEFT JOIN FETCH l.players WHERE l.lobbyId = ?1")
    Optional<Lobby> findByIdWithPlayers(Long lobbyId);

    /**
     * finds an amount open lobbies
     *
     * @param amount the amount of lobbies
     * @return a list of lobbies
     */
    @Query(value = "SELECT * FROM lobbies l WHERE l.closed=false ORDER BY l.lobby_id DESC  LIMIT ?1", nativeQuery = true)
    List<Lobby> findOpenLobbies(int amount);

    /**
     * finds all lobbies of a player
     *
     * @param name the name of the player
     * @return a list of lobbies
     */
    @Query("SELECT l FROM lobbies l JOIN FETCH l.players pl WHERE pl.username = ?1")
    List<Lobby> findLobbiesOfPlayer(String name);

    /**
     * finds an amount of open lobbies that are not joined by the player
     *
     * @param amount the amount of lobbies
     * @param username the username of the player
     * @return a list of lobbies
     */
    @Query(value = "select l.lobby_id, l.host_player_id, l.max_players, l.closed, l.timer, count(*) as player_count" +
            " from lobbies l join lobbies_players lp on lp.lobbies_lobby_id = l.lobby_id" +
            " join players p on lp.players_player_id = p.player_id where l.lobby_id not in" +
            " (select l.lobby_id from lobbies l join lobbies_players lp on lp.lobbies_lobby_id = l.lobby_id" +
            " join players p on lp.players_player_id = p.player_id where l.closed=false and p.username = ?2)" +
            " and l.closed=false" +
            " group by l.lobby_id, l.host_player_id, l.max_players" +
            " order by l.lobby_id desc limit ?1", nativeQuery = true)
    List<Lobby> findOpenLobbiesNotJoinedByUser(int amount, String username);

    /**
     * finds all lobbies that the player joined but not started yet
     *
     * @param username the username of the player
     * @return a list of lobbies
     */
    @Query(value = "select * from lobbies l join lobbies_players lp on lp.lobbies_lobby_id = l.lobby_id " +
            "join players p on lp.players_player_id = p.player_id where l.closed=false and p.username = ?1", nativeQuery = true)
    List<Lobby> findJoinedNotStartedLobbies(String username);

    /**
     * counts all the open lobbies
     *
     * @return the amount of open lobbies
     */
    @Query("SELECT count(l) FROM lobbies l WHERE l.closed = false")
    int countOpenLobbies();

    /**
     * counts all the closed lobbies
     *
     * @return the amount of closed lobbies
     */
    @Query("SELECT count(l) FROM lobbies l WHERE l.closed = true")
    int countClosedLobbies();
}
