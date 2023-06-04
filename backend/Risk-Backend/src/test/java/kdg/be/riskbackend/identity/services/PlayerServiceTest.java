package kdg.be.riskbackend.identity.services;

import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.repositories.GameRepository;
import kdg.be.riskbackend.game.services.GameService;
import kdg.be.riskbackend.identity.domain.user.AppUserRole;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.dtos.PlayerDto;
import kdg.be.riskbackend.identity.dtos.registration.RegistrationRequest;
import kdg.be.riskbackend.identity.repositories.PlayerRepository;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.services.LobbyService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PlayerServiceTest {
    Lobby lobby;
    Game game;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private GameService gameService;
    @Autowired
    private GameRepository gameRepository;

    @BeforeEach
    public void setup() {
        lobby = lobbyService.startLobby(new CreateLobbyDto("KdgUser1", 5, 60));
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser2");
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser3");
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser4");
        lobby = lobbyService.getLobbyByIdWithPlayers(lobby.getLobbyId());
        game = gameService.startGame(lobby);
    }

    @AfterEach
    public void tearDown() {
        gameRepository.delete(game);
    }

    @Test
    void editingPlayerWorks() {
        String token = registrationService.registerWithoutEmail(new RegistrationRequest("user", "user@gmail.com", "Password", false));
        registrationService.confirmToken(token);
        Player player = playerService.loadUserByUsername("user");
        playerService.editPlayer(new PlayerDto(player.getPlayerId(), "newName", "newEmail", "picture"));
        //assert that the player actually changed
        Player playerToCheck = new Player("newName", "newEmail", "Password", AppUserRole.USER, false, "coolProfilePic");
        playerToCheck.setProfilePicture("picture");
        playerRepository.findById(player.getPlayerId()).ifPresent(player1 -> assertThat(player1).usingRecursiveComparison()
                .ignoringFields("playerId", "password", "friends","friendRequests", "enabled", "loyaltyPoints", "shopItems", "achievements")
                .isEqualTo(playerToCheck));
        playerService.editPlayer(new PlayerDto(player.getPlayerId(), player.getUsername(), player.getEmail(), "coolPicture"));

    }

    @Test
    void editingPlayerDoesNotChangeRoleOrPasswordOrId() {
        Player player = playerRepository.findAll().get(0);
        Player editedPlayer = playerService.editPlayer(new PlayerDto(player.getPlayerId(), "newName2", "newEmail2", "coolPicture"));
        //assert that the password and role didn't change
        Assertions.assertEquals(player.getPassword(), editedPlayer.getPassword());
        Assertions.assertEquals(player.getAppUserRole(), editedPlayer.getAppUserRole());
        Assertions.assertEquals(player.getPlayerId(), editedPlayer.getPlayerId());
        playerService.editPlayer(new PlayerDto(player.getPlayerId(), player.getUsername(), player.getEmail(), "coolPicture"));

    }

    @Test
    void getRecentlyPlayedWith() {
        Player player = playerRepository.findAll().get(0);
        List<Player> recentlyPlayedWith = playerService.getRecentlyPlayedWith(player.getUsername());
        Assertions.assertEquals(3, recentlyPlayedWith.size());
        Assertions.assertTrue(recentlyPlayedWith.stream().anyMatch(p -> p.getUsername().equals("KdgUser2")));
        Assertions.assertTrue(recentlyPlayedWith.stream().anyMatch(p -> p.getUsername().equals("KdgUser3")));
        Assertions.assertTrue(recentlyPlayedWith.stream().anyMatch(p -> p.getUsername().equals("KdgUser4")));
        Assertions.assertTrue(recentlyPlayedWith.stream().noneMatch(p -> p.getUsername().equals("KdgUser1")));
    }

    @Test
    void getLeaderboard() {
        //get all players
        Player player1 = playerService.loadUserByUsername("KdgUser1");
        Player player2 = playerService.loadUserByUsername("KdgUser2");
        Player player3 = playerService.loadUserByUsername("KdgUser3");
        Player player4 = playerService.loadUserByUsername("KdgUser4");
        Player player5 = playerService.loadUserByUsername("KdgUser5");
        player1.setGamesWon(2);
        player2.setGamesWon(4);
        player3.setGamesWon(5);
        player4.setGamesWon(2);
        player5.setGamesWon(8);
        playerRepository.saveAll(List.of(player1, player2, player3, player4, player5));
        List<Player> leaderboard = playerService.getLeaderboard();
        Assertions.assertEquals(10, leaderboard.size());
        Assertions.assertEquals("KdgUser5", leaderboard.get(0).getUsername());
        Assertions.assertEquals("KdgUser3", leaderboard.get(1).getUsername());
        Assertions.assertEquals("KdgUser2", leaderboard.get(2).getUsername());
    }
    //werkt niet in de pipeline :(
    /*
    @Test
    void getLocalPlayers() throws Exception {
        mockMvc.perform(post("/api/player/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("KdgUser1", "password"))))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/player/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("KdgUser2", "password"))))
                .andDo(print())
                .andExpect(status().isOk());
        playerService.getAllLocalPlayers("KdgUser1").forEach(player -> {
            Assertions.assertEquals("KdgUser2", player.getUsername());
        });
    }
    @Test
    void getLocalPlayersGivesNoOneIfNoOneIsLoggedInOnNetworkExceptYou() throws Exception {
        mockMvc.perform(post("/api/player/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("KdgUser1", "password"))))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/player/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest("KdgUser2", "password"))))
                .andDo(print())
                .andExpect(status().isOk());
        playerService.logout("KdgUser2");
        var players = playerService.getAllLocalPlayers("KdgUser1");
        Assertions.assertEquals(0, players.size());
    }
     */
}