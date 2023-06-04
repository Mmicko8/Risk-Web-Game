package kdg.be.riskbackend.identity.services;

import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.exceptions.FriendException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class FriendService {
    private final PlayerService playerService;

    /**
     * gets all friends requests of a player
     *
     * @param name the name of the player
     * @return a list of players
     */
    public List<Player> getFriendRequestsOfPlayer(String name) {
        //check if player exists
        playerService.loadUserByUsername(name);
        return playerService.getPlayerWithFriendRequests(name).getFriendRequests();
    }

    /**
     * decline a friend request
     *
     * @param yourUsername the name of the player
     * @param friendUsername the name of the friend
     */
    @Transactional
    public void declineFriend(String yourUsername, String friendUsername) {
        Player player = playerService.getPlayerWithFriendRequestsAndFriends(yourUsername);
        Player friend = playerService.getUserByUsernameWithFriends(friendUsername);
        //check if friend request exists
        var friendRequestIds = player.getFriendRequests().stream().map(Player::getPlayerId).toList();
        if (!friendRequestIds.contains(friend.getPlayerId())) throw new FriendException("Friend request not found");
        player.removeFriendRequest(friend);
        playerService.save(player);
    }

    /**
     * sends a friend request
     *
     * @param yourUsername the name of the player
     * @param friendUsername the name of the friend
     */
    public void sendFriendRequest(String yourUsername, String friendUsername) {
        if (yourUsername.equals(friendUsername)) {
            throw new FriendException("You can't add yourself as friend");
        }
        Player player = playerService.getPlayerWithFriendRequestsAndFriends(yourUsername);
        Player friend = playerService.getPlayerWithFriendRequests(friendUsername);
        var friendIds = player.getFriends().stream().map(Player::getPlayerId).toList();
        var friendRequestIds = player.getFriends().stream().map(Player::getPlayerId).toList();
        if (friendIds.contains(friend.getPlayerId())) {
            throw new FriendException("You are already friends with " + friendUsername);
        }
        if (friendRequestIds.contains(friend.getPlayerId())) {
            throw new FriendException("You already sent a friend request to this player");
        }
        friend.addFriendRequest(player);
        playerService.save(friend);
    }

    /**
     * remove a friend from a player
     *
     * @param player1Username the username of player1
     * @param player2Username the username of player2
     */
    public void removeFriend(String player1Username, String player2Username) {
        Player player = playerService.getUserByUsernameWithFriends(player1Username);
        Player friend = playerService.getUserByUsernameWithFriends(player2Username);
        //check if they have a friend relationship
        player.setFriends(player.getFriends().stream().filter(f -> !f.getUsername().equals(player2Username)).toList());
        friend.setFriends(friend.getFriends().stream().filter(f -> !f.getUsername().equals(player1Username)).toList());
        playerService.save(player);
        playerService.save(friend);
    }

    /**
     * add a friend to a player
     *
     * @param yourUsername   the username of player1
     * @param friendUsername the username of player2
     */
    @Transactional
    public void acceptFriendRequest(String yourUsername, String friendUsername) {
        //update friend requests
        Player player = playerService.getPlayerWithFriendRequestsAndFriends(yourUsername);
        Player friend = playerService.getPlayerWithFriendRequestsAndFriends(friendUsername);
        player.removeFriendRequest(friend);
        //update friends list
        friend.addFriend(player);
        player.addFriend(friend);
        //save
        playerService.save(player);
        playerService.save(friend);
    }

    public List<Player> getFriends(String username) {
        Player player = playerService.getUserByUsernameWithFriends(username);
        return player.getFriends();
    }
}
