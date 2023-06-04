package kdg.be.riskbackend.game.domain.card;

import kdg.be.riskbackend.game.domain.game.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * class between game and card is many to many, it contains all the cards in a game
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "game_cards")
public class GameCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gameCardId;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "game_id")
    @NotNull(message = "Game cannot be null")
    private Game game;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "card_id")
    @NotNull(message = "Card cannot be null")
    private Card card;

    public GameCard(Game game, Card card) {
        this.game = game;
        this.card = card;
    }
}
