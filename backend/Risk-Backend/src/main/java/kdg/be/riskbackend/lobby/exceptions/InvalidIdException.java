package kdg.be.riskbackend.lobby.exceptions;

public class InvalidIdException extends RuntimeException {
    public InvalidIdException(String idNotFound) {
        super(idNotFound);
    }
}
