package kdg.be.riskbackend.lobby.exceptions;

public class LobbyClosedException extends RuntimeException {
    public LobbyClosedException(String message) {
        super(message);
    }
}
