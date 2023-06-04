package kdg.be.riskbackend.lobby.services;

import kdg.be.riskbackend.lobby.domain.Invite;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.exceptions.InvalidIdException;
import kdg.be.riskbackend.lobby.repositories.InviteRepository;
import kdg.be.riskbackend.lobby.repositories.LobbyRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InviteServiceTest {
    Lobby lobby;
    Invite invite;
    @Autowired
    private InviteService inviteService;
    @Autowired
    private InviteRepository inviteRepository;
    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private LobbyRepository lobbyRepository;


    @BeforeEach
    void setUp() {
        lobby = lobbyService.startLobby(new CreateLobbyDto("KdgUser1", 3, 60));
        invite = inviteService.createInviteByUsername("KdgUser3", lobby.getLobbyId(), "KdgUser1");
    }

    @AfterEach
    void clean() {
        inviteRepository.deleteAll();
        lobbyRepository.delete(lobby);
    }

    @Test
    @WithMockUser(username = "kdgUser1")
    void createInviteWithUsernameWorksIfPlayerAndGameExist() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        invite = inviteService.createInviteByUsername("KdgUser2", lobby.getLobbyId(), authentication.getName());
        Assertions.assertNotNull(invite);
        Assertions.assertEquals("KdgUser2", invite.getPlayer().getUsername());
        Assertions.assertEquals(lobby.getLobbyId(), invite.getLobby().getLobbyId());
    }


    @Test
    @WithMockUser(username = "kdgUser1")
    void createInviteByUsernameFailsIfLobbyDoesntExist() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Assertions.assertThrows(InvalidIdException.class, () -> inviteService.createInviteByUsername("KdgUser2", 0, authentication.getName()));
    }


    @Test
    @WithMockUser(username = "kdgUser3")
    void decliningInviteWorks() {
        inviteService.declineInvite(invite.getId());
        Assertions.assertEquals(1, lobby.getPlayers().size());
        Assertions.assertNull(inviteRepository.findById(invite.getId()).orElse(null));
    }

    @Test
    @WithMockUser(username = "KdgUser3")
    void gettingAllOpenInvitesForPlayerWorks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var invites = inviteService.getAllInvitesByUsername(authentication.getName());
        Assertions.assertEquals(1, invites.size());
        Assertions.assertEquals(invite.getId(), invites.get(0).getId());
        Assertions.assertEquals(invite.getPlayer().getUsername(), invites.get(0).getPlayer().getUsername());
        Assertions.assertEquals(invite.getLobby().getLobbyId(), invites.get(0).getLobby().getLobbyId());
    }

}