package kdg.be.riskbackend.lobby.services;

import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.exceptions.InvalidPlayerException;
import kdg.be.riskbackend.identity.exceptions.PlayerException;
import kdg.be.riskbackend.identity.repositories.PlayerRepository;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.exceptions.FullLobbyException;
import kdg.be.riskbackend.lobby.exceptions.InvalidIdException;
import kdg.be.riskbackend.lobby.exceptions.LobbyClosedException;
import kdg.be.riskbackend.lobby.repositories.LobbyRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

/**
 * this class contains the lobby service with logic for the lobby's
 */
@Service
@AllArgsConstructor
public class LobbyService {
    private final PlayerService playerService;
    private final LobbyRepository lobbyRepository;
    private final PlayerRepository playerRepository;

    /**
     * creates a new lobby
     *
     * @param createLobbyDto username and maxPlayers
     * @return the lobby
     */
    @Transactional
    public Lobby startLobby(CreateLobbyDto createLobbyDto) {
        Player player = playerService.loadUserByUsername(createLobbyDto.getUsername());
        if (player == null) {
            throw new InvalidPlayerException("Player not found");
        }
        Lobby lobby = new Lobby(createLobbyDto.getMaxPlayers(), player, createLobbyDto.getTimer());
        lobby = createLobby(lobby);
        lobby = joinLobby(lobby.getLobbyId(), createLobbyDto.getUsername());
        return lobby;
    }

    /**
     * saves the lobby
     *
     * @param lobby the lobby
     * @return the lobby
     */
    public Lobby createLobby(@Valid Lobby lobby) {
        return lobbyRepository.save(lobby);
    }

    /**
     * closes a lobby
     *
     * @param lobby the lobby
     */
    public void closeLobby(Lobby lobby) {
        lobby.setClosed(true);
        createLobby(lobby);
    }

    /**
     * Adds a player to the lobby if lobby is full throws FullLobbyException
     *
     * @param lobbyId the lobby to add the player in
     * @return the lobby
     */
    @Transactional
    public Lobby joinLobby(long lobbyId, String username) {
        Lobby lobby = lobbyRepository.findByIdWithPlayers(lobbyId).orElseThrow(() -> new InvalidIdException("Lobby was not found"));
        var player = playerRepository.findByUsername(username).orElseThrow(() -> new InvalidPlayerException("Player for username: " + username + " was not found"));
        if (lobby.getPlayers().size() == lobby.getMaxPlayers()) {
            throw new FullLobbyException("Lobby is already full");
        }
        if (lobby.isClosed()) {
            throw new LobbyClosedException("Lobby is already closed");
        }
        if (lobby.getPlayers().contains(player)) {
            throw new PlayerException("Player is already in lobby");
        }
        lobby.getPlayers().add(player);
        return createLobby(lobby);
    }

    /**
     * gets a lobby with its players
     *
     * @param lobbyId the id of the lobby
     * @return the lobby
     */
    public Lobby getLobbyByIdWithPlayers(Long lobbyId) {
        return lobbyRepository.findByIdWithPlayers(lobbyId).orElseThrow(() -> new InvalidIdException("Lobby not found"));
    }

    /**
     * adds a random AI player to a lobby
     *
     * @param lobbyId the id of the lobby
     */
    @Transactional
    public void addRandomAiPlayer(long lobbyId) {
        Lobby lobby = lobbyRepository.findByIdWithPlayers(lobbyId).orElseThrow(() -> new InvalidIdException("Lobby not found"));
        Player player;
        do {
            player = playerService.findAnAiPlayer();
            Player finalPlayer = player;
            if (lobby.getPlayers().stream().filter(p -> p.getUsername().equals(finalPlayer.getUsername())).toList().size() > 0) {
                player = null;
            }
        } while (player == null);
        joinLobby(lobby.getLobbyId(), player.getUsername());
    }

    /**
     * gets a lobby
     *
     * @param lobbyId the id of the lobby
     * @return the lobby
     */
    public Lobby getLobbyById(long lobbyId) {
        return lobbyRepository.findById(lobbyId).orElseThrow(() -> new InvalidIdException("Lobby not found"));
    }

    /**
     * gets an amount open lobbies
     *
     * @param amount the amount of lobbies
     * @return a list of lobbies
     */
    public List<Lobby> getOpenLobbies(int amount) {
        return lobbyRepository.findOpenLobbies(amount);
    }

    /**
     * gets all lobbies of a player
     *
     * @param name the name of the player
     * @return a list of lobbies
     */
    public List<Lobby> getLobbiesOfPlayer(String name) {
        return lobbyRepository.findLobbiesOfPlayer(name);
    }

    /**
     * gets an amount of open lobbies that are not joined by the player
     *
     * @param amount the amount of lobbies
     * @param username the name of the player
     * @return a list of lobbies
     */
    public List<Lobby> getOpenLobbiesNotJoinedByUser(int amount, String username) {
        return lobbyRepository.findOpenLobbiesNotJoinedByUser(amount, username);
    }

    /**
     * gets all lobbies that the user joined but nog started yet
     *
     * @param username the name of the player
     * @return a list of lobbies
     */
    public List<Lobby> getJoinedNotStartedLobbies(String username) {
        //check if user exists
        playerService.loadUserByUsername(username);
        return lobbyRepository.findJoinedNotStartedLobbies(username);
    }

    /**
     * gets the amount of open lobbies
     *
     * @return the amount of open lobbies
     */
    public int getAmountOpenLobbies() {
        return lobbyRepository.countOpenLobbies();
    }

    /**
     * gets the amount of closed lobbies
     *
     * @return the amount of closed lobbies
     */
    public int getAmountClosedLobbies() {
        return lobbyRepository.countClosedLobbies();
    }
}
