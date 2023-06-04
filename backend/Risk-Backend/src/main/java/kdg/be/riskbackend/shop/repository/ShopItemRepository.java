package kdg.be.riskbackend.shop.repository;


import kdg.be.riskbackend.shop.domain.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {
    /**
     * finds a shop item by its name
     *
     * @param name the name of the shop item
     * @return the shop item
     */
    ShopItem findByName(String name);

    /**
     * finds an item for a player
     *
     * @param username the name the player
     * @return the shop item
     */
    @Query("SELECT s FROM ShopItems s where (SELECT p from players p where p.username = ?1) not in elements(s.players)")
    List<ShopItem> findItemsForPlayer(String username);
}
