package kdg.be.riskbackend.game.services;

import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.domain.game.Phase;
import kdg.be.riskbackend.game.domain.map.Territory;
import kdg.be.riskbackend.game.dtos.phases.AttackDto;
import kdg.be.riskbackend.game.repositories.GameRepository;
import kdg.be.riskbackend.game.repositories.TerritoryRepository;
import kdg.be.riskbackend.game.dtos.phases.AttackResponse;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.services.LobbyService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameEndingTests {
    Lobby lobby;
    Game game;
    @Autowired
    private GameService gameService;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private TerritoryService territoryService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private TerritoryRepository territoryRepository;

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
    void ifAllContinentsAreOwnedByOnePlayerThanTheGameEnds() {
        //set phase to attack
        game.setPhase(Phase.ATTACK);
        gameService.saveGame(game);
        //set all territories to player 1 and troops to 10
        territoryService.getAllTerritoriesOfGame(game.getGameId()).forEach(territory -> {
            territory.setTroops(10);
            territory.setOwner(game.getPlayersInGame().get(0));
            territoryService.save(territory);
        });
        //set statistics of all players to 0
        game.getPlayersInGame().forEach(playerInGame -> {
            Player player = playerInGame.getPlayer();
            player.setGamesLost(0);
            player.setGamesPlayed(0);
            player.setGamesWon(0);
            playerService.save(player);
        });
        //set 1 territory to be owned by player 2 and troops to 1
        Territory territory = territoryService.getAllTerritoriesOfGameWithNeighbors(game.getGameId()).get(0);
        territory.setOwner(game.getPlayersInGame().get(1));
        territory.setTroops(1);
        territoryRepository.save(territory);
        //get name of a neighbor of the territory
        var attackerTerritoryName = territory.getNeighbors().get(0).getName();
        //attack until won
        AttackResponse attackResponse;
        do {
            attackResponse = gameService.attack(new AttackDto(game.getGameId(), attackerTerritoryName, territory.getName(), 3));
        } while (attackResponse.getAmountOfSurvivingTroopsDefender() > 0);
        Game gameToCheck = gameService.getGameState(game.getGameId());
        //check ending of game
        Assertions.assertTrue(attackResponse.isAttackerWonGame());
        Assertions.assertEquals(gameToCheck.getPlayersInGame().get(0).getPlayer().getGamesLost(), 0);
        Assertions.assertEquals(gameToCheck.getPlayersInGame().get(0).getPlayer().getGamesWon(), 1);
        Assertions.assertEquals(gameToCheck.getPlayersInGame().get(1).getPlayer().getGamesLost(), 1);
        Assertions.assertEquals(gameToCheck.getPlayersInGame().get(1).getPlayer().getGamesWon(), 0);
        Assertions.assertEquals(gameToCheck.getPlayersInGame().get(2).getPlayer().getGamesLost(), 1);
        Assertions.assertEquals(gameToCheck.getPlayersInGame().get(2).getPlayer().getGamesWon(), 0);
        Assertions.assertEquals(gameToCheck.getPlayersInGame().get(3).getPlayer().getGamesLost(), 1);
        Assertions.assertEquals(gameToCheck.getPlayersInGame().get(3).getPlayer().getGamesWon(), 0);

    }


}