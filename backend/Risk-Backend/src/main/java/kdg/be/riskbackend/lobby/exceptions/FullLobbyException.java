package kdg.be.riskbackend.lobby.exceptions;

public class FullLobbyException extends RuntimeException {
    public FullLobbyException(String message) {
        super(message);
    }
}
