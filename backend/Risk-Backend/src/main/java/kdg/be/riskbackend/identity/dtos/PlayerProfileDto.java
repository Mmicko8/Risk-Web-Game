package kdg.be.riskbackend.identity.dtos;

import kdg.be.riskbackend.achievements.dtos.AchievementDto;
import kdg.be.riskbackend.shop.dtos.ShopItemDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerProfileDto {
    private long id;
    private String username;
    private String email;
    private long gamesWon;
    private long gamesLost;
    private long gamesPlayed;
    private long loyaltyPoints;
    private String profilePicture;
    private String title;
    List<AchievementDto> achievements;
    List<ShopItemDto> shopItems;
}

