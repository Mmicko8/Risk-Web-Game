import {TerritoryModel} from "../model/territory/TerritoryModel";
import {unionArrays} from "./utilsService";
import {getTerritoryData} from "./territoryService";
import axios from "axios";

export async function fortify(gameId: number, territoryFrom: string, territoryTo: string, troops: number) {
    await axios.put('/api/game/fortify', {gameId, territoryFrom, territoryTo, troops});
}

export function getFortifiableTerritories(startTerritory: TerritoryModel,
                                          connectedTerritoryNames: string[],
                                          allTerritories: TerritoryModel[])
    : string[] {
    let territory: TerritoryModel | null;
    const newConnectedTerritories: TerritoryModel[] = [];

    for (const n of startTerritory.neighbors) {
        territory = getTerritoryData(allTerritories, n.name);
        if (!territory) throw Error("startTerritory not in allTerritories list");

        if (territory.ownerId === startTerritory.ownerId && !connectedTerritoryNames.includes(territory.name))
            newConnectedTerritories.push(territory);
    }

    const newConnectedTerritoryNames = newConnectedTerritories.map(t => t.name);
    connectedTerritoryNames = connectedTerritoryNames.concat(newConnectedTerritoryNames)
    for (const newConnectedTerritory of newConnectedTerritories) {
        const result = getFortifiableTerritories(newConnectedTerritory, connectedTerritoryNames, allTerritories);
        connectedTerritoryNames = unionArrays(connectedTerritoryNames, result);
    }
    return connectedTerritoryNames;
}

export function getFortifiableTerritoryNames(territoriesWithNeighbors: TerritoryModel[], startTerritory: TerritoryModel) {
    const startTerritoryWithNeighbors = getTerritoryData(territoriesWithNeighbors, startTerritory.name);
    if (!startTerritoryWithNeighbors) throw Error("startTerritory cannot be found in given territories list");

    let fortifiableTerritories = getFortifiableTerritories(startTerritory, [startTerritory.name], territoriesWithNeighbors);
    // remove startTerritory from result
    return fortifiableTerritories.filter(value => value !== startTerritory.name);
}
