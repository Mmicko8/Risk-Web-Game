package kdg.be.riskbackend.game.dtos.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CardDto {
    private int cardId;
    private int stars;
    private String name;
}
