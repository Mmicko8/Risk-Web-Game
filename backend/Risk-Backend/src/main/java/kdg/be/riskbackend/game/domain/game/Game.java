package kdg.be.riskbackend.game.domain.game;


import kdg.be.riskbackend.game.domain.card.GameCard;
import kdg.be.riskbackend.game.domain.map.Continent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Game is the main class of the game
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gameId;
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<Continent> continents;
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<GameCard> gameCards;
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<PlayerInGame> playersInGame;
    @NotNull(message = "StartTime cannot be null")
    @Column(nullable = false)
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Min(value = 10, message = "Game must have at least a timer of 10")
    private int timer;
    private LocalDateTime afkThreshold;
    @PositiveOrZero( message = "Game must be on turn 0 or higher")
    private int turn;
    @PositiveOrZero( message = "Game must be on turn 0 or higher")
    private int currentPlayerIndex;
    private Phase phase;

    /**
     * class constructor
     *
     * @param startTime the start time of the game
     */
    public Game(LocalDateTime startTime, int timer) {
        this.startTime = startTime;
        this.turn = 0;
        this.currentPlayerIndex = 0;
        this.phase = Phase.REINFORCEMENT;
        this.timer = timer;
        afkThreshold = LocalDateTime.now().plusSeconds(timer);
    }

    /*
     * sets the next player in the game
     */
    public void nextPlayer() {
        currentPlayerIndex++;
    }

    /**
     * adds a turn to the game
     */
    public void addTurn() {
        turn++;
    }
}
