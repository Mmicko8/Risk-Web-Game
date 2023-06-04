package kdg.be.riskbackend.lobby.repositories;

import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.lobby.domain.Invite;
import kdg.be.riskbackend.lobby.domain.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * this interface is for the invite repository
 */
public interface InviteRepository extends JpaRepository<Invite, Long> {
    /**
     * finds all invites for a player
     *
     * @param username the username of the player
     * @return a list of invites
     */
    @Query("SELECT i FROM invites i WHERE i.player.username = ?1")
    List<Invite> findAllInvitesByUsername(String username);

    void deleteByUsernameSenderAndLobbyAndPlayer(String usernameSender, Lobby lobby, Player player);
}
