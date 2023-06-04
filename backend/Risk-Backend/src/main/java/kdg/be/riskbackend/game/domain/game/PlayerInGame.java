package kdg.be.riskbackend.game.domain.game;

import kdg.be.riskbackend.game.domain.card.PlayerCard;
import kdg.be.riskbackend.identity.domain.user.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * class between player and game
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "player_in_games")
public class PlayerInGame {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long playerInGameId;
    @PositiveOrZero(message = "Player must have at least 0 armies")
    private int remainingTroopsToReinforce;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    @NotNull(message = "Player cannot be null")
    private Player player;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "game_id")
    @NotNull(message = "Game cannot be null")
    private Game game;
    @NotNull(message = "Color cannot be null")
    private String color;
    private boolean conqueredATerritoryThisTurn;
    @OneToMany(mappedBy = "playerInGame", cascade = CascadeType.ALL)
    private List<PlayerCard> playerCards;
    private boolean hasLost;
    private boolean winner;

    /**
     * class constructor
     *
     * @param player the player
     * @param color  the color of the player
     */
    public PlayerInGame(Player player, String color) {
        this.player = player;
        this.color = color;
        this.remainingTroopsToReinforce = 0;
        this.conqueredATerritoryThisTurn = false;
    }

    /**
     * adds troops to reinforce your territory
     *
     * @param remainingTroopsToReinforce the amount of troops to reinforce
     */
    public void addRemainingTroopsToReinforce(int remainingTroopsToReinforce) {
        this.remainingTroopsToReinforce += remainingTroopsToReinforce;
    }

    /**
     * adds all playerCards to the player
     *
     * @param cardsFrom the cards to add
     */
    public void addPlayerCards(List<PlayerCard> cardsFrom) {
        this.playerCards.addAll(cardsFrom);
    }
}
