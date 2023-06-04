package kdg.be.riskbackend.game.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * list of colors used in the game
 */
public class RandomColors {
    //a list of all colors
    private static final List<String> colors = Arrays.asList("#CEA252", "#BDDB8C", "#C87B6A", "#B091CA", "#4EB8FF");

    /**
     * gets a list of random colors
     *
     * @param amount the amount of colors you want
     * @return a list of random colors
     */
    public static List<String> getRandomColors(int amount) {
        if (amount > colors.size()) {
            throw new IllegalArgumentException("amount is too high");
        }
        List<String> randomColors = new ArrayList<>();
        Collections.shuffle(colors);
        for (int i = 0; i < amount; i++) {
            randomColors.add(colors.get(i));
        }
        return randomColors;
    }
}
