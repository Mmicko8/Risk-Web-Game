package kdg.be.riskbackend.shop.controller;

import kdg.be.riskbackend.shop.dtos.ShopItemDto;
import kdg.be.riskbackend.shop.service.ShopItemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RestController
@Slf4j
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000","http://localhost","http://127.0.0.1",
        "http://frontend-service", "http://backend-service", "http://ai-service", "http://www.risk.gq",
        "https://localhost:3000", "https://localhost:5000","https://localhost","https://127.0.0.1",
        "https://frontend-service", "https://backend-service", "https://ai-service", "https://www.risk.gq"})
@RequestMapping(path = "/api/shopItem")
public class ShopItemController {
    ShopItemService shopItemService;
    ModelMapper modelMapper;

    /**
     * buys an item from the shop
     *
     * @param shopItemId the id of the item
     * @return HttpStatus
     */
    @PostMapping("/buyItem/{shopItemId}")
    public ResponseEntity<?> buyItem(@PathVariable long shopItemId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            shopItemService.buyItem(authentication.getName(), shopItemId);
            log.info("Player " + authentication.getName() + " bought item with id" + shopItemId);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Error while buying an item: " + e.getMessage());
            throw new ResponseStatusException(CONFLICT, e.getMessage());
        }
    }

    /**
     * shows all items in the shop for the player
     *
     * @return List of ShopItemDto
     */
    @GetMapping("/forPlayer")
    public ResponseEntity<List<ShopItemDto>> showItemsForPlayer() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            var shopItems = shopItemService.getItemsForPlayer(authentication.getName());
            log.info("Player " + authentication.getName() + " requested shop items");
            return new ResponseEntity<>(shopItems.stream().map(shopItem -> modelMapper.map(shopItem, ShopItemDto.class)).collect(Collectors.toList()), HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error while buying an item: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }
}
