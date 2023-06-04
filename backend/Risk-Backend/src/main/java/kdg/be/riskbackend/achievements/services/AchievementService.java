package kdg.be.riskbackend.achievements.services;

import kdg.be.riskbackend.achievements.Exceptions.AchievementException;
import kdg.be.riskbackend.achievements.domain.Achievement;
import kdg.be.riskbackend.achievements.repositories.AchievementRepository;
import kdg.be.riskbackend.identity.domain.user.Player;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;

@Service
@AllArgsConstructor
public class AchievementService {
    private final AchievementRepository achievementRepository;

    /**
     * Method to get an achievement by its id
     *
     * @param achievementId the id of the achievement that is requested
     * @return the requested achievement
     */
    public Achievement getAchievementById(long achievementId) {
        return achievementRepository.findById(achievementId).orElseThrow(() -> new AchievementException("Achievement not found"));
    }

    /**
     * Method to get an achievement by its name
     *
     * @param name the name of the achievement that is requested
     * @return the requested achievement
     */
    public Achievement getAchievementByName(String name) {
        return achievementRepository.findByName(name).orElseThrow(() -> new AchievementException("Achievement not found"));
    }

    /**
     * Adds achievements for playing games to the given player if milestone is reached.
     *
     * @param player the player to give an achievement to
     * @return the same player instance
     */
    @Transactional
    public Player addGamesPlayedAchievementsToPlayer(@Valid Player player) {
        String name = switch ((int) player.getGamesPlayed()) {
            case 1 -> "The Newcomer Award";
            case 10 -> "The Emerging Talent Award";
            case 50 -> "The Prodigy Prize";
            default -> null;
        };
        if (name == null) return player;
        var achievement = getAchievementByName(name);
        player.getAchievements().add(achievement);
        player.setLoyaltyPoints(player.getLoyaltyPoints() + achievement.getPoints());
        return player;
    }

    /**
     * Adds achievements for winning games to the given player if milestone is reached.
     *
     * @param player the player to give an achievement to
     * @return the same player instance
     */
    @Transactional
    public Player addWinningAchievementsToPlayer(@Valid Player player) {
        String name = switch ((int) player.getGamesWon()) {
            case 1 -> "First Win";
            case 5 -> "Victory Lap";
            case 10 -> "Winning Streak";
            case 50 -> "Master Of The Game";
            default -> null;
        };
        if (name == null) return player;
        var achievement = getAchievementByName(name);
        player.getAchievements().add(achievement);
        player.setLoyaltyPoints(player.getLoyaltyPoints() + achievement.getPoints());
        return player;
    }

    /**
     * Adds achievements for losing games to the given player if milestone is reached.
     *
     * @param player the player to give an achievement to
     * @return the same player instance
     */
    public Player addLosingAchievementsToPlayer(@Valid Player player) {
        Achievement achievement;
        if (player.getGamesLost() != 50) {
            return player;
        }
        achievement = getAchievementByName("The Determination Trophy");
        player.getAchievements().add(achievement);
        player.setLoyaltyPoints(player.getLoyaltyPoints() + achievement.getPoints());
        return player;
    }

    /**
     * Adds achievements for winning games that have AI players to the given player if milestone is reached.
     *
     * @param player the player to give an achievement to
     * @return the same player instance
     */
    public Player addWinningAgainstAiAchievementPlayer(@Valid Player player) {
        String name = switch ((int) player.getGamesWonAgainstAi()) {
            case 1 -> "AI Annihilator";
            case 5 -> "Digital Dominator";
            case 10 -> "AI Overload";
            case 50 -> "Machine Master";
            default -> null;
        };
        if (name == null) return player;
        var achievement = getAchievementByName(name);
        player.getAchievements().add(achievement);
        player.setLoyaltyPoints(player.getLoyaltyPoints() + achievement.getPoints());
        return player;
    }

    public void saveAchievement(@Valid Achievement achievement) {
        achievementRepository.save(achievement);
    }
}
