package kdg.be.riskbackend.game.services;

import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.repositories.GameRepository;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.repositories.LobbyRepository;
import kdg.be.riskbackend.lobby.services.LobbyService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TerritoryServiceTests {
    @Autowired
    TerritoryService territoryService;
    @Autowired
    GameRepository gameRepository;
    Game game;
    Lobby lobby;
    @Autowired
    GameService gameService;
    @Autowired
    LobbyService lobbyService;
    @Autowired
    LobbyRepository lobbyRepository;

    @BeforeAll
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
    void calculateRemainingTroopsWorks() {
        var territory = territoryService.getRandomTerritory(game.getGameId());
        var amountOfTroops = 5;
        territoryService.setOwnersRemainingTroopsToReinforce(territory.getTerritoryId(), amountOfTroops);
        var amountBeforeReinforcement = territory.getTroops();
        territoryService.calculateRemainingTroops(territory.getTerritoryId(), amountOfTroops);
        territoryService.placeTroops(territory.getTerritoryId(), amountOfTroops);
        Assertions.assertEquals(amountBeforeReinforcement + amountOfTroops, territoryService.getTerritoryById(territory.getTerritoryId()).getTroops());
    }
}
