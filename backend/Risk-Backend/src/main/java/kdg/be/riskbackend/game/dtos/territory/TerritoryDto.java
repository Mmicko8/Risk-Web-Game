package kdg.be.riskbackend.game.dtos.territory;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TerritoryDto {
    private long territoryId;
    private String name;
    private long ownerId;
    private int troops;
}

