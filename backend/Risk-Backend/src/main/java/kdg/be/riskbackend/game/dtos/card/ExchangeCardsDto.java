package kdg.be.riskbackend.game.dtos.card;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeCardsDto {
    @Positive(message = "A valid game must be specified")
    private long gameId;
    @Size(min=1, message = "You need to select a minimum of 1 card")
    private List<String> cardNames;
}
