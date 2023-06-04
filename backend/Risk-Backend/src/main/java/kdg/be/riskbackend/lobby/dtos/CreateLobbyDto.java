package kdg.be.riskbackend.lobby.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
public class CreateLobbyDto {
    @NotNull(message = "A valid username must be specified")
    String username;
    @Range(min = 2, max = 6, message = "The amount of players must be between 2 and 6")
    int maxPlayers;
    @Min(value = 10, message = "A minimum timer of 10 is required")
    int timer;
}
