import {TerritoryModel} from "../model/territory/TerritoryModel";
import {getTerritoryData} from "./territoryService";
import axios from "axios";
import {AttackResult} from "../model/AttackResult";


export function getMaxTroopsForAttack(troopCount: number) {
    const MAX_ATTACK_TROOPS = 3;
    if (troopCount <= 1) throw Error("cannot attack with less than 2 troops");
    return Math.min(MAX_ATTACK_TROOPS, troopCount - 1);
}

export function hasTerritoryEnoughTroopsToAttack(territory: TerritoryModel) {
    const MIN_TROOPS_TO_ATTACK = 2;
    return territory.troops >= MIN_TROOPS_TO_ATTACK;
}

export function getAttackableTerritoryNames(territoriesWithNeighbors: TerritoryModel[], startTerritory: TerritoryModel) {
    let friendlyTerritoryNames = territoriesWithNeighbors
        .filter(t => {return t.ownerId === startTerritory.ownerId;})
        .map((t) => {return t.name});

    let attackableNeighborNameList: string[] = [];
    const startTerritoryWithNeighbors = getTerritoryData(territoriesWithNeighbors, startTerritory.name)
    if (!startTerritoryWithNeighbors) throw Error("startTerritory cannot be found in given territories list");

    startTerritoryWithNeighbors.neighbors.forEach((n) => {
        if (!friendlyTerritoryNames.includes(n.name)) {
            attackableNeighborNameList.push(n.name)
        }
    })
    return attackableNeighborNameList;
}

interface AttackParams {
    gameId: number;
    attackerTerritoryName: string;
    defenderTerritoryName: string;
    amountOfAttackers: number;
}

/**
 * Sends attack request and returns the results of the dice rolls in dice notation.
 * @param attackData data required to send attack request
 * @return results of the dice rolls in dice notation
 */
export async function attack (attackData: AttackParams) {
    const response = await axios.put('/api/game/attack', attackData);
    const result: AttackResult = response.data;
    const attackerDiceResult = convertToDiceNotation(result.attackerDices)
    const defenderDiceResult = convertToDiceNotation(result.defenderDices)
    return {attackerDiceNotation: attackerDiceResult, defenderDiceNotation: defenderDiceResult};
}

/**
 * Converts dice roll result to dice notation.
 *  example: [1,2,5] => "3d6@1,2,5"     |   3 -> dice count  ;  d6 -> six sided die  ;  1,2,5 -> result of each die
 * @param diceRoll the result of the dice roll, each value is the result a die landed on.
 *      [1,2,5] => 3 dice in total: first one resulted in '1', second in '2' and last one in '5'
 * @return the string in dice notation representing the dice roll result
 */
function convertToDiceNotation(diceRoll: number[]) {
    return `${diceRoll.length}d6@${diceRoll.join(",")}`
}
