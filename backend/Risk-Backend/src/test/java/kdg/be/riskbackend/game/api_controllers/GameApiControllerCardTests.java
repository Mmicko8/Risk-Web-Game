package kdg.be.riskbackend.game.api_controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.dtos.card.ExchangeCardsDto;
import kdg.be.riskbackend.game.repositories.GameRepository;
import kdg.be.riskbackend.game.services.CardService;
import kdg.be.riskbackend.game.services.GameService;
import kdg.be.riskbackend.game.services.PlayerInGameService;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.repositories.LobbyRepository;
import kdg.be.riskbackend.lobby.services.LobbyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameApiControllerCardTests {
    Lobby lobby;
    Game game;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired

    private PlayerInGameService playerInGameService;
    @Autowired

    private LobbyService lobbyService;
    @Autowired

    private LobbyRepository lobbyRepository;

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameService gameService;
    @Autowired
    private CardService cardService;


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
    @WithMockUser(username = "User")
    void exchangingCards() throws Exception {
        var gameCards = cardService.getGameCards(game.getGameId());
        game.setGameCards(gameCards);
        var gameId = game.getGameId();
        var player = playerInGameService.getCurrentPlayerInGameWithCards(game);
        for (int i = 0; i < 3; i++) {
            cardService.addTopCardToPlayer(game, player);
            //noinspection OptionalGetWithoutIsPresent
            game = gameRepository.findGameWithGameCards(gameId).get(); // updates the game state after adding card, otherwise will add 3 times the same card
        }
        player = playerInGameService.getCurrentPlayerInGameWithCards(game);
        var cardNames = player.getPlayerCards().stream().map(card -> card.getCard().getName())
                .collect(Collectors.toCollection(ArrayList::new));
        var dto = new ExchangeCardsDto(gameId, cardNames);
        mockMvc.perform(put("/api/game/exchangeCards")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isNoContent());

    }
    @Test
    @WithMockUser(username = "User")
    void exchangingCardsFailsWhenWrongGame() throws Exception {
        var gameCards = cardService.getGameCards(game.getGameId());
        game.setGameCards(gameCards);
        var gameId = game.getGameId();
        var player = playerInGameService.getCurrentPlayerInGameWithCards(game);
        for (int i = 0; i < 3; i++) {
            cardService.addTopCardToPlayer(game, player);
            //noinspection OptionalGetWithoutIsPresent
            game = gameRepository.findGameWithGameCards(gameId).get(); // updates the game state after adding card, otherwise will add 3 times the same card
        }
        player = playerInGameService.getCurrentPlayerInGameWithCards(game);
        var cardNames = player.getPlayerCards().stream().map(card -> card.getCard().getName())
                .collect(Collectors.toCollection(ArrayList::new));
        var dto = new ExchangeCardsDto(0, cardNames);
        mockMvc.perform(put("/api/game/exchangeCards")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }
}
