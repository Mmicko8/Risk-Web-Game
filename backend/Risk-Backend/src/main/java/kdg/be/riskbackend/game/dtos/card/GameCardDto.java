package kdg.be.riskbackend.game.dtos.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GameCardDto {
    private int gameCardId;
    private CardDto card;
}
