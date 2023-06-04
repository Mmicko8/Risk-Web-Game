package kdg.be.riskbackend.shop.domain;

import kdg.be.riskbackend.identity.domain.user.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * This class represents a shop item.
 * A shop item can be bought by a player.
 * A shop item can be a profile picture or a game item.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "ShopItems")
public class ShopItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long shopItemId;
    @NotNull(message = "Item name is required")
    private String name;
    @NotNull(message = "A item category must be specified")
    private ItemCategory itemCategory;
    @Positive(message = "Price must be positive")
    private int price;
    @ManyToMany(mappedBy = "shopItems", fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<Player> players;

    /**
     * class constructor
     *
     * @param name          the name of the shop item
     * @param itemCategory  the category of the shop item
     * @param price         the price of the shop item
     */
    public ShopItem(String name, ItemCategory itemCategory, int price) {
        this.name = name;
        this.itemCategory = itemCategory;
        this.price = price;
    }
}
