package kdg.be.riskbackend.lobby.api_controllers;

import kdg.be.riskbackend.identity.exceptions.InvalidPlayerException;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.dtos.LobbyDto;
import kdg.be.riskbackend.lobby.exceptions.InvalidIdException;
import kdg.be.riskbackend.lobby.services.LobbyService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 *
 */
@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000","http://localhost","http://127.0.0.1",
        "http://frontend-service", "http://backend-service", "http://ai-service", "http://www.risk.gq",
        "https://localhost:3000", "https://localhost:5000","https://localhost","https://127.0.0.1",
        "https://frontend-service", "https://backend-service", "https://ai-service", "https://www.risk.gq"})
@RequestMapping(path = "/api/lobby")
public class LobbyApiController {
    private final LobbyService lobbyService;
    private final ModelMapper modelMapper;

    /**
     * gets a lobby by id
     *
     * @param lobbyId the id of the lobby
     * @return LobbyDto
     */
    @GetMapping("/{lobbyId}")
    public ResponseEntity<?> getLobby(@PathVariable long lobbyId) {
        try {
            var lobby = lobbyService.getLobbyById(lobbyId);
            var lobbyDto = modelMapper.map(lobby, LobbyDto.class);
            log.info("Lobby found and provided");
            return new ResponseEntity<>(lobbyDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error while finding lobby with id " + lobbyId + ": " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Create a new lobby
     *
     * @param createLobbyMode the lobby to create
     * @return HttpStatus
     */
    @PostMapping("/create")
    public ResponseEntity<?> createLobby(@Valid @RequestBody CreateLobbyDto createLobbyMode) {
        try {
            var lobby = lobbyService.startLobby(createLobbyMode);
            log.info("Lobby created");
            return new ResponseEntity<>(lobby.getLobbyId(), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Error while creating lobby: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * adds an AI to the game
     *
     * @param lobbyId the id of the lobby
     * @return HttpStatus
     */
    @PutMapping("{lobbyId}/addAi")
    public ResponseEntity<?> addAiToGame(@PathVariable long lobbyId) {
        try {
            lobbyService.addRandomAiPlayer(lobbyId);
            log.info("Player added to lobby");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            log.error("Error while adding an ai to game: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    /**
     * gets a maximum of 10 open lobbies from the database
     *
     * @param amount the amount of lobbies to get
     * @return List of LobbyDto
     */
    @GetMapping("/openLobbies/{amount}")
    public ResponseEntity<List<LobbyDto>> getOpenLobbies(@PathVariable int amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            List<Lobby> openLobbies;
            if (!authentication.getName().equals("anonymousUser")) {
                openLobbies = lobbyService.getOpenLobbiesNotJoinedByUser(amount, authentication.getName());
            } else {
                openLobbies = lobbyService.getOpenLobbies(amount);
            }
            log.info("Getting open lobbies");
            var lobbiesDto = openLobbies.stream().map((lobby) -> modelMapper.map(lobby, LobbyDto.class)).toList();
            return new ResponseEntity<>(lobbiesDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error while getting open lobbies: " + e.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * gets the lobbies that are joined by the current player but not started yet
     *
     * @return List of LobbyDto
     */
    @GetMapping("/joinedNotStartedLobbies")
    public ResponseEntity<List<LobbyDto>> getJoinedNotStartedLobbies() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            var joinedLobbies = lobbyService.getJoinedNotStartedLobbies(authentication.getName());
            var joinedLobbiesDto = joinedLobbies.stream().map(lobby -> modelMapper.map(lobby, LobbyDto.class)).toList();
            log.info("Joined lobbies found and provided");
            return new ResponseEntity<>(joinedLobbiesDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.warn("Error while trying to join lobby: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Gets the amount of open lobbies
     *
     * @return the amount of open lobbies
     */
    @GetMapping("/monitoring/amountOpen")
    public ResponseEntity<Integer> getAmountOpenLobbies() {

        var amount = lobbyService.getAmountOpenLobbies();
        log.info("Amount of open lobbies provided");
        return new ResponseEntity<>(amount, HttpStatus.OK);

    }

    /**
     * Gets the amount of closed lobbies
     *
     * @return the amount of closed lobbies
     */
    @GetMapping("/monitoring/amountClosed")
    public ResponseEntity<Integer> getAmountClosedLobbies() {
        var amount = lobbyService.getAmountClosedLobbies();
        log.info("Amount of closed lobbies provided");
        return new ResponseEntity<>(amount, HttpStatus.OK);
    }

    /**
     * joins a lobby by id
     *
     * @param lobbyId the id of the lobby
     * @return the amount of running lobbies
     */
    @PutMapping("joinLobby/{lobbyId}")
    public ResponseEntity<?> joinLobby(@PathVariable long lobbyId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            lobbyService.joinLobby(lobbyId, authentication.getName());
            log.info("Lobby joined");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (InvalidIdException | InvalidPlayerException e) {
            log.error("Error while trying to join lobby: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        } catch (RuntimeException e) {
            log.error("Error while trying to join lobby: " + e.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }
}
