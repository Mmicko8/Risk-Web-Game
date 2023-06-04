package kdg.be.riskbackend.game.dtos.phases;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AttackDto {
    @Positive(message = "A valid game must be specified")
    private long gameId;
    @NotNull(message = "A valid territory must be specified")
    private String attackerTerritoryName;
    @NotNull(message = "A valid territory must be specified")
    private String defenderTerritoryName;
    @Range(min = 1, max = 3, message = "The amount of dice must be between 1 and 3")
    private int amountOfAttackers;
}
