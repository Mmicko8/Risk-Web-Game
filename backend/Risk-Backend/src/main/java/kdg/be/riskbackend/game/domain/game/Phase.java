package kdg.be.riskbackend.game.domain.game;

/**
 * reinforcement phase: player can place armies on his territories
 * attack phase: player can attack other territories
 * fortification phase: player can move armies from one territory to another that is connected to it
 */
public enum Phase {
    REINFORCEMENT,
    ATTACK,
    FORTIFICATION
}
