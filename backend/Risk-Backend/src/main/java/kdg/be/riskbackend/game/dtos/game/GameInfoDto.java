package kdg.be.riskbackend.game.dtos.game;

import kdg.be.riskbackend.game.domain.game.Phase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameInfoDto {
    private long gameId;
    private int timer;
    private int turn;
    private int currentPlayerIndex;
    private Phase phase;
}
