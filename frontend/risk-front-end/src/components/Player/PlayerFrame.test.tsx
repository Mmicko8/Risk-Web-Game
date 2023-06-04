import {render, screen} from "@testing-library/react";
import React from "react";
import PlayerFrame from "./PlayerFrame";


test('Integration-test PlayerFrame', async () => {

    let testDataPlayer = {
        id: 1,
        username: "KdgUser1",
        email: "user1@student.kdg.be",
        gamesWon: 5,
        profilePicture: "default",
        title: "The Knight"
    };

    let testDataCards = [
        {
            playerCardId: 1,
            card: {name: "alaska", stars: 2}
        },
        {
            playerCardId: 1,
            card: {name: "china", stars: 1}
        }
    ]

    let testDataPlayerInGame = {
        playerInGameId: 1,
        color: "blue",
        player: testDataPlayer,
        remainingTroopsToReinforce: 3,
        playerCards: testDataCards,
        winner: false
    }

    let testDataStats = {
        amountOfCards: testDataCards.length,
        amountOfTroops: 22,
        amountOfTerritories: 9
    }

    render(<PlayerFrame currentPlayerName={"KdgUser1"} playerInGame={testDataPlayerInGame}
                        playerInGameStats={testDataStats}/>);
    expect(await screen.findByTestId("playerFrame-cardAmount")).toHaveTextContent(testDataStats.amountOfCards.toString())
    expect(await screen.findByTestId("playerFrame-territoryAmount")).toHaveTextContent(testDataStats.amountOfTerritories.toString())
    expect(await screen.findByTestId("playerFrame-troopAmount")).toHaveTextContent(testDataStats.amountOfTroops.toString())
    expect(await screen.findByTestId("playerFrame-username")).toHaveTextContent(testDataPlayer.username)
    expect(await screen.findByTestId("playerFrame-playerTitle")).toHaveTextContent(testDataPlayer.title)
});
