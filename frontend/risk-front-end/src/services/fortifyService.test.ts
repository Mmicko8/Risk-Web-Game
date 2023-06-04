import {TerritoryModel} from "../model/territory/TerritoryModel";
import {getFortifiableTerritories} from "./fortifyService";

let territoriesTestData: TerritoryModel[] = [
    {
        "territoryId": 1,
        "name": "Ural",
        "ownerId": 1,
        "troops": 1,
        "neighbors": [{"name": "Afghanistan"}, {"name": "China"}, {"name": "Siberia"}, {"name": "Ukraine"}]
    },
    {
        "territoryId": 2,
        "name": "Yakutsk",
        "ownerId": 2,
        "troops": 1,
        "neighbors": [{"name": "Irkutsk"}, {"name": "Kamchatka"}, {"name": "Siberia"}]
    },
    {
        "territoryId": 3,
        "name": "Mongolia",
        "ownerId": 3,
        "troops": 1,
        "neighbors": [{"name": "China"}, {"name": "Irkutsk"}, {"name": "Japan"}, {"name": "Kamchatka"}, {"name": "Siberia"}]
    },
    {
        "territoryId": 4,
        "name": "Afghanistan",
        "ownerId": 4,
        "troops": 1,
        "neighbors": [{"name": "China"}, {"name": "India"}, {"name": "MiddleEast"}, {"name": "Ural"}, {"name": "Ukraine"}]
    }, {
        "territoryId": 5,
        "name": "Irkutsk",
        "ownerId": 1,
        "troops": 1,
        "neighbors": [{"name": "Kamchatka"}, {"name": "Mongolia"}, {"name": "Siberia"}, {"name": "Yakutsk"}]
    },
    {
        "territoryId": 6,
        "name": "Siam",
        "ownerId": 2,
        "troops": 1,
        "neighbors": [{"name": "China"}, {"name": "India"}, {"name": "Indonesia"}]
    },
    {
        "territoryId": 7,
        "name": "China",
        "ownerId": 3,
        "troops": 1,
        "neighbors": [{"name": "Afghanistan"}, {"name": "India"}, {"name": "Mongolia"}, {"name": "Siam"}, {"name": "Siberia"}, {"name": "Ural"}]
    },
    {
        "territoryId": 8,
        "name": "Japan",
        "ownerId": 4,
        "troops": 1,
        "neighbors": [{"name": "Kamchatka"}, {"name": "Mongolia"}]
    },
    {
        "territoryId": 9,
        "name": "Siberia",
        "ownerId": 1,
        "troops": 1,
        "neighbors": [{"name": "China"}, {"name": "Irkutsk"}, {"name": "Mongolia"}, {"name": "Ural"}, {"name": "Yakutsk"}]
    },
    {
        "territoryId": 10,
        "name": "Kamchatka",
        "ownerId": 2,
        "troops": 1,
        "neighbors": [{"name": "Irkutsk"}, {"name": "Japan"}, {"name": "Mongolia"}, {"name": "Yakutsk"}, {"name": "Alaska"}]
    },
    {
        "territoryId": 11,
        "name": "MiddleEast",
        "ownerId": 3,
        "troops": 1,
        "neighbors": [{"name": "EastAfrica"}, {"name": "Egypt"}, {"name": "Afghanistan"}, {"name": "India"}, {"name": "SouthEurope"}, {"name": "Ukraine"}]
    },
    {
        "territoryId": 12,
        "name": "India",
        "ownerId": 4,
        "troops": 1,
        "neighbors": [{"name": "Afghanistan"}, {"name": "China"}, {"name": "MiddleEast"}, {"name": "Siam"}]
    },
    {
        "territoryId": 13,
        "name": "GreatBritain",
        "ownerId": 1,
        "troops": 1,
        "neighbors": [{"name": "Iceland"}, {"name": "NorthEurope"}, {"name": "Scandinavia"}, {"name": "WestEurope"}]
    },
    {
        "territoryId": 14,
        "name": "WestEurope",
        "ownerId": 2,
        "troops": 1,
        "neighbors": [{"name": "NorthAfrica"}, {"name": "GreatBritain"}, {"name": "NorthEurope"}, {"name": "SouthEurope"}]
    },
    {
        "territoryId": 15,
        "name": "NorthEurope",
        "ownerId": 3,
        "troops": 1,
        "neighbors": [{"name": "GreatBritain"}, {"name": "Scandinavia"}, {"name": "SouthEurope"}, {"name": "Ukraine"}, {"name": "WestEurope"}]
    },
    {
        "territoryId": 16,
        "name": "SouthEurope",
        "ownerId": 4,
        "troops": 1,
        "neighbors": [{"name": "Egypt"}, {"name": "NorthAfrica"}, {"name": "MiddleEast"}, {"name": "NorthEurope"}, {"name": "Ukraine"}, {"name": "WestEurope"}]
    },
    {
        "territoryId": 17,
        "name": "Ukraine",
        "ownerId": 1,
        "troops": 1,
        "neighbors": [{"name": "Afghanistan"}, {"name": "MiddleEast"}, {"name": "Ural"}, {"name": "NorthEurope"}, {"name": "Scandinavia"}, {"name": "SouthEurope"}]
    },
    {
        "territoryId": 18,
        "name": "Scandinavia",
        "ownerId": 2,
        "troops": 1,
        "neighbors": [{"name": "GreatBritain"}, {"name": "Iceland"}, {"name": "NorthEurope"}, {"name": "Ukraine"}]
    },
    {
        "territoryId": 19,
        "name": "Iceland",
        "ownerId": 3,
        "troops": 1,
        "neighbors": [{"name": "GreatBritain"}, {"name": "Scandinavia"}, {"name": "Greenland"}]
    },
    {
        "territoryId": 20,
        "name": "Venezuela",
        "ownerId": 4,
        "troops": 1,
        "neighbors": [{"name": "CentralAmerica"}, {"name": "Brazil"}, {"name": "Peru"}]
    },
    {
        "territoryId": 21,
        "name": "Argentina",
        "ownerId": 1,
        "troops": 1,
        "neighbors": [{"name": "Brazil"}, {"name": "Peru"}]
    },
    {
        "territoryId": 22,
        "name": "Brazil",
        "ownerId": 2,
        "troops": 1,
        "neighbors": [{"name": "NorthAfrica"}, {"name": "Argentina"}, {"name": "Peru"}, {"name": "Venezuela"}]
    },
    {
        "territoryId": 23,
        "name": "Peru",
        "ownerId": 3,
        "troops": 1,
        "neighbors": [{"name": "Argentina"}, {"name": "Brazil"}, {"name": "Venezuela"}]
    },
    {
        "territoryId": 24,
        "name": "PapuaNewGuinea",
        "ownerId": 4,
        "troops": 1,
        "neighbors": [{"name": "EastAustralia"}, {"name": "Indonesia"}, {"name": "WestAustralia"}]
    },
    {
        "territoryId": 25,
        "name": "EastAustralia",
        "ownerId": 1,
        "troops": 1,
        "neighbors": [{"name": "PapuaNewGuinea"}, {"name": "WestAustralia"}]
    },
    {
        "territoryId": 26,
        "name": "WestAustralia",
        "ownerId": 2,
        "troops": 1,
        "neighbors": [{"name": "EastAustralia"}, {"name": "PapuaNewGuinea"}, {"name": "Indonesia"}]
    },
    {
        "territoryId": 27,
        "name": "Indonesia",
        "ownerId": 3,
        "troops": 1,
        "neighbors": [{"name": "Siam"}, {"name": "EastAustralia"}, {"name": "PapuaNewGuinea"}, {"name": "WestAustralia"}]
    },
    {
        "territoryId": 28,
        "name": "EastUS",
        "ownerId": 4,
        "troops": 1,
        "neighbors": [{"name": "CentralAmerica"}, {"name": "Ontario"}, {"name": "WestUS"}, {"name": "Quebec"}]
    },
    {
        "territoryId": 29,
        "name": "NorthWest",
        "ownerId": 1,
        "troops": 1,
        "neighbors": [{"name": "Alaska"}, {"name": "Alberta"}, {"name": "Greenland"}, {"name": "Ontario"}]
    },
    {
        "territoryId": 30,
        "name": "Alberta",
        "ownerId": 2,
        "troops": 1,
        "neighbors": [{"name": "Alaska"}, {"name": "NorthWest"}, {"name": "Ontario"}, {"name": "WestUS"}]
    },
    {
        "territoryId": 31,
        "name": "Quebec",
        "ownerId": 3,
        "troops": 1,
        "neighbors": [{"name": "EastUS"}, {"name": "Greenland"}, {"name": "Ontario"}]
    },
    {
        "territoryId": 32,
        "name": "WestUS",
        "ownerId": 4,
        "troops": 1,
        "neighbors": [{"name": "Alberta"}, {"name": "CentralAmerica"}, {"name": "EastUS"}, {"name": "Ontario"}]
    },
    {
        "territoryId": 33,
        "name": "CentralAmerica",
        "ownerId": 1,
        "troops": 1,
        "neighbors": [{"name": "EastUS"}, {"name": "WestUS"}, {"name": "Venezuela"}]
    },
    {
        "territoryId": 34,
        "name": "Alaska",
        "ownerId": 2,
        "troops": 1,
        "neighbors": [{"name": "Kamchatka"}, {"name": "Alberta"}, {"name": "NorthWest"}]
    },
    {
        "territoryId": 35,
        "name": "Ontario",
        "ownerId": 3,
        "troops": 1,
        "neighbors": [{"name": "Alberta"}, {"name": "EastUS"}, {"name": "Greenland"}, {"name": "NorthWest"}, {"name": "WestUS"}, {"name": "Quebec"}]
    },
    {
        "territoryId": 36,
        "name": "Greenland",
        "ownerId": 4,
        "troops": 1,
        "neighbors": [{"name": "Iceland"}, {"name": "NorthWest"}, {"name": "Ontario"}, {"name": "Quebec"}]
    },
    {
        "territoryId": 37,
        "name": "NorthAfrica",
        "ownerId": 1,
        "troops": 1,
        "neighbors": [{"name": "EastAfrica"}, {"name": "Egypt"}, {"name": "SouthEurope"}, {"name": "WestEurope"}, {"name": "Congo"}, {"name": "Brazil"}]
    },
    {
        "territoryId": 38,
        "name": "SouthAfrica",
        "ownerId": 2,
        "troops": 1,
        "neighbors": [{"name": "EastAfrica"}, {"name": "Madagascar"}, {"name": "Congo"}]
    },
    {
        "territoryId": 39,
        "name": "EastAfrica",
        "ownerId": 3,
        "troops": 1,
        "neighbors": [{"name": "Egypt"}, {"name": "Madagascar"}, {"name": "SouthAfrica"}, {"name": "NorthAfrica"}, {"name": "MiddleEast"}, {"name": "Congo"}]
    },
    {
        "territoryId": 40,
        "name": "Egypt",
        "ownerId": 4,
        "troops": 1,
        "neighbors": [{"name": "EastAfrica"}, {"name": "NorthAfrica"}, {"name": "MiddleEast"}, {"name": "SouthEurope"}]
    },
    {
        "territoryId": 41,
        "name": "Madagascar",
        "ownerId": 1,
        "troops": 1,
        "neighbors": [{"name": "EastAfrica"}, {"name": "SouthAfrica"}]
    },
    {
        "territoryId": 42,
        "name": "Congo",
        "ownerId": 2,
        "troops": 1,
        "neighbors": [{"name": "EastAfrica"}, {"name": "SouthAfrica"}, {"name": "NorthAfrica"}]
    }
];

test('testDataCheck', () => {
    expect(territoriesTestData.length).toBe(42);
    expect(territoriesTestData[2].name).toBe("Mongolia");
    expect(territoriesTestData[41].neighbors.length).toBe(3);
    expect(territoriesTestData[41].neighbors.toString()).toBe([{"name": "EastAfrica"}, {"name": "SouthAfrica"}, {"name": "NorthAfrica"}].toString());
})


test('getFortifiableTerritories', () => {
    const startTerritory = {
        "territoryId": 2, "name": "Yakutsk", "ownerId": 2, "troops": 1,
        "neighbors": [{"name": "Irkutsk"}, {"name": "Kamchatka"}, {"name": "Siberia"}]
    };

    const result = getFortifiableTerritories(startTerritory, [startTerritory.name], territoriesTestData);
    expect(result.length).toBe(4);
    expect(result.includes("Kamchatka")).toBe(true);
    expect(result.includes("Alberta")).toBe(true);
    expect(result.includes("Alaska")).toBe(true);
    expect(result.includes("Yakutsk")).toBe(true);
})


