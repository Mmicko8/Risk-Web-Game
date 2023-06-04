import {createContext} from "react";
import {GameModel} from "../model/game/GameModel";


export interface IGameStateContext {
    game: GameModel;
}

export default createContext<IGameStateContext> ({game: {} as GameModel});