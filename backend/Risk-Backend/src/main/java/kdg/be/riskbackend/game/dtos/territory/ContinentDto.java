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
public class ContinentDto {
    private long continentId;
    private List<TerritoryDto> territories;
    private int bonusTroops;
    private String name;
}
