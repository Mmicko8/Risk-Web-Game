package kdg.be.riskbackend.game.repositories;

import kdg.be.riskbackend.game.domain.card.PlayerCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlayerCardRepository extends JpaRepository<PlayerCard, Long> {
    /**
     * finds a player card by its name and ownerId
     *
     * @param cardName the name of the card
     * @param playerInGameId the id of the player
     * @return An optional of a player card
     */
    @Query("SELECT pc FROM player_cards pc WHERE pc.card.name = ?1 AND pc.playerInGame.playerInGameId = ?2")
    Optional<PlayerCard> findByNameAndPlayerInGameId(String cardName, Long playerInGameId);

    /**
     * finds all the player cards from a player
     *
     * @param fromId the id of the player
     * @return List of all player cards from the given player
     */
    @Query("SELECT pc FROM player_cards pc  WHERE pc.playerInGame.playerInGameId = ?1")
    List<PlayerCard> findAllByPlayerInGame(long fromId);
}
