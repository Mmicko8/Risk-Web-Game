package kdg.be.riskbackend.identity.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EquipShopItemDto {
    private long shopItemId;
    private long playerId;
}
