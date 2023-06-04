package kdg.be.riskbackend;

import kdg.be.riskbackend.achievements.domain.Achievement;
import kdg.be.riskbackend.achievements.repositories.AchievementRepository;
import kdg.be.riskbackend.achievements.services.AchievementService;
import kdg.be.riskbackend.game.domain.map.Neighbor;
import kdg.be.riskbackend.game.repositories.CardRepository;
import kdg.be.riskbackend.game.repositories.GameRepository;
import kdg.be.riskbackend.game.repositories.NeighborRepository;
import kdg.be.riskbackend.game.services.GameService;
import kdg.be.riskbackend.game.util.CardUtil;
import kdg.be.riskbackend.game.util.TerritoryUtil;
import kdg.be.riskbackend.identity.domain.user.AppUserRole;
import kdg.be.riskbackend.identity.domain.user.Player;
import kdg.be.riskbackend.identity.dtos.registration.RegistrationRequest;
import kdg.be.riskbackend.identity.repositories.PlayerRepository;
import kdg.be.riskbackend.identity.services.PlayerService;
import kdg.be.riskbackend.identity.services.RegistrationService;
import kdg.be.riskbackend.lobby.domain.Lobby;
import kdg.be.riskbackend.lobby.dtos.CreateLobbyDto;
import kdg.be.riskbackend.lobby.services.LobbyService;
import kdg.be.riskbackend.shop.domain.ItemCategory;
import kdg.be.riskbackend.shop.domain.ShopItem;
import kdg.be.riskbackend.shop.repository.ShopItemRepository;
import kdg.be.riskbackend.shop.service.ShopItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to seed the database with data.
 * It is only used for development purposes.
 */
@Component
public class Seeder implements org.springframework.boot.CommandLineRunner {
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private LobbyService lobbyService;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private ShopItemService shopItemService;
    @Autowired
    private AchievementService achievementService;
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private NeighborRepository neighborRepository;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private ShopItemRepository shopItemRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    private GameService gameService;
    @Autowired
    private AchievementRepository achievementRepository;

    /**
     * seeds the database with some data
     *
     * @param args all the data needed to seed the database
     */
    @Override
    public void run(String... args) {
        //seeding of data
        if (cardRepository.count() == 0) {
            var cards = CardUtil.generateCards();
            cardRepository.saveAll(cards);
        }
        if (neighborRepository.count() == 0) {
            List<Neighbor> neighbors = new ArrayList<>();
            TerritoryUtil.getAllTerritoryNames().forEach(territoryName -> neighbors.add(new Neighbor(territoryName)));
            neighborRepository.saveAll(neighbors);
        }

        if (achievementRepository.count() == 0) {
            achievementService.saveAchievement(new Achievement("First Win", "Win your first game", 50));
            achievementService.saveAchievement(new Achievement("Victory Lap", "Win 5 games", 150));
            achievementService.saveAchievement(new Achievement("Winning Streak", "Win 10 games", 400));
            achievementService.saveAchievement(new Achievement("Master Of The Game", "Win 50 games", 1000));
            achievementService.saveAchievement(new Achievement("AI Annihilator", "Win a game against an ai player", 25));
            achievementService.saveAchievement(new Achievement("Digital Dominator", "Win 5 games against an ai player", 100));
            achievementService.saveAchievement(new Achievement("AI Overload", "Win 10 games against an ai player", 300));
            achievementService.saveAchievement(new Achievement("Machine Master", "Win 50 games against an ai player", 750));
            achievementService.saveAchievement(new Achievement("The Determination Trophy", "Lose 50 games", 300));
            achievementService.saveAchievement(new Achievement("The Newcomer Award", "Play a game", 25));
            achievementService.saveAchievement(new Achievement("The Emerging Talent Award", "Play 10 games", 100));
            achievementService.saveAchievement(new Achievement("The Prodigy Prize", "Play 50 games", 500));
        }

        if (shopItemRepository.count() == 0) {
            shopItemService.saveShopItem(new ShopItem("Steve", ItemCategory.PROFILE_PICTURE, 150));
            shopItemService.saveShopItem(new ShopItem("Harold", ItemCategory.PROFILE_PICTURE, 300));
            shopItemService.saveShopItem(new ShopItem("Robbert", ItemCategory.PROFILE_PICTURE, 250));
            shopItemService.saveShopItem(new ShopItem("Jeffrey", ItemCategory.PROFILE_PICTURE, 200));
            shopItemService.saveShopItem(new ShopItem("Walter", ItemCategory.PROFILE_PICTURE, 100));
            shopItemService.saveShopItem(new ShopItem("Angrylerfish", ItemCategory.PROFILE_PICTURE, 150));
            shopItemService.saveShopItem(new ShopItem("Big Chungus", ItemCategory.PROFILE_PICTURE, 200));
            shopItemService.saveShopItem(new ShopItem("Budgetzilla", ItemCategory.PROFILE_PICTURE, 300));
            shopItemService.saveShopItem(new ShopItem("Cobra", ItemCategory.PROFILE_PICTURE, 400));
            shopItemService.saveShopItem(new ShopItem("Diver", ItemCategory.PROFILE_PICTURE, 50));
            shopItemService.saveShopItem(new ShopItem("Fluffington", ItemCategory.PROFILE_PICTURE, 500));
            shopItemService.saveShopItem(new ShopItem("Fluffy Viking", ItemCategory.PROFILE_PICTURE, 1000));
            shopItemService.saveShopItem(new ShopItem("Galaxy Bunny", ItemCategory.PROFILE_PICTURE, 600));
            shopItemService.saveShopItem(new ShopItem("Happy Burger", ItemCategory.PROFILE_PICTURE, 200));
            shopItemService.saveShopItem(new ShopItem("Lucha Libre", ItemCategory.PROFILE_PICTURE, 300));
            shopItemService.saveShopItem(new ShopItem("Meat Lover", ItemCategory.PROFILE_PICTURE, 250));
            shopItemService.saveShopItem(new ShopItem("Dragon", ItemCategory.PROFILE_PICTURE, 800));
            shopItemService.saveShopItem(new ShopItem("Werewolf Jack", ItemCategory.PROFILE_PICTURE, 700));
            shopItemService.saveShopItem(new ShopItem("Zombie", ItemCategory.PROFILE_PICTURE, 150));
            shopItemService.saveShopItem(new ShopItem("Zooming", ItemCategory.PROFILE_PICTURE, 350));

            shopItemService.saveShopItem(new ShopItem("The Knight", ItemCategory.TITLE, 20));
            shopItemService.saveShopItem(new ShopItem("The Protector", ItemCategory.TITLE, 20));
            shopItemService.saveShopItem(new ShopItem("The Soldier", ItemCategory.TITLE, 20));
            shopItemService.saveShopItem(new ShopItem("The Duke", ItemCategory.TITLE, 50));
            shopItemService.saveShopItem(new ShopItem("The Great", ItemCategory.TITLE, 50));
            shopItemService.saveShopItem(new ShopItem("The Tyrant", ItemCategory.TITLE, 75));
            shopItemService.saveShopItem(new ShopItem("The Strategist", ItemCategory.TITLE, 75));
            shopItemService.saveShopItem(new ShopItem("The Daredevil", ItemCategory.TITLE, 100));
            shopItemService.saveShopItem(new ShopItem("The Puppet Master", ItemCategory.TITLE, 100));
            shopItemService.saveShopItem(new ShopItem("The King", ItemCategory.TITLE, 150));
            shopItemService.saveShopItem(new ShopItem("The Queen", ItemCategory.TITLE, 150));
            shopItemService.saveShopItem(new ShopItem("The Dragon", ItemCategory.TITLE, 200));
            shopItemService.saveShopItem(new ShopItem("The Conqueror", ItemCategory.TITLE, 200));
            shopItemService.saveShopItem(new ShopItem("The Mastermind", ItemCategory.TITLE, 250));
            shopItemService.saveShopItem(new ShopItem("The Emperor", ItemCategory.TITLE, 300));
        }

        if (playerService.countPlayers() == 0) {
            Player player1 = new Player("KdgUser1", "kdgUser1@student.kdg.be", "password", AppUserRole.USER, false);
            Player player2 = new Player("KdgUser2", "kdgUser2@student.kdg.be", "password", AppUserRole.USER, false);
            Player player3 = new Player("KdgUser3", "kdgUser3@student.kdg.be", "password", AppUserRole.USER, false);
            Player player4 = new Player("KdgUser4", "kdgUser4@student.kdg.be", "password", AppUserRole.USER, false);
            Player player5 = new Player("KdgUser5", "kdgUser5@student.kdg.be", "password", AppUserRole.USER, false);
            Player napoleon = new Player("Napoleon", "Napoleon@student.kdg.be", "password", AppUserRole.USER, true);
            Player juliusCaesar = new Player("Julius Caesar", "juliusCaesar@student.kdg.be", "password", AppUserRole.USER, true);
            Player sunTzu = new Player("Sun Tzu", "SunTsu@gmail.com", "password", AppUserRole.USER, true);
            Player shaka = new Player("Shaka", "Shaka@gmail.com", "password", AppUserRole.USER, true);
            Player stalin = new Player("Stalin", "Stalin@gmail.com", "password", AppUserRole.USER, true);
            Player kitler = new Player("Kitler", "Kitler@gmail.com", "password", AppUserRole.USER, true);
            Player theBossBaby = new Player("The Boss Baby", "TheBossBaby@gmail.com", "password", AppUserRole.USER, true);
            List<Player> players = new ArrayList<>(List.of(player1, player2, player3, player4, player5, napoleon, juliusCaesar, sunTzu, shaka, stalin, kitler, theBossBaby));
            for (Player player : players) {
                registrationService.registerWithoutEmail(new RegistrationRequest(player.getUsername(), player.getEmail(), player.getPassword(), player.isAi()));
                playerService.enableByUsername(player.getUsername());
            }
            player1 = playerService.loadUserByUsername("KdgUser1");
            player2 = playerService.loadUserByUsername("KdgUser2");
            player3 = playerService.loadUserByUsername("KdgUser3");
            player4 = playerService.loadUserByUsername("KdgUser4");
            player5 = playerService.loadUserByUsername("KdgUser5");
            napoleon = playerService.loadUserByUsername("Napoleon");
            juliusCaesar = playerService.loadUserByUsername("Julius Caesar");
            sunTzu = playerService.loadUserByUsername("Sun Tzu");
            shaka = playerService.loadUserByUsername("Shaka");
            stalin = playerService.loadUserByUsername("Stalin");
            kitler = playerService.loadUserByUsername("Kitler");
            theBossBaby = playerService.loadUserByUsername("The Boss Baby");

            // set profile pictures
            player1.setProfilePicture("angrylerfish");
            player2.setProfilePicture("bigChungus");
            player3.setProfilePicture("galaxyBunny");
            player4.setProfilePicture("diver");
            player5.setProfilePicture("happyBurger");
            napoleon.setProfilePicture("napoleon");
            juliusCaesar.setProfilePicture("juliusCaesar");
            sunTzu.setProfilePicture("sunTzu");
            shaka.setProfilePicture("shaka");
            stalin.setProfilePicture("stalin");
            kitler.setProfilePicture("kitler");
            theBossBaby.setProfilePicture("theBossBaby");

            // set titles
            player1.setTitle("The Knight");
            player2.setTitle("The Tyrant");
            player3.setTitle("The Puppet Master");

            // setting game won for nicer leaderboard data
            // games lost, played and achievements statistics won't make sense for players 1,2,3,5 in profile page
            // (only player 4 has full data)
            player1.setGamesWon(2);
            player2.setGamesWon(4);
            player3.setGamesWon(5);
            player5.setGamesWon(8);

            // set loyalty points
            player1.setLoyaltyPoints(1100);
            player2.setLoyaltyPoints(1100);
            player3.setLoyaltyPoints(1100);
            player4.setLoyaltyPoints(1100);
            player5.setLoyaltyPoints(1100);

            // achievements for player 4
            player4.setAchievements(new ArrayList<>());
            player4.setGamesPlayed(1);
            player4 = achievementService.addGamesPlayedAchievementsToPlayer(player4);
            player4.setGamesPlayed(10);
            player4 = achievementService.addGamesPlayedAchievementsToPlayer(player4);
            player4.setGamesPlayed(50);
            player4 = achievementService.addGamesPlayedAchievementsToPlayer(player4);
            player4.setGamesWon(1);
            player4 = achievementService.addWinningAchievementsToPlayer(player4);
            player4.setGamesWon(5);
            player4 = achievementService.addWinningAchievementsToPlayer(player4);
            player4.setGamesWon(10);
            player4 = achievementService.addWinningAchievementsToPlayer(player4);
            player4.setGamesWon(50);
            player4 = achievementService.addWinningAchievementsToPlayer(player4);
            player4.setGamesWonAgainstAi(1);
            player4 = achievementService.addWinningAgainstAiAchievementPlayer(player4);
            player4.setGamesWonAgainstAi(5);
            player4 = achievementService.addWinningAgainstAiAchievementPlayer(player4);
            player4.setGamesWonAgainstAi(10);
            player4 = achievementService.addWinningAgainstAiAchievementPlayer(player4);
            player4.setGamesWonAgainstAi(50);
            player4 = achievementService.addWinningAgainstAiAchievementPlayer(player4);
            player4.setGamesLost(50);
            player4 = achievementService.addLosingAchievementsToPlayer(player4);
            player4.setGamesPlayed(100);

            playerRepository.saveAll(List.of(player1, player2, player3, player4, player5, napoleon, juliusCaesar,
                    sunTzu, shaka, stalin, kitler, theBossBaby));

            // buy profile pictures that each user has equipped
            shopItemService.buyItem(player1.getUsername(), 6);
            shopItemService.buyItem(player2.getUsername(), 7);
            shopItemService.buyItem(player3.getUsername(), 13);
            shopItemService.buyItem(player4.getUsername(), 1);
            shopItemService.buyItem(player4.getUsername(), 3);
            shopItemService.buyItem(player4.getUsername(), 10);
            shopItemService.buyItem(player4.getUsername(), 14);

            // buy title that users have equipped
            shopItemService.buyItem(player1.getUsername(), 21); // the knight
            shopItemService.buyItem(player2.getUsername(), 26); // The Tyrant
            shopItemService.buyItem(player3.getUsername(), 29); // the puppet master
        }
        if (gameRepository.count() == 0) {
            Lobby lobby = lobbyService.startLobby(new CreateLobbyDto("KdgUser1", 4, 60));
            lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser2");
            lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser3");
            lobbyService.joinLobby(lobby.getLobbyId(), "KdgUser4");
            lobby = lobbyService.getLobbyByIdWithPlayers(lobby.getLobbyId());
            gameService.startGame(lobby);
        }
    }
}
