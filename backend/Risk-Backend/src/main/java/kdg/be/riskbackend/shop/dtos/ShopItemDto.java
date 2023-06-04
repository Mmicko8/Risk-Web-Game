package kdg.be.riskbackend.shop.dtos;

import kdg.be.riskbackend.shop.domain.ItemCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShopItemDto {
    private long shopItemId;
    private String name;
    private ItemCategory itemCategory;
    private int price;
}
