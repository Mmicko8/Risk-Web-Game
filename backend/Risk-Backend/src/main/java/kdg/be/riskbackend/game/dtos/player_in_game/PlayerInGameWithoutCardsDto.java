package kdg.be.riskbackend.game.dtos.player_in_game;

import kdg.be.riskbackend.identity.dtos.PlayerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerInGameWithoutCardsDto {
    private long playerInGameId;
    private int remainingTroopsToReinforce;
    private PlayerDto player;
    private String color;

}