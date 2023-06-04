package kdg.be.riskbackend.game.dtos.player_in_game;

import kdg.be.riskbackend.game.dtos.card.PlayerCardDto;
import kdg.be.riskbackend.identity.dtos.PlayerDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerInGameDto {
    private long playerInGameId;
    private int remainingTroopsToReinforce;
    private PlayerDto player;
    private String color;
    private List<PlayerCardDto> playerCards;
    private boolean winner;
}
