package kdg.be.riskbackend.game.domain.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Neighbor is a class that represents a neighbor of a territory
 */
@Entity(name = "neighbors")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Neighbor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long neighborId;
    private String name;

    /**
     * class constructor
     *
     * @param name the name of the neighbor
     */
    public Neighbor(String name) {
        this.name = name;
    }
}