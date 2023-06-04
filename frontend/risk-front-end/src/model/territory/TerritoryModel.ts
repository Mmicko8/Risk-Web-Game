import {Neighbor} from "./Neighbor";

export interface TerritoryModel {
    territoryId: number;
    name: string;
    ownerId: number;
    troops: number;
    neighbors: Neighbor[]
}
