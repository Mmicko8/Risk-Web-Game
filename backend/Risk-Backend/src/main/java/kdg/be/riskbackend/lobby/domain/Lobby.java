package kdg.be.riskbackend.lobby.domain;

import kdg.be.riskbackend.identity.domain.user.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "lobbies")
public class Lobby {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long lobbyId;
    @Min(value = 2, message = "A lobby must have at least 2 players")
    private int maxPlayers = 5;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Player> players = new ArrayList<>();
    @OneToOne(fetch = FetchType.EAGER)
    @NotNull(message = "A lobby must have a host")
    private Player host;
    private boolean closed;
    @Min(value = 10, message = "A minimum timer of 10 is required")
    private int timer;

    /**
     * class constructor
     *
     * @param maxPlayers the maximum amount of players
     * @param player     the host of the game
     * @param timer      the timer of the game
     */
    public Lobby(int maxPlayers, Player player, int timer) {
        this.maxPlayers = maxPlayers;
        host = player;
        closed = false;
        this.timer = timer;
    }
}
