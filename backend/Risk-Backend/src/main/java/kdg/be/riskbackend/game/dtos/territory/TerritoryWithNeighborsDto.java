package kdg.be.riskbackend.game.dtos.territory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TerritoryWithNeighborsDto {
    private long territoryId;
    private String name;
    private long ownerId;
    private int troops;
    private List<NeighborDto> neighbors;
}
