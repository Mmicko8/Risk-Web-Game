package kdg.be.riskbackend.identity.services;

import kdg.be.riskbackend.identity.dtos.EditPlayerUsernameDto;
import kdg.be.riskbackend.identity.dtos.EquipShopItemDto;
import kdg.be.riskbackend.identity.exceptions.PlayerException;
import kdg.be.riskbackend.identity.domain.token.ConfirmationToken;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.dtos.PlayerDto;
import kdg.be.riskbackend.identity.repositories.PlayerRepository;
import kdg.be.riskbackend.identity.util.SsidFinder;
import kdg.be.riskbackend.lobby.exceptions.InvalidIdException;
import kdg.be.riskbackend.shop.domain.ItemCategory;
import kdg.be.riskbackend.shop.domain.ShopItem;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * This class is used to handle the players.
 */
@Service
@AllArgsConstructor
@Slf4j
public class PlayerService implements UserDetailsService {
    private final PlayerRepository playerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final SsidFinder ssidFinder;

    /**
     * actually searches for a player by their email
     *
     * @param email the email identifying the user whose data is required.
     * @return user details
     * @throws UsernameNotFoundException if the user is not found.
     */
    public Player loadUserByEmail(String email) throws UsernameNotFoundException {
        return playerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * create a new user
     *
     * @param player the player to be saved
     * @return token
     */
    @Transactional
    public String singUpPlayer(@Valid Player player) {
        boolean emailExists = playerRepository.findByEmail(player.getEmail()).isPresent();
        boolean usernameExists = playerRepository.findByUsername(player.getEmail()).isPresent();

        if (emailExists || usernameExists) {
            if (!playerRepository.findByEmail(player.getEmail()).get().isEnabled()) {
                return "user already exists but is not enabled, we have send another email with token";
            }
            throw new IllegalStateException("there already exists a user with given credentials");
        }
        String encodedPw = bCryptPasswordEncoder.encode(player.getPassword());
        player.setPassword(encodedPw);

        save(player);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                player
        );

        confirmationTokenService.createConfirmationToken(confirmationToken);

        return token;
    }

    /**
     * This method is used to enable a player.
     *
     * @param email the email of the player
     */
    public void enableAppUser(String email) {
        playerRepository.enablePlayer(email);
    }

    /**
     * gets a player by there username
     *
     * @param username the username of the player
     * @return the player
     */
    @Override
    public Player loadUserByUsername(String username) throws UsernameNotFoundException {
        return playerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * counts the players
     *
     * @return the amount of players
     */
    public long countPlayers() {
        return playerRepository.count();
    }

    /**
     * enables a user by its username
     *
     * @param username the username of the player
     */
    public void enableByUsername(String username) {
        Player player = playerRepository.findByUsername(username).orElseThrow(() -> new InvalidIdException("User not found"));
        player.setEnabled(true);
        save(player);
    }

    /**
     * finds an AI player
     *
     * @return the AI player
     */
    public Player findAnAiPlayer() {
        return playerRepository.findAllAiPlayer();
    }

    /**
     * saves a player
     *
     * @param player the player to be saved
     */
    public Player save(Player player) {
        return playerRepository.save(player);
    }

    /**
     * edits a player
     *
     * @param playerDto the data of the player
     * @return the updated player
     */
    public Player editPlayer(@Valid PlayerDto playerDto) {
        Player player = playerRepository.findById(playerDto.getId()).orElseThrow(() -> new PlayerException("Player not found"));
        player.setUsername(playerDto.getUsername());
        player.setEmail(playerDto.getEmail());
        player.setProfilePicture(playerDto.getProfilePicture());
        return save(player);
    }

    /**
     *  Updates the username of the given player.
     * @param playerDto the data containing the id of the player and the new username.
     */
    public void editPlayerUsername(EditPlayerUsernameDto playerDto) {
        Player player = playerRepository.findById(playerDto.getId()).orElseThrow(() -> new PlayerException("Player not found"));
        player.setUsername(playerDto.getUsername());
        playerRepository.save(player);
    }

    /**
     *  Equipes the shop item for the given player id.
     * @param equipShopItemDto the data containing the id of the player and id of the shop item.
     */
    public void equipShopItem(EquipShopItemDto equipShopItemDto) {
        Player player = playerRepository.findWithShopItems(equipShopItemDto.getPlayerId())
                .orElseThrow(() -> new PlayerException("Player not found"));

        ShopItem ownedItem = null;
        for (var item : player.getShopItems()) {
            if (item.getShopItemId() == equipShopItemDto.getShopItemId()) {
                ownedItem = item;
                break;
            }
        }
        if (ownedItem == null)
            throw new PlayerException("Player does not own the item they want to equip");

        if (ownedItem.getItemCategory() == ItemCategory.TITLE)
            player.setTitle(ownedItem.getName());
        else if (ownedItem.getItemCategory() == ItemCategory.PROFILE_PICTURE) {
            var picturePath = ownedItem.getName().replaceAll("\\s", "");
            picturePath = picturePath.substring(0, 1).toLowerCase() + picturePath.substring(1);
            player.setProfilePicture(picturePath);
        }
        playerRepository.save(player);
    }


    /**
     * gets all players you recently played with
     *
     * @return a list of all players
     */
    public List<Player> getRecentlyPlayedWith(String username) {
        return playerRepository.findRecentlyPlayedWith(username);
    }

    /**
     * adds loyalty points to a player
     *
     * @param username the username of the player
     * @param points the amount of points to be added
     */
    public void addLoyaltyPoints(String username, int points) {
        Player player = playerRepository.findByUsername(username).orElseThrow(() -> new PlayerException("Player not found"));
        player.addLoyaltyPoints(points);
        save(player);
    }

    /**
     * This method is used to find all players on the local network
     *
     * @param name the name of the player
     * @return a list of players
     */
    public List<Player> getAllLocalPlayers(String name) {
        //check if user exists
        loadUserByUsername(name);
        try {
            String ssidName = ssidFinder.getSsid();
            return playerRepository.findAllLocalPlayersExceptCurrent(ssidName, name);
        } catch (IOException e) {
            log.error("error while getting ssid name");
            throw new RuntimeException(e);
        }
    }

    /**
     * sets the ssid of a player
     *
     * @param username the username of the player
     */
    public void setSsid(String username) {
        Player player = playerRepository.findByUsername(username).orElseThrow(() -> new PlayerException("Player not found"));
        String ssid;
        try {
            ssid = ssidFinder.getSsid();
        } catch (IOException e) {
            ssid = null;
        }
        player.setSsid(ssid);
        save(player);
    }

    /**
     * logs out a player
     *
     * @param username the username of the player
     */
    public void logout(String username) {
        Player player = playerRepository.findByUsername(username).orElseThrow(() -> new PlayerException("Player not found"));
        player.setSsid(null);
        save(player);
    }

    /**
     * finds a player by its username with all his shopping items
     *
     * @param username the username of the player
     * @return player
     */
    public Player findByUsernameWithShopItems(String username) {
        return playerRepository.findByUsernameWithShopItems(username).orElseThrow(() -> new PlayerException("Player not found"));
    }

    /**
     * This method is used to find all the top players with the most wins
     */
    public List<Player> getLeaderboard() {
        return playerRepository.findLeaderboard();
    }

    /**
     * gets a player with its achievements
     *
     * @param playerId the id of the player
     * @return the player
     */
    public Player getPlayerByIdWithAchievements(long playerId) {
        return playerRepository.findByIdWithAchievements(playerId).orElseThrow(() -> new PlayerException("Player not found"));
    }

    /**
     * gets a player with its friends
     *
     * @param player1Email the email of the player
     * @return the player
     */
    public Player getUserByUsernameWithFriends(String player1Email) {
        return playerRepository.findUserByUsernameWithFriends(player1Email).orElseThrow(() -> new PlayerException("Player not found"));
    }

    /**
     * gets a player with its friend requests
     *
     * @param username the username of the player
     * @return the player
     */
    public Player getPlayerWithFriendRequests(String username) {
        return playerRepository.findPlayerWithFriendRequests(username).orElseThrow(() -> new PlayerException("Player not found"));
    }

    /**
     * gets a player with its friend requests and its friends
     *
     * @param username the username of the player
     * @return the player
     */
    public Player getPlayerWithFriendRequestsAndFriends(String username) {
        var player = playerRepository.findUserByUsernameWithFriends(username).orElse(null);
        if (player == null) {
            player = playerRepository.findByUsername(username).orElseThrow(() -> new PlayerException("Player not found"));
        }
        player.setFriendRequests(getPlayerWithFriendRequests(username).getFriendRequests());
        return player;
    }

    /**
     * deletes a player
     *
     * @param username the username of the player
     */
    @Transactional
    public void deletePlayerByUsername(String username) {
        var player = getPlayerWithFriendRequestsAndFriends(username);
        //delete all friends of the player
        player.getFriends().forEach(friend -> {
            friend.getFriends().remove(player);
            save(friend);
        });
        //delete all send invites from deleted player
        playerRepository.findAll().forEach(p -> {
            p.getFriendRequests().removeIf(friendRequest -> friendRequest.getUsername().equals(username));
            save(p);
        });
        playerRepository.deleteById(player.getPlayerId());
    }

    /**
     * gets a players profile
     *
     * @param username the username of the player
     * @return the player
     */
    public Player getPlayerProfile(String username) {
        var player = findByUsernameWithShopItems(username);
        var achievements = getPlayerByIdWithAchievements(player.getPlayerId()).getAchievements();
        player.setAchievements(achievements);
        return player;
    }
}
