package kdg.be.riskbackend.game.util;

import java.util.Random;

/**
 * Dice is used to roll a dice
 */
public class Dice {
    private static final Random random = new Random();

    /**
     * rolls a dice
     *
     * @return a random number between 1 and 6
     */
    public static int roll() {
        return random.nextInt(6) + 1;
    }
}
