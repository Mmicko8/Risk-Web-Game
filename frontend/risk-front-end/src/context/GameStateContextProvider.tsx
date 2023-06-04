import {ReactElement} from "react";
import {GameModel} from "../model/game/GameModel";
import GameStateContext from "./GameStateContext";

interface GameStateContextProviderProps {
    children: ReactElement | ReactElement[];
    game: GameModel;
}

export default function GameStateContextProvider({game, children}: GameStateContextProviderProps) {

    return (<GameStateContext.Provider value={{game: game}}>
    {children}
    </GameStateContext.Provider>);
}