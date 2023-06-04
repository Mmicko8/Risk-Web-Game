package kdg.be.riskbackend.identity.api_controllers;

import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.dtos.PlayerDto;
import kdg.be.riskbackend.identity.services.FriendService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * controller for the friends
 */
@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000","http://localhost","http://127.0.0.1",
        "http://frontend-service", "http://backend-service", "http://ai-service", "http://www.risk.gq",
        "https://localhost:3000", "https://localhost:5000","https://localhost","https://127.0.0.1",
        "https://frontend-service", "https://backend-service", "https://ai-service", "https://www.risk.gq"})
@RequestMapping(path = "/api/friend")
public class FriendController {
    private final FriendService friendService;
    private final ModelMapper modelMapper;

    /**
     * sends a friend request
     *
     * @return HttpStatus
     */
    @PutMapping("/send/{friendUsername}")
    public ResponseEntity<?> sendFriendRequest(@PathVariable String friendUsername) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            friendService.sendFriendRequest(authentication.getName(), friendUsername);
            log.info(authentication.getName() + " sent a friend request to " + friendUsername);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            log.error("Error while sending friend request: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    /**
     * accepts a new friend request
     *
     * @param friendUsername the username of the sending player
     * @return HttpStatus
     */
    @PutMapping("/accept/{friendUsername}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable String friendUsername) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            friendService.acceptFriendRequest(authentication.getName(), friendUsername);
            log.info(authentication.getName() + " and " + friendUsername + " are friends now");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            log.error("Error while adding friend: " + e.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * declines a friend request
     *
     * @param friendUsername the username of the sending player
     * @return HttpStatus
     */
    @PutMapping("/decline/{friendUsername}")
    public ResponseEntity<?> declineFriendRequest(@PathVariable String friendUsername) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            friendService.declineFriend(authentication.getName(), friendUsername);
            log.info(authentication.getName() + " declined the friend request from " + friendUsername);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            log.error("Error while declining friend: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    /**
     * get all friend requests
     *
     * @return friend requests
     */
    @GetMapping("/requests")
    public ResponseEntity<?> getFriendRequests() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            var friendRequests = friendService.getFriendRequestsOfPlayer(authentication.getName());
            log.info("Friend requests of "+authentication.getName()+ " are returned");
            return new ResponseEntity<>(friendRequests.stream().map(fr -> modelMapper.map(fr, PlayerDto.class)), HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error while adding friend: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    /**
     * gets all friends of a player
     *
     * @return all friends of the player
     */
    @GetMapping
    public ResponseEntity<List<PlayerDto>> getAllFriends() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<Player> friends = friendService.getFriends(authentication.getName());
            List<PlayerDto> friendsDto = new ArrayList<>();
            friends.forEach(friend -> friendsDto.add(modelMapper.map(friend, PlayerDto.class)));
            log.info("Friends of " + authentication.getName() + " are returned");
            return new ResponseEntity<>(friendsDto, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error while getting friends: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }

    /**
     * deletes a friend
     *
     * @param username the username of the player
     * @return HttpStatus
     */
    @DeleteMapping("/remove/{username}")
    public ResponseEntity<?> removeFriend(@PathVariable String username) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            friendService.removeFriend(authentication.getName(), username);
            log.info(authentication.getName() + " removed " + username + " from his friends");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            log.error("Error while removing friend: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }
}
