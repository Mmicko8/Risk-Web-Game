package kdg.be.riskbackend.shop.service.loyalty_points;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * calculates the loyalty points based on the time spent in a game
 *
 */
@Component
public class LoyaltyPointsTimeCalculation implements LoyaltyPointsCalculator {

    /**
     * calculates the loyalty points based on the time spent in a game
     *
     * @param startTime the start time of the game
     * @param endTime   the end time of the game
     * @return the loyalty points
     */
    @Override
    public int calculateLoyaltyPoints(LocalDateTime startTime, LocalDateTime endTime) {
        return endTime.minusMinutes(startTime.getMinute()).getMinute();
    }
}
