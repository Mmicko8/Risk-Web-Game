package kdg.be.riskbackend.achievements.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Achievements")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long achievementId;
    @NotNull(message = "Name is mandatory")
    private String name;
    private String description;
    @Positive(message = "Points need to be positive")
    private int points;

    /**
     * class constructor
     *
     * @param name        the name of the achievement
     * @param description the description of the achievement
     * @param points      the amount of points the achievement is worth
     */
    public Achievement(String name, String description, int points) {
        this.name = name;
        this.description = description;
        this.points = points;
    }
}
