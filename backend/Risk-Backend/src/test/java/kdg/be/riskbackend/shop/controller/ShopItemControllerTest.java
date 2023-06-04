package kdg.be.riskbackend.shop.controller;

import kdg.be.riskbackend.identity.exceptions.PlayerException;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.dtos.registration.RegistrationRequest;
import kdg.be.riskbackend.identity.repositories.ConfirmationTokenRepository;
import kdg.be.riskbackend.identity.repositories.PlayerRepository;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.identity.services.RegistrationService;
import kdg.be.riskbackend.shop.domain.ItemCategory;
import kdg.be.riskbackend.shop.domain.ShopItem;
import kdg.be.riskbackend.shop.repository.ShopItemRepository;
import kdg.be.riskbackend.shop.service.ShopItemService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShopItemControllerTest {
    @Autowired
    ShopItemService shopItemService;
    @Autowired
    ShopItemRepository shopItemRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    RegistrationService registrationService;
    @Autowired
    PlayerService playerService;
    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    ShopItem shopItem;
    Player player;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    void setUp() {
        shopItem = shopItemService.saveShopItem(new ShopItem("Monkey", ItemCategory.PROFILE_PICTURE, 20));
        registrationService.register(new RegistrationRequest("test", "test@gmail.com", "password", false));
        playerService.enableByUsername("test");
        player = playerRepository.findByUsername("test").orElseThrow(() -> new PlayerException("Player not found"));
    }

    @AfterAll
    void tearDown() {
        confirmationTokenRepository.deleteAll();
        playerRepository.delete(player);
        shopItemRepository.delete(shopItem);

    }

    @Test
    @WithMockUser(username = "test")
    void buyItem() throws Exception {
        player.setLoyaltyPoints(20);
        playerService.save(player);
        mockMvc.perform(post("/api/shopItem/buyItem/" + shopItem.getShopItemId())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }
    @Test
    @WithMockUser(username = "test")
    void buyItemFailsWithInvalidItem() throws Exception {
        player.setLoyaltyPoints(20);
        playerService.save(player);
        mockMvc.perform(post("/api/shopItem/buyItem/0")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "KdgUser1")
    void showItemsForPlayer() throws Exception {
        mockMvc.perform(get("/api/shopItem/forPlayer")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
    @Test
    @WithMockUser(username = "chingchong")
    void showItemsForPlayerFailsForUnknownPlayer() throws Exception {
        mockMvc.perform(get("/api/shopItem/forPlayer")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
    }
}