package kdg.be.riskbackend.game.api_controllers;

import kdg.be.riskbackend.game.dtos.territory.TerritoryWithNeighborsDto;
import kdg.be.riskbackend.game.services.TerritoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * controller for the territories
 */
@Slf4j
@AllArgsConstructor
@RestController
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000","http://localhost","http://127.0.0.1",
        "http://frontend-service", "http://backend-service", "http://ai-service", "http://www.risk.gq",
        "https://localhost:3000", "https://localhost:5000","https://localhost","https://127.0.0.1",
        "https://frontend-service", "https://backend-service", "https://ai-service", "https://www.risk.gq"})
@RequestMapping(path = "/api/territory")
public class TerritoryApiController {
    private final TerritoryService territoryService;
    private final ModelMapper modelMapper;

    /**
     * Add troops to a territory
     *
     * @param territoryId the id of the territory
     * @param troopAmount the amount of troops to add
     * @return the remaining troops the player has to place on a territory
     */
    @PutMapping("/{territoryId}/placeTroops/{troopAmount}")
    public ResponseEntity<Integer> placeTroops(@PathVariable long territoryId, @PathVariable int troopAmount) {
        try {
            var remainingTroops = territoryService.calculateRemainingTroops(territoryId, troopAmount);
            territoryService.placeTroops(territoryId, troopAmount);
            log.info(troopAmount + " troops placed on territory with id " + territoryId);
            return new ResponseEntity<>(remainingTroops, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error while placing troops: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Returns a list of all territories and their neighbors of the given game
     *
     * @param gameId the id of the game
     * @return a list of all territories and their neighbors of the given game
     */
    @GetMapping("/game/{gameId}/neighbors")
    public ResponseEntity<List<TerritoryWithNeighborsDto>> getAllTerritoriesWithNeighbors(@PathVariable long gameId) {
        try {
            var territories = territoryService.getAllTerritoriesOfGameWithNeighbors(gameId);
            log.info("territories with neighbors requested of game id: " + gameId);
            List<TerritoryWithNeighborsDto> territoryWithNeighborsDtos = territories
                    .stream()
                    .map(user -> modelMapper.map(user, TerritoryWithNeighborsDto.class))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(territoryWithNeighborsDtos, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Territories with neighbors not found: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }
}
