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
public class InviteWithEmail {
    @NotNull(message = "A valid email must be specified")
    String email;
    @Positive(message = "A valid lobby must be specified")
    long lobbyId;
}
