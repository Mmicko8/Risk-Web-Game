package kdg.be.riskbackend.identity.domain.user;

import kdg.be.riskbackend.achievements.domain.Achievement;
import kdg.be.riskbackend.shop.domain.ShopItem;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class is used for creating a user.
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@Entity(name = "players")
public class Player implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long playerId;
    @Column(nullable = false)
    @NotNull(message = "Username cannot be null")
    @Length(min = 2, max = 15, message = "username must be between 2 and 15 characters")
    private String username;
    @Column(nullable = false)
    @NotNull(message = "Email cannot be null")
    private String email;
    @Column(nullable = false)
    @NotNull(message = "password must be specified")
    private String password;
    @Enumerated(EnumType.STRING)
    private AppUserRole appUserRole;
    private boolean locked = false;
    private boolean enabled = false;
    private boolean isAi = false;
    private String ssid;
    @NotNull(message = "Profile picture is required")
    private String profilePicture;
    private String title; // allowed to be null
    @PositiveOrZero(message = "Loyalty points should be greater or equals than 0")
    private long loyaltyPoints = 0;

    //statistics
    @PositiveOrZero(message = "Games played should be greater or equals than 0")
    private long gamesPlayed = 0;
    @PositiveOrZero(message = "Games won should be greater or equals than 0")
    private long gamesWon = 0;
    @PositiveOrZero(message = "Games won against ai should be greater or equals than 0")
    private long gamesWonAgainstAi = 0;
    @PositiveOrZero(message = "Games lost should be greater or equals than 0")
    private long gamesLost = 0;

    //relations with other classes
    private int aiDifficulty; //only if it's an AI player
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<Player> friends = new ArrayList<>();
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<Player> friendRequests = new ArrayList<>();
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    private List<ShopItem> shopItems = new ArrayList<>();
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Achievement> achievements = new ArrayList<>();


    /**
     * class constructor
     *
     * @param username the username of the player
     * @param email    the email of the player
     * @param password the password of the player
     * @param userRole the role of the player
     */
    public Player(String username, String email, String password, AppUserRole userRole, boolean isAi, String profilePicture) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.appUserRole = userRole;
        this.isAi = isAi;
        this.profilePicture = profilePicture;
    }

    /**
     * class constructor
     *
     * @param username the username of the player
     * @param email    the email of the player
     * @param password the password of the player
     * @param userRole the role of the player
     */
    public Player(String username, String email, String password, AppUserRole userRole, boolean isAi) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.appUserRole = userRole;
        this.isAi = isAi;
        profilePicture = "default";
    }

    /**
     * class constructor
     *
     * @param playerId the id of the player
     * @param username the username of the player
     * @param email    the email of the player
     * @param password the password of the player
     * @param userRole the role of the player
     * @param isAi     if the player is an AI player
     */
    public Player(long playerId, String username, String email, String password, AppUserRole userRole, boolean isAi, String profilePicture) {
        this(username, email, password, userRole, isAi, profilePicture);
        this.playerId = playerId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * add a friend to the list of friends
     *
     * @param friend the friend to add
     */
    public void addFriend(Player friend) {
        if (friends.stream().noneMatch(f -> f.getPlayerId() == friend.getPlayerId())) {
            friends.add(friend);
        }
    }

    /**
     * adds loyalty points to the player
     *
     * @param points the amount of points to add
     */
    public void addLoyaltyPoints(int points) {
        loyaltyPoints += points;
    }

    /**
     * removes a friend request from the list of friend requests
     *
     * @param player the player to remove
     */
    public void removeFriendRequest(Player player) {
        friendRequests.remove(player);
    }

    /**
     * adds a friend request to the list of friend requests
     *
     * @param player the player to add
     */
    public void addFriendRequest(Player player) {
        friendRequests.add(player);
    }
}
