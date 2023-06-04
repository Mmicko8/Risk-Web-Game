package kdg.be.riskbackend.lobby.domain;

import kdg.be.riskbackend.identity.domain.user.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * this class is for sending invites to other players, so they can join a lobby
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "invites")
public class Invite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotNull(message = "A valid username must be specified")
    private String usernameSender;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lobby_id")
    @NotNull(message = "A valid lobby must be specified")
    private Lobby lobby;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    @NotNull(message = "A valid player must be specified")
    private Player player;

    /**
     * class constructor
     *
     * @param usernameSender the username of the sender
     * @param lobby          the lobby to join
     * @param player         the player to invite
     */
    public Invite(Lobby lobby, Player player, String usernameSender) {
        this.lobby = lobby;
        this.player = player;
        this.usernameSender = usernameSender;
    }
}
