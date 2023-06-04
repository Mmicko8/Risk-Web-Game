package kdg.be.riskbackend.game.domain.card;

import kdg.be.riskbackend.game.domain.game.PlayerInGame;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * class between player and card is many to many, it contains all the cards of a player
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "player_cards")
public class PlayerCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long playerCardId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_in_game_id")
    @NotNull(message = "Player in game cannot be null")
    private PlayerInGame playerInGame;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "card_id")
    @NotNull(message = "Card in game cannot be null")
    private Card card;

    public PlayerCard(PlayerInGame playerInGame, Card card) {
        this.playerInGame = playerInGame;
        this.card = card;
    }
}
