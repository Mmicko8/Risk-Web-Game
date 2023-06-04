package kdg.be.riskbackend.lobby.dtos;

import kdg.be.riskbackend.identity.dtos.PlayerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * this class is for sending invites to other players, so they can join a lobby
 */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class InviteDto {
    private long id;
    private String usernameSender;
    private LobbyDto lobby;
    private PlayerDto player;
}
