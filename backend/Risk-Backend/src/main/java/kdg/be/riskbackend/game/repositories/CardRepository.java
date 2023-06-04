package kdg.be.riskbackend.game.repositories;

import kdg.be.riskbackend.game.domain.card.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
