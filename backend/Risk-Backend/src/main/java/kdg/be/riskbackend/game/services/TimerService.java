package kdg.be.riskbackend.game.services;

import kdg.be.riskbackend.game.domain.game.Phase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * timer that checks if the current player is still active
 * if not, there wil be a skip to the next phase/player
 */
@Service
@EnableScheduling
@Slf4j
@AllArgsConstructor
public class TimerService {
    private GameService gameService;
    private PlayerInGameService playerInGameService;

    /**
     * checks if the current player is still active
     * if not, there wil be a skip to the next phase/player
     * Method is called every seconds
     */
    @Scheduled(fixedRate = 1000)
    public void countDown() {
        for (var game : gameService.getAllActiveGamesWithPlayers()) {
            var logging =
                    playerInGameService.getCurrentPlayerInGame(game).getPlayer().getUsername() +
                            " has not participated for " + game.getTimer() +
                            " seconds. ";
            if (game.getAfkThreshold().isBefore(LocalDateTime.now())) {
                if (game.getPhase().equals(Phase.FORTIFICATION)) {
                    gameService.getNextTurnPlayerOfGame(game.getGameId());
                    log.info(logging + "He was in the last phase. The next player is now playing.");
                } else {
                    gameService.nextPhase(game.getGameId());
                    log.info(logging + "He is now in the next phase");
                }
            }
        }
    }
}
