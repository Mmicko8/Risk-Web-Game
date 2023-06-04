package kdg.be.riskbackend.game.domain.map;

import kdg.be.riskbackend.game.domain.game.PlayerInGame;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Territory is a class that represents a territory on the map
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "territories")
public class Territory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long territoryId;
    @ManyToMany
    private List<Neighbor> neighbors;
    private String name;
    @ManyToOne
    @JoinColumn(name = "player_in_game_id")
    private PlayerInGame owner;
    private int troops;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "continent_id")
    private Continent continent;

    /**
     * class constructor
     *
     * @param name      the name of the territory
     * @param neighbors the neighbors of the territory
     */
    public Territory(String name, List<Neighbor> neighbors) {
        this.name = name;
        this.neighbors = neighbors;
    }

    /**
     * reduces the amount of troops by one on the territory
     */
    public void loseTroop() {
        setTroops(getTroops() - 1);
    }
}
