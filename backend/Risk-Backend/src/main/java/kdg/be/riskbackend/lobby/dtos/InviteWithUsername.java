package kdg.be.riskbackend.lobby.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InviteWithUsername {
    @NotNull(message = "A valid username must be specified")
    String username;
    @Positive(message = "A valid lobby must be specified")
    long lobbyId;
}
