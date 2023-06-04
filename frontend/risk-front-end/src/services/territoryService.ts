import {territoriesDrawData} from "../data/territoryDrawData";
import {GameModel} from "../model/game/GameModel";
import {TerritoryModel} from "../model/territory/TerritoryModel";
import axios from "axios";

export function getTerritoryDrawData(name: string) {
    return territoriesDrawData[name];
}

export function getAllTerritoriesFromGameState(game: GameModel) {
    let territories = game.continents[0].territories;
    for (let i = 1; i < game.continents.length; i++) {
        territories = territories.concat(game.continents[i].territories);
    }
    return territories;
}

export function getTerritoryData(territories: TerritoryModel[], territoryName: string) {
    for (let i = 0; i < territories.length; i++) {
        if (territories[i].name === territoryName) {
            return territories[i];
        }
    }
    return null;
}

export async function getTerritoriesWithNeighbors(gameId: number) {
    const response = await axios.get<TerritoryModel[]>(`/api/territory/game/${gameId}/neighbors`);
    return response.data;
}

export async function placeTroops(territoryId: number, troops: number) {
    if (!territoryId) return;
    await axios.put(`/api/territory/${territoryId}/placeTroops/${troops}`);
}
