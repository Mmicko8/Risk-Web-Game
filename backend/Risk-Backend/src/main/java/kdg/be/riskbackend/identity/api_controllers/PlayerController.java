package kdg.be.riskbackend.identity.api_controllers;

import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.dtos.EditPlayerUsernameDto;
import kdg.be.riskbackend.identity.dtos.EquipShopItemDto;
import kdg.be.riskbackend.identity.dtos.PlayerDto;
import kdg.be.riskbackend.identity.dtos.PlayerProfileDto;
import kdg.be.riskbackend.identity.dtos.login.LoginRequest;
import kdg.be.riskbackend.identity.dtos.login.PlayerView;
import kdg.be.riskbackend.identity.dtos.registration.RegistrationRequest;
import kdg.be.riskbackend.identity.security.filter.JwtTokenUtil;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.identity.services.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * controller for the players
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000","http://localhost","http://127.0.0.1",
        "http://frontend-service", "http://backend-service", "http://ai-service", "http://www.risk.gq",
        "https://localhost:3000", "https://localhost:5000","https://localhost","https://127.0.0.1",
        "https://frontend-service", "https://backend-service", "https://ai-service", "https://www.risk.gq"}
        , exposedHeaders = HttpHeaders.AUTHORIZATION)
@RequestMapping(path = "/api/player")
public class PlayerController {
    private final JwtTokenUtil jwtTokenUtil;
    private final RegistrationService registrationService;
    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final PlayerService playerService;
    @Value("${ui.url}")
    private String uiUrl;

    /**
     * This method is used for logging in a user.
     *
     * @param loginRequest the information needed to log in
     * @return ResponseEntity with a PlayerView and a JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<PlayerView> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    loginRequest.getUsername(), loginRequest.getPassword()
                            )
                    );

            Player player = (Player) authenticate.getPrincipal();
            playerService.setSsid(player.getUsername());
            log.info("Player logged in: " + player.getUsername());
            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            jwtTokenUtil.generateAccessToken(player)
                    )
                    .body(modelMapper.map(player, PlayerView.class));
        } catch (BadCredentialsException ex) {
            log.error("Error while logging in: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * This method is used for logging out
     *
     * @return HttpStatus
     */
    @PutMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            playerService.logout(authentication.getName());
            log.info("Player logged out: " + authentication.getName());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            log.error("Error while logging out: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * This method is used for registering a new user.
     *
     * @param request the information needed to register a new user
     * @return token
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequest request) {
        var token = registrationService.register(request);
        log.info("Player registered: " + request.getUsername());
        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }

    /**
     * This method is used for activating a user's account.
     *
     * @param token the token needed to activate the account
     * @return token
     */
    @GetMapping("/confirm/{token}")
    public ResponseEntity<?> confirm(@PathVariable String token) {
        try {
            registrationService.confirmToken(token);
            String link = uiUrl + "/sign_in";
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(link));
            log.info("User activated");
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        } catch (RuntimeException e) {
            log.error("Error while activating account: " + e.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * This method is used for editing a user's account.
     *
     * @param playerDto the username, password and id of the user
     * @return HttpStatus
     */
    @PutMapping("/edit")
    public ResponseEntity<?> editPlayer(@Valid @RequestBody PlayerDto playerDto) {
        try {
            playerService.editPlayer(playerDto);
            log.info("User edited");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Error while editing account: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Updates the username of the player.
     * @param playerDto contains the id and the new username for the player
     * @return HttpStatus
     */
    @PutMapping("/edit/username")
    public ResponseEntity<?> editPlayerUsername(@RequestBody EditPlayerUsernameDto playerDto) {
        try {
            playerService.editPlayerUsername(playerDto);
            log.info("User changed his username");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Error while editing username: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    /**
     * Equips the given shop item. (e.g. sets the profile picture)
     * @param equipShopItemDto contains the player id and the shop item id
     * @return HttpStatus
     */
    @PutMapping("/equip")
    public ResponseEntity<?> equipShopItem(@RequestBody EquipShopItemDto equipShopItemDto) {
        try {
            playerService.equipShopItem(equipShopItemDto);
            log.info("User equiped Shop Item");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Error while equiping shop item: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    /**
     * This method is used getting the leaderboard
     *
     * @return a list of player information
     */
    @GetMapping("/leaderboard")
    public ResponseEntity<List<PlayerDto>> getLeaderboard() {
        var players = playerService.getLeaderboard();
        log.info("Leaderboard requested");
        return new ResponseEntity<>(players.stream().map(player -> modelMapper.map(player, PlayerDto.class)).collect(Collectors.toList()), HttpStatus.OK);
    }

    /**
     * This method is used for getting players that you recently played with
     *
     * @return a list of player information of players you recently played with
     */
    @GetMapping("/recentlyPlayedWith")
    public ResponseEntity<List<PlayerDto>> getRecentlyPlayedWith() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Player> players = playerService.getRecentlyPlayedWith(authentication.getName());
        var playerDtos = players.stream().map(player -> modelMapper.map(player, PlayerDto.class)).collect(Collectors.toList());
        log.info("Recently played with players are returned");
        return new ResponseEntity<>(playerDtos, HttpStatus.OK);
    }

    /**
     * This method is used for getting all the local players
     *
     * @return all the local players
     */
    @GetMapping("/getLocalPlayers")
    public ResponseEntity<List<PlayerDto>> getAllLocalPlayers() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<Player> friends = playerService.getAllLocalPlayers(authentication.getName());
            List<PlayerDto> localPlayersDto = new ArrayList<>();
            friends.forEach(friend -> localPlayersDto.add(modelMapper.map(friend, PlayerDto.class)));
            log.info("Local players are returned");
            return new ResponseEntity<>(localPlayersDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error while getting friends: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    /**
     * This method is used for getting a player's profile information
     *
     * @return the player's profile information
     */
    @GetMapping("/profile")
    public ResponseEntity<PlayerProfileDto> getPlayerProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            var player = playerService.getPlayerProfile(authentication.getName());
            log.info("Player " + authentication.getName() + " requested profile info");
            return new ResponseEntity<>(modelMapper.map(player, PlayerProfileDto.class), HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error requesting player profile: " + e.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }
}
