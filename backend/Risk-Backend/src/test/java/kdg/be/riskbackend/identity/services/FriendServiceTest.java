package kdg.be.riskbackend.identity.services;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FriendServiceTest {
    @Autowired
    private FriendService friendService;

    @Test
    void sendingAndGettingFriendRequestsWorks() {
        friendService.sendFriendRequest("KdgUser4", "KdgUser2");
        var friendRequests = friendService.getFriendRequestsOfPlayer("KdgUser2");
        Assertions.assertEquals(1, friendRequests.size());
        Assertions.assertEquals("KdgUser4", friendRequests.get(0).getUsername());

    }

    @Test
    void declineFriend() {
        friendService.sendFriendRequest("KdgUser1", "KdgUser3");
        friendService.declineFriend("KdgUser3", "KdgUser1");
        var friendRequests = friendService.getFriendRequestsOfPlayer("KdgUser3");
        Assertions.assertEquals(0, friendRequests.size());
        var friendsUser3 = friendService.getFriends("KdgUser3");
        var friendsUser1 = friendService.getFriends("KdgUser1");
        Assertions.assertTrue(friendsUser3.stream().noneMatch(f -> f.getUsername().equals("KdgUser1")));
        Assertions.assertTrue(friendsUser1.stream().noneMatch(f -> f.getUsername().equals("KdgUser3")));
    }

    @Test
    void acceptFriendRequest() {
        friendService.sendFriendRequest("KdgUser1", "KdgUser3");
        friendService.acceptFriendRequest("KdgUser3", "KdgUser1");
        var friendRequestsP1 = friendService.getFriendRequestsOfPlayer("KdgUser1");
        var friendRequestsP3 = friendService.getFriendRequestsOfPlayer("KdgUser3");
        Assertions.assertEquals(0, friendRequestsP1.size());
        Assertions.assertEquals(0, friendRequestsP3.size());
        var friendsUser3 = friendService.getFriends("KdgUser3");
        var friendsUser1 = friendService.getFriends("KdgUser1");
        Assertions.assertTrue(friendsUser3.stream().anyMatch(f -> f.getUsername().equals("KdgUser1")));
        Assertions.assertTrue(friendsUser1.stream().anyMatch(f -> f.getUsername().equals("KdgUser3")));
    }


    @Test
    void removeFriend() {
        //checking if friend is added
        friendService.sendFriendRequest("KdgUser2", "KdgUser3");
        friendService.acceptFriendRequest("KdgUser3", "KdgUser2");
        var friendsUser3 = friendService.getFriends("KdgUser3");
        var friendsUser2 = friendService.getFriends("KdgUser2");
        Assertions.assertTrue(friendsUser3.stream().anyMatch(f -> f.getUsername().equals("KdgUser2")));
        Assertions.assertTrue(friendsUser2.stream().anyMatch(f -> f.getUsername().equals("KdgUser3")));
        //checking if friend is removed
        friendService.removeFriend("KdgUser3", "KdgUser2");
        friendsUser3 = friendService.getFriends("KdgUser3");
        friendsUser2 = friendService.getFriends("KdgUser2");
        Assertions.assertTrue(friendsUser3.stream().noneMatch(f -> f.getUsername().equals("KdgUser2")));
        Assertions.assertTrue(friendsUser2.stream().noneMatch(f -> f.getUsername().equals("KdgUser3")));
    }

}