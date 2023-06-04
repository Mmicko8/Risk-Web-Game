package kdg.be.riskbackend.game.services;

import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.domain.map.Territory;
import kdg.be.riskbackend.game.repositories.TerritoryRepository;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.services.LobbyService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FortificationTerritoryFinderTest {
    @Autowired
    private FortificationTerritoryFinder fortificationTerritoryFinder;
    @Autowired
    private GameService gameService;
    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private TerritoryRepository territoryRepository;
    @Autowired
    private TerritoryService territoryService;


    @Test
    void getAllFortifiableTerritories() {
        Lobby lobby = lobbyService.startLobby(new CreateLobbyDto("KdgUser1", 5, 60));
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser2");
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser3");
        lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser4");
        lobby = lobbyService.getLobbyByIdWithPlayers(lobby.getLobbyId());
        Game game = gameService.startGame(lobby);
        Territory territory1 = territoryService.getTerritoryByNameAndGame("Ural", game.getGameId());
        Territory territory2 = territoryService.getTerritoryByNameAndGame("Irkutsk", game.getGameId());
        Territory territory3 = territoryService.getTerritoryByNameAndGame("Siberia", game.getGameId());
        Territory territory4 = territoryService.getTerritoryByNameAndGame("Mongolia", game.getGameId());
        Territory territory5 = territoryService.getTerritoryByNameAndGame("Kamchatka", game.getGameId());
        territory1.setOwner(game.getPlayersInGame().get(0));
        territory2.setOwner(game.getPlayersInGame().get(0));
        territory3.setOwner(game.getPlayersInGame().get(0));
        territory4.setOwner(game.getPlayersInGame().get(0));
        territory5.setOwner(game.getPlayersInGame().get(0));
        territoryRepository.saveAll(List.of(territory1, territory2, territory3, territory4, territory5));
        var territories = fortificationTerritoryFinder.getAllFortifiableTerritories("Ural", game.getGameId());
        Assertions.assertTrue(territories.contains("Irkutsk"));
        Assertions.assertTrue(territories.contains("Siberia"));
        Assertions.assertTrue(territories.contains("Mongolia"));
        Assertions.assertTrue(territories.contains("Kamchatka"));
        Assertions.assertFalse(territories.contains("Ural"));
    }
}