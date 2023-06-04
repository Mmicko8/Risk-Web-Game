import {Player} from "./Player";
import {PlayerCard} from "../PlayerCard";


export interface PlayerInGame {
    playerInGameId: number;
    color: string;
    player: Player;
    remainingTroopsToReinforce: number;
    playerCards: PlayerCard[];
    winner: boolean;
}