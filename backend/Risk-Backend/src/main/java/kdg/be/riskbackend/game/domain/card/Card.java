package kdg.be.riskbackend.game.domain.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Cards are used to exchange for armies
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "cards")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cardId;

    @Min(value = 1, message = "Card star value must be at least 1")
    private int stars;
    @NotNull(message = "Card name must be set")
    private String name;

    @OneToMany(mappedBy = "card")
    List<GameCard> gameCards;
    @OneToMany(mappedBy = "card")
    List<PlayerCard> playerCards;
    public Card(String cardName, Integer stars) {
        this.name = cardName;
        this.stars = stars;
    }
}