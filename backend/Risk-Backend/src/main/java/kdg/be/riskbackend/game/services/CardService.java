package kdg.be.riskbackend.game.services;

import kdg.be.riskbackend.game.domain.card.Card;
import kdg.be.riskbackend.game.domain.card.GameCard;
import kdg.be.riskbackend.game.domain.card.PlayerCard;
import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.domain.game.PlayerInGame;
import kdg.be.riskbackend.game.exceptions.CardException;
import kdg.be.riskbackend.game.exceptions.WrongNumberOfStarsException;
import kdg.be.riskbackend.game.repositories.CardRepository;
import kdg.be.riskbackend.game.repositories.GameCardRepository;
import kdg.be.riskbackend.lobby.exceptions.InvalidIdException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the logic for the cards
 */
@Service
@Slf4j
@AllArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final GameCardRepository gameCardRepository;
    private final PlayerInGameService playerInGameService;

    /**
     * Fills the GameCard table with all 42 cards.
     *
     * @param game the game to add the cards to
     */
    public void generateCards(Game game) {
        var cards = cardRepository.findAll(); // cards only ever has 42 rows
        List<GameCard> gameCards = cards.stream().map(card -> new GameCard(game, card)).toList();
        gameCardRepository.saveAll(gameCards);
    }

    /**
     * gets all the cards from a game
     *
     * @param gameId the id of the game
     * @return all the cards of the game
     */
    public List<GameCard> getGameCards(Long gameId) {
        return gameCardRepository.findByGameId(gameId);
    }

    /**
     * Gets the card from the game that's on top of the deck (first of the list).
     *
     * @return the top card
     */
    public Card getTopCard(Game game) {
        var gameCards = game.getGameCards();
        if (gameCards.isEmpty()) throw new CardException("There are no cards left in deck of the game.");
        var gameCard = gameCards.get(0);
        var card = gameCard.getCard();
        gameCardRepository.delete(gameCard);
        return card;
    }

    /**
     * adds a card to a player
     *
     * @param playerInGame the player who receives the card
     */
    public void addTopCardToPlayer(Game game, PlayerInGame playerInGame) {
        try {
            playerInGameService.addCardToPlayer(getTopCard(game), playerInGame);
        }
        catch(CardException e){
            log.warn(e.getMessage());
        }
    }

    /**
     * updates if a player has won an attack while it's his turn
     *
     * @param game the game (with gameCards)
     */
    public void giveCurrentPlayerACard(Game game) {
        game.setGameCards(gameCardRepository.findByGameId(game.getGameId()));
        PlayerInGame currentPlayerInGame = playerInGameService.getCurrentPlayerInGameWithCards(game);
        if (currentPlayerInGame.isConqueredATerritoryThisTurn()) {
            currentPlayerInGame.setConqueredATerritoryThisTurn(false);
            addTopCardToPlayer(game, currentPlayerInGame);
        }
    }

    /**
     * Calculates the troops acquired from exchanging stars
     *
     * @param stars amount of stars
     * @return amount of troops acquired
     */
    public int calculateTroopsFromStars(int stars) {
        return switch (stars) {
            case 2 -> 2;
            case 3 -> 4;
            case 4 -> 7;
            case 5 -> 10;
            case 6 -> 13;
            case 7 -> 17;
            case 8 -> 21;
            case 9 -> 25;
            case 10 -> 30;
            default -> throw new WrongNumberOfStarsException("To many stars");
        };
    }

    /**
     * Checks if the given cards to exchange are owned by the playerInGame
     *
     * @param cardNames    the names of the cards the user wants to exchange
     * @param playerInGame the player who is exchanging his cards
     * @return a list of cards corresponding to the given cardNames
     */
    private List<Card> checkCardsToExchange(List<String> cardNames, PlayerInGame playerInGame) {
        var cardsToExchange = new ArrayList<Card>();
        var cardsOfCurrentPlayer = playerInGame.getPlayerCards().stream().map(PlayerCard::getCard).toList();
        for (var cardName : cardNames) {
            var card = cardsOfCurrentPlayer.stream().filter(c -> c.getName().equals(cardName)).findFirst()
                    .orElseThrow(() -> new InvalidIdException("Player does not have this card"));
            cardsToExchange.add(card);
        }
        return cardsToExchange;
    }

    /**
     * Exchanges the given cards of the player for extra troops
     *
     * @param game         the current state of the game
     * @param playerInGame the playerInGame exchanging his cards
     * @param cardNames    the names of the cards the user is exchanging
     * @return the updated game state
     */
    public Game exchangeCards(Game game, PlayerInGame playerInGame, List<String> cardNames) {
        var cardsToExchange = checkCardsToExchange(cardNames, playerInGame);
        var stars = cardsToExchange.stream().map(Card::getStars).mapToInt(Integer::intValue).sum();
        if (stars < 2) throw new WrongNumberOfStarsException("Not enough stars");
        if (stars > 10) throw new WrongNumberOfStarsException("Too many stars");

        var gameCards = game.getGameCards();
        for (var card : cardsToExchange) {
            playerInGameService.removeCardFromPlayer(card, playerInGame);
            gameCards.add(new GameCard(game, card));
        }
        game.setGameCards(gameCards);
        playerInGame = playerInGameService.getCurrentPlayerInGameWithCards(game); // new state of playerInGame

        var cardTroops = calculateTroopsFromStars(stars);
        var currentTroops = playerInGame.getRemainingTroopsToReinforce();
        playerInGame.setRemainingTroopsToReinforce(currentTroops + cardTroops);
        playerInGameService.savePlayerInGame(playerInGame);
        game.setPlayersInGame(playerInGameService.getPlayersInGameWithCards(game.getGameId()));
        return game;
    }
}
