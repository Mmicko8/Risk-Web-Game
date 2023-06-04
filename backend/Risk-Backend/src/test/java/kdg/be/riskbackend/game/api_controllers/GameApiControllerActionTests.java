package kdg.be.riskbackend.game.api_controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.domain.game.Phase;
import kdg.be.riskbackend.game.dtos.phases.AttackDto;
import kdg.be.riskbackend.game.dtos.phases.FortifyDto;
import kdg.be.riskbackend.game.repositories.GameRepository;
import kdg.be.riskbackend.game.repositories.NeighborRepository;
import kdg.be.riskbackend.game.repositories.TerritoryRepository;
import kdg.be.riskbackend.game.services.GameService;
import kdg.be.riskbackend.game.services.TerritoryService;
import kdg.be.riskbackend.identity.domain.user.AppUserRole;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.repositories.LobbyRepository;
import kdg.be.riskbackend.lobby.services.LobbyService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.in;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameApiControllerActionTests {
    Lobby lobby;
    Game game;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired

    private LobbyService lobbyService;
    @Autowired

    private LobbyRepository lobbyRepository;

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameService gameService;

    @Autowired
    private TerritoryService territoryService;
    @Autowired
    private TerritoryRepository territoryRepository;
    @Autowired
    private NeighborRepository neighborRepository;

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
    void cleanup() {
        gameRepository.delete(game);
        lobbyRepository.delete(lobby);
    }

    @Test
    @WithMockUser(username = "currentUser@student.kdg.be")
    void attackWorks() throws Exception {
        //setup for the test
        //set game-phase to attack
        game.setPhase(Phase.ATTACK);
        game =  gameService.saveGame(game);
        game = gameService.getGameState(game.getGameId());
        var player1 = game.getPlayersInGame().get(0);
        var player2 = game.getPlayersInGame().get(1);
        var gameId = game.getGameId();
        var attackerTerritory = territoryService.getAllTerritoriesOfGame(gameId).get(0);
        attackerTerritory.setOwner(player1);
        var attackerTerritoryName = attackerTerritory.getName();
        var defenderTerritoryName =
                territoryService.getTerritoryNeighborsByTerritoryNameAndGameId(attackerTerritoryName, gameId).get(0)
                        .getName();
        var territory = territoryService.getTerritoryByNameAndGame(defenderTerritoryName, gameId);
        territory.setOwner(player2);
        territoryRepository.save(territory);
        var defenderTerritory = territoryService.getTerritoryByNameAndGame(defenderTerritoryName, gameId);
        defenderTerritory.setTroops(10);
        attackerTerritory.setTroops(10);
        territoryRepository.save(defenderTerritory);
        territoryRepository.save(attackerTerritory);
        //checking if the api call works
        var dto = new AttackDto(gameId, attackerTerritoryName, defenderTerritoryName, 3);
        mockMvc.perform(put("/api/game/attack")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amountOfSurvivingTroopsAttacker").exists())
                .andExpect(jsonPath("$.amountOfSurvivingTroopsDefender").exists())
                .andExpect(jsonPath("$.gameId").exists())
                .andExpect(jsonPath("$.attackerDices").exists())
                .andExpect(jsonPath("$.defenderDices").exists());
    }

    @Test
    @WithMockUser(username = "currentUser@student.kdg.be")
    void attackFailsIfGameIsInvalid() throws Exception {
        //setup for the test
        //set game-phase to attack
        game.setPhase(Phase.ATTACK);
        game =  gameService.saveGame(game);
        game = gameService.getGameState(game.getGameId());
        var player1 = game.getPlayersInGame().get(0);
        var player2 = game.getPlayersInGame().get(1);
        var gameId = game.getGameId();
        var attackerTerritory = territoryService.getAllTerritoriesOfGame(gameId).get(0);
        attackerTerritory.setOwner(player1);
        var attackerTerritoryName = attackerTerritory.getName();
        var defenderTerritoryName =
                territoryService.getTerritoryNeighborsByTerritoryNameAndGameId(attackerTerritoryName, gameId).get(0)
                        .getName();
        var territory = territoryService.getTerritoryByNameAndGame(defenderTerritoryName, gameId);
        territory.setOwner(player2);
        territoryRepository.save(territory);
        var defenderTerritory = territoryService.getTerritoryByNameAndGame(defenderTerritoryName, gameId);
        defenderTerritory.setTroops(10);
        attackerTerritory.setTroops(10);
        territoryRepository.save(defenderTerritory);
        territoryRepository.save(attackerTerritory);
        //checking if the api call works
        var dto = new AttackDto(0, attackerTerritoryName, defenderTerritoryName, 3);
        mockMvc.perform(put("/api/game/attack")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(username = "currentUser@student.kdg.be")
    void fortifyWorks() throws Exception {
        //set game-phase to attack
        game.setPhase(Phase.FORTIFICATION);
        game =  gameService.saveGame(game);
        //set troops
        var territory1 = territoryRepository.findByNameAndGameId("Ural", game.getGameId());
        //set a neighboring country to current owner
        var territoryWithNeighbors = neighborRepository.findNeighborsOfTerritory(territoryRepository.findByNameAndGameId("Ural", game.getGameId()).getTerritoryId());
        var neighborTerritory = territoryRepository.findByNameAndGameId(territoryWithNeighbors.getNeighbors().get(0).getName(), game.getGameId());
        neighborTerritory.setOwner(territory1.getOwner());
        territoryRepository.save(neighborTerritory);
        //set troops
        territory1.setTroops(10);
        territoryRepository.save(territory1);
        neighborTerritory.setTroops(10);
        territoryRepository.save(neighborTerritory);
        var dto = new FortifyDto(game.getGameId(), territory1.getName(), neighborTerritory.getName(), 2);
        //do api call
        mockMvc.perform(put("/api/game/fortify")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "currentUser@student.kdg.be")
    void fortifyFailsIfGameIsUnknown() throws Exception {
        //set game-phase to attack
        game.setPhase(Phase.FORTIFICATION);
        game =  gameService.saveGame(game);
        //set troops
        var territory1 = territoryRepository.findByNameAndGameId("Ural", game.getGameId());
        //set a neighboring country to current owner
        var territoryWithNeighbors = neighborRepository.findNeighborsOfTerritory(territoryRepository.findByNameAndGameId("Ural", game.getGameId()).getTerritoryId());
        var neighborTerritory = territoryRepository.findByNameAndGameId(territoryWithNeighbors.getNeighbors().get(0).getName(), game.getGameId());
        neighborTerritory.setOwner(territory1.getOwner());
        territoryRepository.save(neighborTerritory);
        //set troops
        territory1.setTroops(10);
        territoryRepository.save(territory1);
        neighborTerritory.setTroops(10);
        territoryRepository.save(neighborTerritory);
        var dto = new FortifyDto(0, territory1.getName(), neighborTerritory.getName(), 2);
        //do api call
        mockMvc.perform(put("/api/game/fortify")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "currentUser@student.kdg.be")
    void nextPhaseWorks() throws Exception {
        var result = mockMvc.perform(put("/api/game/" + game.getGameId() + "/nextPhase")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        var string = result.getResponse().getContentAsString();
        Assertions.assertEquals("\"ATTACK\"", string);
    }

    @Test
    @WithMockUser(username = "currentUser@student.kdg.be")
    void nextPhaseFailsIfGameNotFound() throws Exception {
        mockMvc.perform(put("/api/game/0/nextPhase")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "currentUser@student.kdg.be")
    void nextTurnWorks() throws Exception {
        Player player = new Player("KdgUser1", "kdgUser1@student.kdg.be", "password", AppUserRole.USER, true, "coolProfilePic");
        Player player2 = new Player("KdgUser2", "kdgUser2@student.kdg.be", "password", AppUserRole.USER, true, "coolProfilePic");
        Player player3 = new Player("KdgUser3", "kdgUser3@student.kdg.be", "password", AppUserRole.USER, true, "coolProfilePic");
        Player player4 = new Player("KdgUser4", "kdgUser4@student.kdg.be", "password", AppUserRole.USER, true, "coolProfilePic");
        game.setPhase(Phase.FORTIFICATION);
         gameService.saveGame(game);
        mockMvc.perform(put("/api/game/" + game.getGameId() + "/nextTurn")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.player.email",
                        in(new String[]{player.getEmail(), player2.getEmail(), player3.getEmail(),
                                player4.getEmail()})));
    }

    @Test
    @WithMockUser(username = "currentUser@student.kdg.be")
    void nextTurnFailsWithUnknownGame() throws Exception {
        game.setPhase(Phase.FORTIFICATION);
         gameService.saveGame(game);
        mockMvc.perform(put("/api/game/0/nextTurn")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
