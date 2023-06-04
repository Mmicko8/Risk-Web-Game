package kdg.be.riskbackend.game.api_controllers;

import kdg.be.riskbackend.game.dtos.territory.NeighborDto;
import kdg.be.riskbackend.game.services.NeighborService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * controller for the neighbors of a territory
 */
@RestController
@Slf4j
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000","http://localhost","http://127.0.0.1",
        "http://frontend-service", "http://backend-service", "http://ai-service", "http://www.risk.gq",
        "https://localhost:3000", "https://localhost:5000","https://localhost","https://127.0.0.1",
        "https://frontend-service", "https://backend-service", "https://ai-service", "https://www.risk.gq"})
@RequestMapping(path = "/api/neighbour")
public class NeighborApiController {
    private final NeighborService neighborService;
    private final ModelMapper modelMapper;

    /*
     * Get all territories that are neighbours of a territory
     * @param territoryId the id of the territory
     */
    @GetMapping("/territory/{territoryId}")
    public ResponseEntity<List<NeighborDto>> getAllNeighborsOfTerritory(@PathVariable long territoryId) {
        try {
            var neighbors = neighborService.getAllNeighborsOfTerritory(territoryId);
            var neighborsDto = neighbors.stream().map(neighbor -> modelMapper.map(neighbor, NeighborDto.class)).toList();
            log.info("neighbors of territory with id " + territoryId + " requested");
            return new ResponseEntity<>(neighborsDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error while getting neighbors of territory: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }
}
