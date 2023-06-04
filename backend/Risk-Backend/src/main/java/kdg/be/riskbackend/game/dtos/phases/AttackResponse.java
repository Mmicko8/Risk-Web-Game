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
public class AttackResponse {
    private int amountOfSurvivingTroopsAttacker;
    private int amountOfSurvivingTroopsDefender;
    private long gameId;
    private List<Integer> attackerDices;
    private List<Integer> defenderDices;
    private boolean attackerWonGame;

    /**
     * class constructor
     *
     * @param gameId the id of the game
     * @param attackerDices the dices (eyes) of the attacker
     * @param defenderDices the dices (eyes) of the defender
     * @param attackerWonGame true if the attacker won the hole game
     */
    public AttackResponse(long gameId, List<Integer> attackerDices, List<Integer> defenderDices, boolean attackerWonGame) {
        this.attackerWonGame = attackerWonGame;
        this.gameId = gameId;
        this.attackerDices = attackerDices;
        this.defenderDices = defenderDices;
    }
}
