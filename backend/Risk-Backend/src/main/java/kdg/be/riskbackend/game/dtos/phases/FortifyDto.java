package kdg.be.riskbackend.game.dtos.phases;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FortifyDto {
    @Positive(message = "A valid game must be specified")
    private long gameId;
    @NotNull(message = "A valid territory from must be specified")
    private String territoryFrom;
    @NotNull(message = "A valid territory to must be specified")
    private String territoryTo;
    @Positive(message = "Cant fortify with negative amount")
    private int troops;


}
