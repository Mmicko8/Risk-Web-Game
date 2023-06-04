package kdg.be.riskbackend.identity.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDto {
    private long id;
    @NotNull(message = "Username is required")
    @Length(min = 2, max = 15, message = "username must be between 2 and 15 characters")
    private String username;
    @Email(message = "Email should be valid")
    private String email;
    @PositiveOrZero(message = "Games won should be greater or equals than 0")
    private long gamesWon;
    @PositiveOrZero(message = "Games lost should be greater or equals than 0")
    private long gamesLost;
    @PositiveOrZero(message = "Games won against ai should be greater or equals than 0")
    private long gamesWonAgainstAi;
    @PositiveOrZero(message = "Games played should be greater or equals than 0")
    private long gamesPlayed;
    @PositiveOrZero( message = "Loyalty points should be greater or equals than 0")
    private long loyaltyPoints;
    @NotNull(message = "Profile picture is required")
    private String profilePicture;
    private String title;
    private int aiDifficulty;

    /**
     * class constructor
     *
     * @param id the id of the player
     * @param username the username of the player
     * @param email the email of the player
     * @param profilePicture the profile picture of the player
     */
    public PlayerDto(long id, String username, String email, String profilePicture) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.profilePicture = profilePicture;
    }
}
