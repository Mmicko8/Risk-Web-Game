package kdg.be.riskbackend.game.util;


import kdg.be.riskbackend.game.domain.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Utility class for cards
 */
public class CardUtil {
    /**
     * Generates list of cards using territory names.
     *
     * @return a list of cards
     */
    public static List<Card> generateCards() {
        List<Card> cards = new ArrayList<>();
        var territoryNames = TerritoryUtil.getAllTerritoryNames();
        Collections.shuffle(territoryNames);
        Random rand = new Random();
        for (var name : territoryNames) {
            cards.add(new Card(name, rand.nextInt(1, 3)));
        }
        return cards;
    }
}
