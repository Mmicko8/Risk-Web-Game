package kdg.be.riskbackend.game.exceptions;

public class PlayerInGameException extends RuntimeException {
    public PlayerInGameException(String message) {
        super(message);
    }
}