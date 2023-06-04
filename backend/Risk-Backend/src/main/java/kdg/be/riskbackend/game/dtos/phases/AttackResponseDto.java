package kdg.be.riskbackend.game.dtos.phases;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AttackResponseDto {
    private int amountOfSurvivingTroopsAttacker;
    private int amountOfSurvivingTroopsDefender;
    private long gameId;
    private List<Integer> attackerDices;
    private List<Integer> defenderDices;
}
