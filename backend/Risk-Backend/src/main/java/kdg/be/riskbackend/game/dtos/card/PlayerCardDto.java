package kdg.be.riskbackend.game.dtos.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PlayerCardDto {
    private int playerCardId;
    private CardDto card;
}