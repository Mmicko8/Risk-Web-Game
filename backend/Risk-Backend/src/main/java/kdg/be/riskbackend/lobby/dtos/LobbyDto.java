package kdg.be.riskbackend.lobby.dtos;

import java.util.List;
import kdg.be.riskbackend.identity.dtos.PlayerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LobbyDto {
    long lobbyId;
    int maxPlayers;
    List<PlayerDto> players;
    PlayerDto host;
    int timer;
    boolean closed;
}
