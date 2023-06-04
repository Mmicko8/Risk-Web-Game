package kdg.be.riskbackend.lobby.services;

import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.identity.services.email.EmailSender;
import kdg.be.riskbackend.identity.util.EmailBuilder;
import kdg.be.riskbackend.lobby.domain.Invite;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.repositories.InviteRepository;
import kdg.be.riskbackend.lobby.util.EmailCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

/**
 * this class is for the invite service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InviteService {
    private final InviteRepository inviteRepository;
    private final PlayerService playerService;
    private final LobbyService lobbyService;
    private final EmailSender emailSender;
    @Value("${backend.url}")
    private String backendUrl;

    /**
     * calls creation of new invite and mails it to the recipient
     *
     * @param lobbyId  the id of the lobby
     * @param recipientName the username of the receiving player
     */
    public void createAndMailInviteWithUsername(String recipientName, long lobbyId, String usernameSender) {
        var player = playerService.loadUserByUsername(recipientName);
        createInviteByUsername(recipientName, lobbyId, usernameSender);
        String link = backendUrl+"/api/invite/accept/"+lobbyId+"/recipient/"+recipientName+"/sender/"+usernameSender;
        try {
            emailSender.send(player.getEmail(), "Lobby Invitation",
                    EmailBuilder.buildLobbyInviteEmail(player.getUsername(), usernameSender, link));
        } catch (Exception e) {
            log.info("email not sent to " + e.getMessage());
        }
    }

    /**
     * mails invite link to user - does not create invite entity because a recipient without an account can also receive an invite
     *
     * @param lobbyId  the id of the lobby
     * @param recipientMail the email of the receiving player
     */
    public void mailInviteWithEmail(String recipientMail, long lobbyId, String usernameSender) {
        String link = backendUrl+"/api/invite/accept/"+lobbyId+"/recipient/"+recipientMail+"/sender/"+usernameSender;
        try {
            emailSender.send(recipientMail, "Lobby Invitation",
                    EmailBuilder.buildLobbyInviteEmail(recipientMail, usernameSender, link));
        } catch (Exception e) {
            log.info("email not sent to " + e.getMessage());
        }
    }

    /**
     * creates a new invite
     *
     * @param lobbyId  the id of the lobby
     * @param username the username of the player
     */
    public Invite createInviteByUsername(String username, long lobbyId, String usernameSender) {
        Player player = playerService.loadUserByUsername(username);
        Lobby lobby = lobbyService.getLobbyById(lobbyId);
        return createInvite(new Invite(lobby, player, usernameSender));
    }

    public Invite createInvite(@Valid Invite invite) {
        return inviteRepository.save(invite);
    }

    /**
     * accepts an invitation
     *
     * @param lobbyId the id of the lobby
     * @param recipient name or email from the receiving player
     * @param senderName username from the player sending the invite
     */
    @Transactional
    public void acceptInvite(long lobbyId, String recipient, String senderName) {
        if (EmailCheck.checkIfValidEmail(recipient)) {
            var player = playerService.loadUserByEmail(recipient);
            lobbyService.joinLobby(lobbyId, player.getUsername());
        } else {
            var player = playerService.loadUserByUsername(recipient);
            var lobby = lobbyService.joinLobby(lobbyId, player.getUsername());
            inviteRepository.deleteByUsernameSenderAndLobbyAndPlayer(senderName, lobby, player);
        }
    }

    /**
     * declines an invitation and deletes it
     *
     * @param inviteId the id of the invite
     */
    public void declineInvite(long inviteId) {
        inviteRepository.deleteById(inviteId);
    }

    /*
     * returns all invites of a player
     * @param username the username of the player
     */
    public List<Invite> getAllInvitesByUsername(String kdgUser) {
        //check if exists
        playerService.loadUserByUsername(kdgUser);
        return inviteRepository.findAllInvitesByUsername(kdgUser);
    }
}
