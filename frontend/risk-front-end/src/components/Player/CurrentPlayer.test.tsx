import {render, screen} from "@testing-library/react";
import React from "react";
import CurrentPlayer from "./CurrentPlayer";
import {getPhaseNumber, Phases} from "../../services/gameService";

test('Integration-test CurrentPlayer', () => {

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

    let phase = Phases.REINFORCEMENT;

    render(<CurrentPlayer currentPhase={getPhaseNumber(phase)} currentPlayer={testDataPlayerInGame} nextPhase={jest.fn} nextTurn={jest.fn}/>);
    expect(screen.getByText(testDataPlayer.username)).toBeInTheDocument()
    expect(screen.getByTestId("troopIconBadge")).toHaveTextContent(testDataPlayerInGame.remainingTroopsToReinforce.toString())
});