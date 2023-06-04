package kdg.be.riskbackend.shop.service;

import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.shop.domain.ShopItem;
import kdg.be.riskbackend.shop.exceptions.ShopException;
import kdg.be.riskbackend.shop.repository.ShopItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

/**
 * This class represents the service for the shop.
 * It contains the business logic for the shop.
 */
@Service
@AllArgsConstructor
public class ShopItemService {
    PlayerService playerService;
    ShopItemRepository shopItemRepository;

    /**
     * add a shop item to the rest your inventory
     *
     * @param username   the username of the user who wants to buy the item
     * @param shopItemId the id of the item you want to add
     */
    @Transactional
    public void buyItem(String username, long shopItemId) {
        Player player = playerService.findByUsernameWithShopItems(username);
        ShopItem shopItem = shopItemRepository.findById(shopItemId).orElseThrow(() -> new ShopException("Shop item not found"));
        var shopItems = player.getShopItems();
        if (shopItems.stream().noneMatch(f -> f.getShopItemId() == shopItem.getShopItemId())) {
            shopItems.add(shopItem);
        } else {
            throw new ShopException("Item is already in your inventory");
        }
        if (player.getLoyaltyPoints() < shopItem.getPrice()) {
            throw new ShopException("You don't have enough loyalty points to buy this item");
        }

        player.setLoyaltyPoints(player.getLoyaltyPoints() - shopItem.getPrice());
        player.setShopItems(shopItems);
        playerService.save(player);
    }

    /**
     * create a new shop item
     *
     * @param shopItem the item you want to create
     */
    public ShopItem saveShopItem(@Valid ShopItem shopItem) {
        return shopItemRepository.save(shopItem);
    }

    /**
     * Returns all shop items the given user doesn't own already.
     *
     * @param username username of the player
     * @return a list of shop items
     */
    public List<ShopItem> getItemsForPlayer(String username) {
        //check if player exists
        playerService.loadUserByUsername(username);
        return shopItemRepository.findItemsForPlayer(username);
    }

}
