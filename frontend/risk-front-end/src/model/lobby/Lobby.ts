import {Player} from "../player/Player";

export type Lobby = {
    lobbyId: number,
    maxPlayers: number,
    players: Player[],
    host: Player,
    timer: number,
    closed: boolean
}