
import {getTerritoryDrawData} from "../../services/territoryService";
import Territory from "./Territory";
import {TerritoryModel} from "../../model/territory/TerritoryModel";
import {useContext} from "react";
import GameStateContext from "../../context/GameStateContext";
import {GameModel} from "../../model/game/GameModel";
import {useTheme} from "@mui/material/styles";

interface BoardProps {
    selectTerritory: (e: string, territoryName: string) => void;
    territories: TerritoryModel[];
    attackableTerritoryNames: string[] | null;
    fortifiableTerritoryNames: string[] | null;
}

function getTerritoryColor(game: GameModel, territoryOwnerId: number) {
    for (let i = 0; i < game.playersInGame.length; i++) {
        if (game.playersInGame[i].playerInGameId === territoryOwnerId) {
            return game.playersInGame[i].color;
        }
    }
}

export default function Board({selectTerritory, territories, attackableTerritoryNames, fortifiableTerritoryNames}: BoardProps) {
    const theme = useTheme();
    const DEFAULT_STROKE_THICKNESS = "1.5";
    const HIGHLIGHTED_STROKE_THICKNESS = "2.5";
    const VBOX_WIDTH = 1024;
    const VBOX_HEIGHT = 650;

    const strokeColor = (territoryName: string) => {
        if (attackableTerritoryNames && attackableTerritoryNames.includes(territoryName)) {
            return theme.palette.error.main;
        }
        if (fortifiableTerritoryNames && fortifiableTerritoryNames.includes(territoryName)) {
            return theme.palette.success.light;
        }
        return theme.palette.grey.A700;
    }
    const strokeWidth = (territoryName: string) => {
        if (attackableTerritoryNames && attackableTerritoryNames.includes(territoryName)) {
            return HIGHLIGHTED_STROKE_THICKNESS;
        }
        if (fortifiableTerritoryNames && fortifiableTerritoryNames.includes(territoryName)) {
            return HIGHLIGHTED_STROKE_THICKNESS;
        }
        return DEFAULT_STROKE_THICKNESS;
    }
    const {game} = useContext(GameStateContext);

    return (
        <div style={{display: "flex", justifyContent: "center"}}>
            <svg xmlns="http://www.w3.org/2000/svg"
                 width={'70%'} height={'70%'}
                 viewBox={`0 0 ${VBOX_WIDTH} ${VBOX_HEIGHT}`}>
                <g id="map" fill="none">
                    {territories.map((t) => {
                        let drawData = getTerritoryDrawData(t.name);

                        return <g key={t.name} stroke={strokeColor(t.name)} strokeWidth={strokeWidth(t.name)}
                           fill={getTerritoryColor(game, t.ownerId)} visibility="visible">
                            <Territory drawPath={drawData.drawPath} name={t.name}
                                       troopCount={t.troops}
                                       callback={selectTerritory} xOffset={drawData.xOffset}
                                       yOffset={drawData.yOffset}/>
                        </g>
                    })}
                </g>
            </svg>
        </div>
    )
}
