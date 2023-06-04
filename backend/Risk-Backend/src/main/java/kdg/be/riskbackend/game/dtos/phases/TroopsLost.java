package kdg.be.riskbackend.game.dtos.phases;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TroopsLost {
    private int amountOfTroopsLostAttacker;
    private int amountOfTroopsLostDefender;
}
