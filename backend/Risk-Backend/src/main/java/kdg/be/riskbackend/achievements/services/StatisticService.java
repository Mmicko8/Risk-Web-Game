package kdg.be.riskbackend.achievements.services;

import kdg.be.riskbackend.game.domain.game.PlayerInGame;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.services.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.List;

/**
 * This class represents the service for the statistics.
 * It contains the business logic for the statistics.
 */
@Service
@AllArgsConstructor
public class StatisticService {

    private PlayerService playerService;

    /**
     * adds 1 to the games played of the players in the given list
     *
     * @param playersInGame the list of players in the game
     */
    public void addGamesPlayedToPlayers(@Valid List<PlayerInGame> playersInGame) {
        for (PlayerInGame playerInGame : playersInGame) {
            Player player = playerInGame.getPlayer();
            player.setGamesPlayed(player.getGamesPlayed() + 1);
            playerService.save(player);
        }
    }
}
