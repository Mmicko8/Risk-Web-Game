import {TerritoryModel} from "./TerritoryModel";

export interface Continent {
    continentId: number;
    territories: TerritoryModel[];
    bonusTroops: number;
    name: string;
}