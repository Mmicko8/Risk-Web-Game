package kdg.be.riskbackend.game.dtos.game;

import kdg.be.riskbackend.game.domain.game.Phase;
import kdg.be.riskbackend.game.dtos.player_in_game.PlayerInGameWithoutCardsDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GameWithoutContinentsDto {
    private long gameId;
    private List<PlayerInGameWithoutCardsDto> playersInGame;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int timer;
    private int turn;
    private int currentPlayerIndex;
    private Phase phase;
}
