package kdg.be.riskbackend.achievements.repositories;


import kdg.be.riskbackend.achievements.domain.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for the achievement class
 */
public interface AchievementRepository extends JpaRepository<Achievement, Long> {
    Optional<Achievement> findByName(String name);
}
