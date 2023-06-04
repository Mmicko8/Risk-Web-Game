package kdg.be.riskbackend.shop.service;

import java.time.LocalDateTime;
import java.util.List;
import kdg.be.riskbackend.game.domain.game.PlayerInGame;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.shop.service.loyalty_points.LoyaltyPointsCalculator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LoyaltyPointService {
    private final PlayerService playerService;
    private final LoyaltyPointsCalculator loyaltyPointCalculator;

    /**
     * calculates the loyalty points for a player based on the time spent in a game
     * then adds the loyalty points to the player
     *
     * @param playersInGame the players in the game
     * @param startTime     the start time of the game
     * @param endTime       the end time of the game
     */
    public void addLoyaltyPoints(List<PlayerInGame> playersInGame, LocalDateTime startTime,
                                 LocalDateTime endTime) {
        int loyaltyPoints = loyaltyPointCalculator.calculateLoyaltyPoints(startTime, endTime);
        playersInGame.forEach(playerInGame -> {
            playerInGame.getPlayer().addLoyaltyPoints(loyaltyPoints);
            playerService.save(playerInGame.getPlayer());
        });

    }
}
