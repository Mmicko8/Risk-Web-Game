package kdg.be.riskbackend.game.domain.map;

import kdg.be.riskbackend.game.domain.game.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Continent is a group of countries
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "continents")
public class Continent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long continentId;
    @OneToMany(mappedBy = "continent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Territory> territories;
    private int bonusTroops;
    private String name;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    /**
     * class constructor
     *
     * @param territories the territories of the continent
     * @param name        the name of the continent
     */
    public Continent(List<Territory> territories, String name, int bonusTroops) {
        territories.forEach(territory -> territory.setContinent(this));
        this.territories = territories;
        this.name = name;
        this.bonusTroops = bonusTroops;
    }
}
