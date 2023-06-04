package kdg.be.riskbackend.shop.service;

import kdg.be.riskbackend.identity.exceptions.PlayerException;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.dtos.registration.RegistrationRequest;
import kdg.be.riskbackend.identity.repositories.ConfirmationTokenRepository;
import kdg.be.riskbackend.identity.repositories.PlayerRepository;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.identity.services.RegistrationService;
import kdg.be.riskbackend.shop.domain.ItemCategory;
import kdg.be.riskbackend.shop.domain.ShopItem;
import kdg.be.riskbackend.shop.exceptions.ShopException;
import kdg.be.riskbackend.shop.repository.ShopItemRepository;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShopItemServiceTest {
    ShopItem shopItem;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    RegistrationService registrationService;
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    Player player;
    @Autowired
    private ShopItemService shopItemService;
    @Autowired
    private ShopItemRepository shopItemRepository;
    @Autowired
    private PlayerService playerService;

    @BeforeEach
    void setUp() {
        shopItemService.saveShopItem(new ShopItem("CoolPicture", ItemCategory.PROFILE_PICTURE, 10));
        shopItem = shopItemRepository.findByName("CoolPicture");
        registrationService.register(new RegistrationRequest("test", "test@gmail.com", "password", false));
        playerService.enableByUsername("test");
        player = playerRepository.findByUsername("test").orElseThrow(() -> new PlayerException("Player not found"));
    }

    @AfterEach
    void tearDown() {
        confirmationTokenRepository.deleteAll();
        playerRepository.delete(player);
        shopItemRepository.delete(shopItem);

    }


    @Test
    void tryToAddShoppingItemWithNotEnoughLoyaltyPoints() {
        Assertions.assertThrows(ShopException.class, () -> shopItemService.buyItem("test", shopItem.getShopItemId()));
    }

    @Test
    void tryToAddShoppingItemWithEnoughLoyaltyPoints() {
        playerService.addLoyaltyPoints(player.getUsername(), 10);
        Assertions.assertDoesNotThrow(() -> shopItemService.buyItem(player.getUsername(), shopItem.getShopItemId()));
    }

    @Test
    void tryToBuyItemYouAlreadyOwn() {
        playerService.addLoyaltyPoints(player.getUsername(), 50);
        Assertions.assertDoesNotThrow(() -> shopItemService.buyItem(player.getUsername(), shopItem.getShopItemId()));
        Assertions.assertThrows(ShopException.class, () -> shopItemService.buyItem(player.getUsername(), shopItem.getShopItemId()));
    }

    @Test
    void getAllItemsExceptTheOnesYouOwn() {
        playerService.addLoyaltyPoints(player.getUsername(), 50);
        Assertions.assertDoesNotThrow(() -> shopItemService.buyItem(player.getUsername(), shopItem.getShopItemId()));
        var items = shopItemService.getItemsForPlayer(player.getUsername());
        Assertions.assertFalse(items.contains(shopItem));
    }


    @Test
    void createShopItem() {
        shopItemService.saveShopItem(new ShopItem("Minion", ItemCategory.PROFILE_PICTURE, 10));
        var shoppingItem = shopItemRepository.findByName("Minion");
        Assertions.assertNotNull(shoppingItem);
        Assertions.assertEquals("Minion", shoppingItem.getName());
        Assertions.assertEquals(ItemCategory.PROFILE_PICTURE, shoppingItem.getItemCategory());
        Assertions.assertEquals(10, shoppingItem.getPrice());
    }
}