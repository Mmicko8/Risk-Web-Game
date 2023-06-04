package kdg.be.riskbackend.shop.service.loyalty_points;

import java.time.LocalDateTime;

public interface LoyaltyPointsCalculator {
    int calculateLoyaltyPoints(LocalDateTime startTime, LocalDateTime endTime);
}
