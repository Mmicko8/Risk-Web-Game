package kdg.be.riskbackend.game.repositories;

import kdg.be.riskbackend.game.domain.card.GameCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface GameCardRepository extends JpaRepository<GameCard, Long> {
    /**
     * finds all the game cards with the game id
     *
     * @param gameId the id of the game
     * @return List of all game cards from the given game
     */
    @Query("SELECT gc FROM game_cards gc WHERE gc.game.gameId = ?1")
    List<GameCard> findByGameId(long gameId);
}
