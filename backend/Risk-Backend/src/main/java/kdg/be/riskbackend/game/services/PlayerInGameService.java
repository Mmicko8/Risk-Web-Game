package kdg.be.riskbackend.game.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.transaction.Transactional;
import kdg.be.riskbackend.achievements.services.AchievementService;
import kdg.be.riskbackend.achievements.services.StatisticService;
import kdg.be.riskbackend.game.domain.card.Card;
import kdg.be.riskbackend.game.domain.card.PlayerCard;
import kdg.be.riskbackend.game.domain.game.Game;
import kdg.be.riskbackend.game.domain.game.PlayerInGame;
import kdg.be.riskbackend.game.exceptions.CardException;
import kdg.be.riskbackend.game.exceptions.PlayerInGameException;
import kdg.be.riskbackend.game.repositories.PlayerCardRepository;
import kdg.be.riskbackend.game.repositories.PlayerInGameRepository;
import kdg.be.riskbackend.game.util.RandomColors;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.exceptions.PlayerException;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.lobby.exceptions.InvalidIdException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

/**
 * Handles all the logic for the players in a game
 */
@Service
@AllArgsConstructor
public class PlayerInGameService {
    private final PlayerInGameRepository playerInGameRepository;
    private final PlayerCardRepository playerCardRepository;
    private final PlayerService playerService;
    private final StatisticService statisticService;
    private final AchievementService achievementService;

    /**
     * generates a list of players in a game
     *
     * @param players the players that need to be saved
     * @return a List of players that are saved
     */
    public List<PlayerInGame> generateAllPlayersInGame(List<Player> players) {
        List<Player> playerList = new ArrayList<>(players);
        Collections.shuffle(playerList);
        var randomColors = RandomColors.getRandomColors(playerList.size());
        //create player in game
        List<PlayerInGame> playersInGame = new ArrayList<>();
        playerList.forEach(player -> {
            PlayerInGame playerInGame =
                    new PlayerInGame(player, randomColors.get(playerList.indexOf(player)));
            playersInGame.add(playerInGame);
        });
        return playersInGame;
    }

    /**
     * sets the remaining troops to reinforce of a player
     *
     * @param playerInGameId the id of the player
     * @param amountOfTroops the amount of troops to reinforce
     */
    public void setOwnersRemainingTroopsToReinforce(long playerInGameId, int amountOfTroops) {
        PlayerInGame playerInGame = playerInGameRepository.findById(playerInGameId)
                .orElseThrow(() -> new PlayerException("player not found"));
        playerInGame.setRemainingTroopsToReinforce(amountOfTroops);
        savePlayerInGame(playerInGame);
    }

    /**
     * gets the current player of the game
     *
     * @param game the game
     * @return the current player of the game
     */
    public PlayerInGame getCurrentPlayerInGame(Game game) {
        try {
            List<PlayerInGame> players =
                    playerInGameRepository.findByGameId(game.getGameId());
            return players.get(game.getCurrentPlayerIndex());
        } catch (RuntimeException e) {
            throw new PlayerInGameException("Player in game not found: " + e.getMessage());
        }
    }

    /**
     * sets value to true if a player has conquered a territory while it's his turn
     *
     * @param currentPlayerInGame the current player
     * @param won                 the boolean if the player has won
     */
    public void setCurrentPlayerConqueredATerritoryInHisTurn(PlayerInGame currentPlayerInGame,
                                                             boolean won) {
        currentPlayerInGame.setConqueredATerritoryThisTurn(won);
        savePlayerInGame(currentPlayerInGame);
    }

    /**
     * adds a card to a player
     *
     * @param card         the card to add
     * @param playerInGame the player who receives the card
     */
    public void addCardToPlayer(Card card, PlayerInGame playerInGame) {
        var playerCard = new PlayerCard(playerInGame, card);
        playerCardRepository.save(playerCard);
    }

    /**
     * gets the player in game by id with its cards
     *
     * @param game the game
     * @return the player in game with his cards
     */
    public PlayerInGame getCurrentPlayerInGameWithCards(Game game) {
        var currentPlayerInGameId = getCurrentPlayerInGame(game).getPlayerInGameId();
        return playerInGameRepository.findByIdWithCards(currentPlayerInGameId)
                .orElseThrow(() -> new InvalidIdException("player not found"));
    }

    /**
     * Gets all players in Game from the given game
     *
     * @param gameId the id of the game the playersInGame belong to
     * @return all players in Game from the given game
     */
    public List<PlayerInGame> getPlayersInGameWithCards(long gameId) {
        return playerInGameRepository.findByGameIdWithCards(gameId);
    }

    /**
     * Gets all players in Game from the given game
     *
     * @param gameId the id of the game the playersInGame belong to
     * @return all players in Game from the given game
     */
    public List<PlayerInGame> getPlayersInGame(long gameId) {
        return playerInGameRepository.findByGameId(gameId);
    }

    public PlayerInGame savePlayerInGame(@Valid PlayerInGame currentPlayerInGame) {
       return playerInGameRepository.save(currentPlayerInGame);
    }

    /**
     * removes a card from a player
     *
     * @param card         the card to remove
     * @param playerInGame the player who loses the card
     */
    public void removeCardFromPlayer(Card card, PlayerInGame playerInGame) {
        var playerCard = playerCardRepository.findByNameAndPlayerInGameId(card.getName(),
                        playerInGame.getPlayerInGameId())
                .orElseThrow(() -> new CardException(
                        "player card not found"));
        playerCardRepository.delete(playerCard);
    }

    /**
     * adds the given list of players to the game and updates the player statistics
     *
     * @param persistedGame the game to add the players to
     * @param players       the players to add to the game
     * @return game
     */
    public Game addPlayersToGame(Game persistedGame, List<Player> players) {
        //make players in game out of players
        List<PlayerInGame> playersInGame =
                generateAllPlayersInGame(players);
        //set players in game to game
        playersInGame.forEach(playerInGame -> playerInGame.setGame(persistedGame));
        //save statistics of player
        statisticService.addGamesPlayedToPlayers(playersInGame);
        // add achievements for playing games
        for (Player player : players) {
            player = playerService.getPlayerByIdWithAchievements(player.getPlayerId());
            player = achievementService.addGamesPlayedAchievementsToPlayer(player);
            playerService.save(player);
        }
        //save players in game
        persistedGame.setPlayersInGame(playersInGame);
        return persistedGame;
    }

    /**
     * update the statistics of the players after a game
     *
     * @param players the players that need to be updated
     */
    public void updatePlayerStatisticsAfterGame(List<PlayerInGame> players) {
        for (PlayerInGame playerInGame : players) {
            Player player = playerService.getPlayerByIdWithAchievements(playerInGame.getPlayer().getPlayerId());
            if (playerInGame.isWinner()) {
                player.setGamesWon(player.getGamesWon() + 1);
                player = achievementService.addWinningAchievementsToPlayer(player);
                if (aiPlayerInGame(players, player)) {
                    player.setGamesWonAgainstAi(player.getGamesWonAgainstAi() + 1);
                    player = achievementService.addWinningAgainstAiAchievementPlayer(player);
                }
            } else {
                player.setGamesLost(player.getGamesLost() + 1);
                player = achievementService.addLosingAchievementsToPlayer(player);
            }
            playerService.save(player);
        }
    }

    /**
     * Returns true if the given list of players contains an AI, except if the AI is {@code player}.
     *
     * @param players the list of playerInGames to check for an AI
     * @param player  the player that won't be checked
     * @return true if the given list of players contains an AI.
     */
    public boolean aiPlayerInGame(List<PlayerInGame> players, Player player) {
        for (PlayerInGame playerInGame : players) {
            Player pl = playerService.getPlayerByIdWithAchievements(
                    playerInGame.getPlayer().getPlayerId());
            if (pl.getPlayerId() != player.getPlayerId() && pl.isAi()) {
                return true;
            }
        }
        return false;
    }

    /**
     * sets a player to lost, so he can't play anymore
     *
     * @param playerInGameId the player to set to lost
     */
    public void setPlayerLost(long playerInGameId) {
        PlayerInGame playerInGame = playerInGameRepository.findById(playerInGameId)
                .orElseThrow(() -> new PlayerException("player not found"));
        playerInGame.setHasLost(true);
        savePlayerInGame(playerInGame);
    }

    /**
     * transfers the cards from the player another player
     *
     * @param from the player to transfer the cards from
     * @param to   the player to transfer the cards to
     */
    @Transactional
    public void transferCards(PlayerInGame from, PlayerInGame to) {
        var cardsFrom = playerCardRepository.findAllByPlayerInGame(from.getPlayerInGameId());
        //give all cards to the player
        cardsFrom.forEach(playerCard -> playerCard.setPlayerInGame(to));
        //the from player loses his cards
        from.setPlayerCards(new ArrayList<>());
        to.addPlayerCards(cardsFrom);
        playerCardRepository.saveAll(cardsFrom);
        savePlayerInGame(from);
        savePlayerInGame(to);

    }

    /**
     * gets a random player from the game except the current player
     *
     * @return a random player from the game except the current player
     */
    public PlayerInGame getRandomPlayerExceptCurrentPlayer(Game game) {
        if (game.getCurrentPlayerIndex() == game.getPlayersInGame().size() - 1) {
            return game.getPlayersInGame().stream().toList().get(0);
        } else {
            return game.getPlayersInGame().stream().toList().get(game.getCurrentPlayerIndex() + 1);
        }
    }
}
