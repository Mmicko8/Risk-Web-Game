package kdg.be.riskbackend.lobby.api_controllers;

import kdg.be.riskbackend.identity.exceptions.PlayerException;
import kdg.be.riskbackend.lobby.dtos.InviteDto;
import kdg.be.riskbackend.lobby.dtos.InviteWithEmail;
import kdg.be.riskbackend.lobby.dtos.InviteWithUsername;
import kdg.be.riskbackend.lobby.exceptions.FullLobbyException;
import kdg.be.riskbackend.lobby.exceptions.LobbyClosedException;
import kdg.be.riskbackend.lobby.services.InviteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000","http://localhost","http://127.0.0.1",
        "http://frontend-service", "http://backend-service", "http://ai-service", "http://www.risk.gq",
        "https://localhost:3000", "https://localhost:5000","https://localhost","https://127.0.0.1",
        "https://frontend-service", "https://backend-service", "https://ai-service", "https://www.risk.gq"})
@RequestMapping(path = "/api/invite")
public class InviteApiController {
    @Autowired
    private InviteService inviteService;
    @Value("${ui.url}")
    private String uiUrl;

    /**
     * this method is for creating an invitation with username
     *
     * @param inviteWithUsername the username and lobbyId
     * @return InviteDto
     */
    @PostMapping("/emailInviteWithUsername")
    public ResponseEntity<InviteDto> createAndMailInviteWithUsername(@Valid @RequestBody InviteWithUsername inviteWithUsername) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            inviteService.createAndMailInviteWithUsername(inviteWithUsername.getUsername(), inviteWithUsername.getLobbyId(),
                    authentication.getName());
            log.info("Invite sent to: " + inviteWithUsername.getUsername());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Error while creating invite: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * this method is for creating an invitation with email
     *
     * @param inviteWithEmail the email and lobbyId
     * @return InviteDto
     */
    @PostMapping("/emailInviteWithEmail")
    public ResponseEntity<InviteDto> createAndMailInviteWithEmail(@Valid @RequestBody InviteWithEmail inviteWithEmail) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            inviteService.mailInviteWithEmail(inviteWithEmail.getEmail(), inviteWithEmail.getLobbyId(), authentication.getName());
            log.info("Invite sent to: " + inviteWithEmail.getEmail());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            log.error("Error while creating invite: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * this method is for accepting an invitation
     *
     * @param lobbyId id for the lobby
     * @param recipient name or email from the receiving player
     * @param senderName username from the player that sent the invite
     */
//    @GetMapping("/accept?lobbyId={lobbyId}&recipient={recipient}&senderName={senderName}")
    @GetMapping("/accept/{lobbyId}/recipient/{recipient}/sender/{senderName}")
    public ResponseEntity<?> acceptInviteFromEmail(@PathVariable long lobbyId,
                                                   @PathVariable String recipient,
                                                   @PathVariable String senderName) {
        try {
            log.info("Entered acceptEndpoint");
            inviteService.acceptInvite(lobbyId, recipient, senderName);
            log.info("Invite accepted");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (UsernameNotFoundException e) {
            log.warn("No existing account for the user was found: " + e.getMessage());
            String link = uiUrl + "/register";
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(link));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        } catch (FullLobbyException e) {
            log.warn("Something went wrong accepting the invite: "+e.getMessage());
            String link = uiUrl + "/lobbyFull";
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(link));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        } catch (LobbyClosedException | PlayerException e) {
            log.warn("Something went wrong accepting the invite: "+e.getMessage());
            String link = uiUrl + "/lobby/"+lobbyId;
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(link));
            return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        }
    }

    /*
     * this method is for refusing an invitation
     *
     * @param inviteId
     */
    @DeleteMapping("{inviteId}/decline")
    public ResponseEntity<?> declineInvite(@PathVariable long inviteId) {
        try {
            inviteService.declineInvite(inviteId);
            log.info("Invite declined");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            log.error("Error while declining invite: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /*
     * this method is for getting all invites of current player
     * gets the name out of the authentication
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllMyInvites() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            var invites = inviteService.getAllInvitesByUsername(authentication.getName());
            log.info("Invite declined");
            return new ResponseEntity<>(invites, HttpStatus.OK);
        } catch (RuntimeException e) {
            log.error("Error while getting invites: " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
