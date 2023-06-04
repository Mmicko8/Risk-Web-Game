package kdg.be.riskbackend.game.api_controllers;

import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.domain.game.Phase;
import kdg.be.riskbackend.game.domain.game.PlayerInGame;
import kdg.be.riskbackend.game.dtos.card.ExchangeCardsDto;
import kdg.be.riskbackend.game.dtos.game.GameDto;
import kdg.be.riskbackend.game.dtos.game.GameInfoDto;
import kdg.be.riskbackend.game.dtos.game.GameWithoutContinentsDto;
import kdg.be.riskbackend.game.dtos.phases.AttackDto;
import kdg.be.riskbackend.game.dtos.phases.AttackResponseDto;
import kdg.be.riskbackend.game.dtos.phases.FortifyDto;
import kdg.be.riskbackend.game.dtos.player_in_game.PlayerInGameWithoutCardsDto;
import kdg.be.riskbackend.game.services.GameService;
import kdg.be.riskbackend.lobby.domain.Lobby;
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

import static org.springframework.http.HttpStatus.*;

/**
 * controller for the games
 */
@RestController
@Slf4j
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000","http://localhost","http://127.0.0.1",
        "http://frontend-service", "http://backend-service", "http://ai-service", "http://www.risk.gq",
        "https://localhost:3000", "https://localhost:5000","https://localhost","https://127.0.0.1",
        "https://frontend-service", "https://backend-service", "https://ai-service", "https://www.risk.gq"})
@RequestMapping(path = "/api/game")
public class GameApiController {
    private final GameService gameService;
    private final LobbyService lobbyService;
    private final ModelMapper modelMapper;

    /**
     * Start a new game with the given lobbyId
     *
     * @param lobbyId the id of the lobby
     * @return the updated game
     */
    @PostMapping("/startGame/lobby/{lobbyId}")
    public ResponseEntity<?> startGame(@PathVariable long lobbyId) {
        try {
            Lobby lobby = lobbyService.getLobbyByIdWithPlayers(lobbyId);
            Game game = gameService.startGame(lobby);
            log.info("Game started with lobbyId: " + lobbyId + " and game has id: " + game.getGameId());
            return new ResponseEntity<>(game.getGameId(), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Error while starting game: " + e.getMessage());
            throw new ResponseStatusException(CONFLICT, e.getMessage());
        }
    }

    /**
     * Get the current game state
     *
     * @param gameId the id of the game
     * @return the current situation of the game
     */
    @GetMapping("{gameId}")
    public ResponseEntity<GameDto> getGameState(@PathVariable long gameId) {
        try {
            Game game = gameService.getGameState(gameId);
            log.info("Game state of game with id: " + game.getGameId() + " requested");
            return new ResponseEntity<>(modelMapper.map(game, GameDto.class), HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error while getting game state: " + e.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Get all active games of current player
     *
     * @return all games where the player is in
     */
    @GetMapping("/activeOfPlayer")
    public ResponseEntity<List<GameInfoDto>> getActiveGamesFromPlayer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            var games = gameService.getActiveGamesWithUsernamesFromUser(authentication.getName());
            var gameDtos = games.stream().map(game -> modelMapper.map(game, GameInfoDto.class)).toList();
            log.info("All active games of player: " + authentication.getName() + " requested");
            return new ResponseEntity<>(gameDtos, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.warn("No active games found for user: " + e.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * go to the next player
     *
     * @param gameId the id of the game
     * @return the next player
     */
    @PutMapping("/{gameId}/nextTurn")
    public ResponseEntity<PlayerInGameWithoutCardsDto> nextTurn(@PathVariable long gameId) {
        try {
            PlayerInGame playerInGame = gameService.getNextTurnPlayerOfGame(gameId);
            log.info("Next turn of player: " + playerInGame.getPlayer().getUsername());
            return new ResponseEntity<>(modelMapper.map(playerInGame, PlayerInGameWithoutCardsDto.class), HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error while getting next turn: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    /**
     * attacks a territory of a specific game
     *
     * @param attackDto all the information needed to attack a territory
     * @return the updated attack information
     */
    @PutMapping("/attack")
    public ResponseEntity<AttackResponseDto> attack(@Valid @RequestBody AttackDto attackDto) {
        try {
            var attackResponse = gameService.attack(attackDto);
            log.info(String.format("A player did an attack from %s with %d on %s",
                    attackDto.getAttackerTerritoryName(), attackDto.getAmountOfAttackers(),
                    attackDto.getDefenderTerritoryName()));
            return new ResponseEntity<>(modelMapper.map(attackResponse, AttackResponseDto.class), HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error while attacking: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Sets the game into the next phase
     *
     * @param gameId the id of the game
     * @return the next phase of the game
     */
    @PutMapping("{gameId}/nextPhase")
    public ResponseEntity<?> nextPhase(@PathVariable long gameId) {
        try {
            Phase phase = gameService.nextPhase(gameId);
            log.info("Game with id " + gameId + " went to phase " + phase.name());
            return new ResponseEntity<>(phase, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.info("Game with id " + gameId + " could not go to the next phase");
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    /**
     * moves armies to one territory to another (that you own)
     *
     * @param fortifyDto all the information needed to fortify a territory
     * @return HttpStatus
     */
    @PutMapping("/fortify")
    public ResponseEntity<?> fortify(@Valid @RequestBody FortifyDto fortifyDto) {
        try {
            gameService.fortify(fortifyDto);
            log.info("Player fortified from " + fortifyDto.getTerritoryFrom() + " to " + fortifyDto.getTerritoryTo() + " with " + fortifyDto.getTroops() + " troops");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            log.info("Error while fortifying from " + fortifyDto.getTerritoryFrom() + " to " + fortifyDto.getTerritoryTo() + " with " + fortifyDto.getTroops() + " troops");
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Get the history of games of a player
     *
     * @return all games where the player has played in
     */
    @GetMapping("/history")
    public ResponseEntity<List<GameWithoutContinentsDto>> gameHistory() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<Game> games = gameService.historyOfPlayer(authentication.getName());
            if (games.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            var gamesDto = games.stream().map((g) -> modelMapper.map(g, GameWithoutContinentsDto.class)).toList();
            log.info("Game history of player: " + authentication.getName() + " requested");
            return new ResponseEntity<>(gamesDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error while getting history: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    /**
     * exchange cards for armies
     *
     * @param exchangeCardsDto the cards to exchange
     */
    @PutMapping("/exchangeCards")
    public ResponseEntity<?> exchangeCards(@Valid @RequestBody ExchangeCardsDto exchangeCardsDto) {
        try {
            gameService.handleExchangeCards(exchangeCardsDto);
            log.info("Get exchange cards from game with id " + exchangeCardsDto.getGameId());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            log.error("Error while exchanging cards: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    //API calls for ISB

    /**
     * Gets the amount of active games
     *
     * @return the amount of active games
     */
    @GetMapping("/monitoring/amountActive")
    public ResponseEntity<Integer> getAmountActiveGames() {
        try {
            var amountOfActiveGames = gameService.getAmountActiveGames();
            log.info("Amount of active games requested");
            return new ResponseEntity<>(amountOfActiveGames, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error while getting amount of active games: " + e.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Gets the amount of finished games
     *
     * @return the amount of finished games
     */
    @GetMapping("/monitoring/amountFinished")
    public ResponseEntity<Integer> getAmountFinishedGames() {
        try {
            var amountOfFinishedGames = gameService.getAmountFinishedGames();
            log.info("Get amount of finished games");
            return new ResponseEntity<>(amountOfFinishedGames, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error while getting amount of finished games: " + e.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }
}
