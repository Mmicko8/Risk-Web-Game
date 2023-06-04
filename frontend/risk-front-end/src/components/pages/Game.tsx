import Board from "../map/Board";
import {useQuery, useQueryClient} from "react-query";
import {
    exchangeCards,
    getGameState,
    getPhaseNumber,
    nextPhase,
    nextTurn,
    Phases
} from "../../services/gameService";
import Loading from "../Loading";
import {Snackbar} from "@mui/material";
import AlertMui from "@mui/material/Alert";
import {
    getAllTerritoriesFromGameState,
    getTerritoriesWithNeighbors, getTerritoryData, placeTroops,
} from "../../services/territoryService";
import GameStateContextProvider from "../../context/GameStateContextProvider";
import {SyntheticEvent, useContext, useEffect, useReducer, useState} from "react";
import AccessContext from "../../context/AccessContext";
import TroopSelector from "../dialogs/TroopSelector";
import PlayerFrame from "../Player/PlayerFrame";
import Grid from "@mui/material/Grid";
import CurrentPlayer from "../Player/CurrentPlayer";
import {GameActionType, GameInteractionStateReducer} from "../../reducers/gameReducer";
import {useParams} from "react-router-dom";
import Fab from "@mui/material/Fab";
import CardsIcon from '@mui/icons-material/Style';
import CardSelector from "../dialogs/CardSelector/CardSelector";
import {attack, hasTerritoryEnoughTroopsToAttack} from "../../services/attackService";
import { fortify } from "../../services/fortifyService";
import DiceBox from "@3d-dice/dice-box-threejs";
import {Alert} from "../Alert";
import GameOverDialog from "../dialogs/GameOverDialog";
import {getPlayerInGameStats} from "../../services/playerService";


export default function Game() {
    const queryClient = useQueryClient();
    const {id} = useParams<{ id: string }>()
    const gameId = parseInt(id!);
    const {isLoading, isError, data: game} = useQuery(["game", gameId], () => getGameState(gameId),
        {refetchInterval: 2000});
    const {username} = useContext(AccessContext);
    const [attackerDiceBox, setAttackerDiceBox] = useState<any>();
    const [defenderDiceBox, setDefenderDiceBox] = useState<any>();
    const [state, dispatch] = useReducer(GameInteractionStateReducer, {
        isOpenErrorToast: false,
        errorToastMessage: "",
        isOpenCardSelector: false,
        selectedStartTerritory: null,
        selectedEndTerritory: null,
        troopState: {
            isOpen: false,
            buttonText: "",
            maxTroops: 0
        },
        attackableTerritoryNames: [],
        fortifiableTerritoryNames: []
    });

    useEffect(() => {
        setTimeout(() => {
            const ATTACK_DICE_BACKGROUND_COLOR = "#700808"
            const DEFEND_DICE_BACKGROUND_COLOR = "#000000"
            const DICE_FOREGROUND_COLOR = "#ffffff"

            const attackerDB = new DiceBox("#attacker-dice-box", {
                    theme_customColorset: {
                        background: ATTACK_DICE_BACKGROUND_COLOR,
                        foreground: DICE_FOREGROUND_COLOR
                    }
                }
            );
            attackerDB.initialize();
            setAttackerDiceBox(attackerDB);

            const defenderDB = new DiceBox("#defender-dice-box", {
                    theme_customColorset: {
                        background: DEFEND_DICE_BACKGROUND_COLOR,
                        foreground: DICE_FOREGROUND_COLOR
                    }
                }
            );
            defenderDB.initialize();
            setDefenderDiceBox(defenderDB);
        }, 600);
    }, []);

    const handleCloseSnackbar = (event?: SyntheticEvent | Event, reason?: string) => {
        if (reason === 'clickaway') return;
        dispatch({type: GameActionType.CLOSE_ERROR_TOAST});
    };

    if (isLoading) return <Loading/>;

    if (isError || !game) {
        return <Alert message={"Game state could not be loaded"}/>;
    }

    function clearDiceBox() {
        try {
            attackerDiceBox.clearDice();
            defenderDiceBox.clearDice();
        }
        catch (error) {
            console.warn("Resetting diceboxes failed, error:")
            console.log(error)
        }
    }

    const clearDiceBoxDelayed = async () => {
        return new Promise((resolve) => {
            setTimeout(() => {
                clearDiceBox()
                resolve(true);
            }, 5000);
        });
    };

    const troopSelectorFunction = async (troops: number, action: string) => {
        if (action === "Reinforce") {
            await placeTroops(state.selectedStartTerritory!.territoryId, troops);
            await queryClient.invalidateQueries(["game", gameId]);
            dispatch({type: GameActionType.RESET});
        }
        else if (action === "Attack") {
            const attacker = state.selectedStartTerritory;
            const defender = state.selectedEndTerritory;
            if (!attacker || !defender) throw new Error("Tried attacking but attacking or defending territory was not set");

            const diceResults = await attack({gameId, attackerTerritoryName: attacker.name,
                defenderTerritoryName: defender.name, amountOfAttackers: troops});
            await queryClient.invalidateQueries(["game", gameId]);
            const newTers = await getTerritoriesWithNeighbors(gameId);
            const updatedAttacker = getTerritoryData(newTers, attacker.name)!;
            const updatedDefender = getTerritoryData(newTers, defender.name)!;

            try {
                attackerDiceBox.roll(diceResults.attackerDiceNotation);
                defenderDiceBox.roll(diceResults.defenderDiceNotation);
                clearDiceBoxDelayed();
            }
            catch (error) {
                console.warn("Dice rolls failed, error:")
                console.log(error)
            }

            if (updatedDefender.ownerId === attacker.ownerId)
                dispatch({type: GameActionType.ANNEXATION_FORTIFICATION});
            else if (!hasTerritoryEnoughTroopsToAttack(updatedAttacker))
                dispatch({type: GameActionType.RESET});
            else {
                dispatch({
                        type: GameActionType.CONTINUE_ATTACK,
                        payload: {
                            game: game,
                            selectedTerritoryName: "",
                            territories: newTers
                        }
                    }
                );
            }
        }
        else if (action === "Fortify") {
            clearDiceBox();
            await fortify(gameId, state.selectedStartTerritory!.name, state.selectedEndTerritory!.name, troops);
            await queryClient.invalidateQueries(["game", gameId]);
            dispatch({type: GameActionType.RESET});
        }
    }

    const handleExchangeCards = async (cardNames: string[]) => {
        dispatch({type: GameActionType.CLOSE_CARD_SELECTOR})
        await exchangeCards(gameId, cardNames);
        await queryClient.invalidateQueries(["game", gameId]);
    }

    const selectTerritory = async (e: any, territoryName: string) => {
        const currentPlayerInGame = game.playersInGame[game.currentPlayerIndex];
        if (currentPlayerInGame.player.username !== username) return; // check if player has turn

        // get territories: with neighbors for Attack and Fortify | without neighbors for Reinforce
        let territories = game.phase === Phases.REINFORCEMENT ?
            getAllTerritoriesFromGameState(game) :
            await getTerritoriesWithNeighbors(game.gameId);

        dispatch({
            type: game.phase,
            payload: {
                game: game,
                selectedTerritoryName: territoryName,
                territories: territories
            }
        });
    }

    const handleNextPhase = async () => {
        clearDiceBoxDelayed();
        await nextPhase(gameId);
        await queryClient.invalidateQueries(["game", gameId]);
        dispatch({type: GameActionType.RESET});
    }

    const handleNextTurn = async () => {
        clearDiceBox();
        await nextTurn(gameId);
        await queryClient.invalidateQueries(["game", gameId]);
        dispatch({type: GameActionType.RESET});
    }

    const isUserInTurnAndReinforcement = () => {
        const currentPlayer = game.playersInGame[game.currentPlayerIndex].player;
        return game.phase === Phases.REINFORCEMENT && username === currentPlayer.username;
    }

    return (
        <>
            <div id="attacker-dice-box"
                 style={{position: "fixed", height: "60vh", width: "50vw", pointerEvents: "none"}}></div>
            <div id="defender-dice-box" style={{position: "fixed", height: "60vh", left: "50vw", width: "50vw",
                pointerEvents: "none", zIndex: 10}}></div>

            <Grid container display="flex" alignItems="center" justifyItems="center">
                <Grid item xs={10}>
                    {/*Renders the game board*/}
                    <GameStateContextProvider game={game}>
                        <Board selectTerritory={selectTerritory} territories={getAllTerritoriesFromGameState(game)}
                               attackableTerritoryNames={state.attackableTerritoryNames} fortifiableTerritoryNames={state.fortifiableTerritoryNames}/>
                    </GameStateContextProvider>

                    {/*Shows information about the current player (e.g. the phase he is in)*/}
                    <div style={{display:"flex", justifyContent:"space-around", alignItems:"center"}}>
                        <Fab color="primary" style={{height: "4vw", width: "4vw"}}
                             disabled={!isUserInTurnAndReinforcement()}
                             onClick={() => dispatch({type: GameActionType.OPEN_CARD_SELECTOR})}>
                            <CardsIcon style={{fontSize: "2.5vw"}}/>
                        </Fab>
                        <CurrentPlayer nextPhase={handleNextPhase} nextTurn={handleNextTurn} currentPhase={getPhaseNumber(game.phase)}
                                       currentPlayer={game.playersInGame[game.currentPlayerIndex]}/>
                        {/*just here to make the current player center*/}
                        <div></div>
                    </div>
                </Grid>
                {/*Shows info about all the players in the game (e.g. their color)*/}
                <Grid item xs={2}>
                    {game.playersInGame.map((playerInGame) => {
                        return <PlayerFrame playerInGame={playerInGame} key={playerInGame.playerInGameId}
                                            currentPlayerName={game.playersInGame[game.currentPlayerIndex].player.username}
                                            playerInGameStats={getPlayerInGameStats(game, playerInGame)}/>
                    })}
                </Grid>
            </Grid>

            {/*Dialog component for selecting amount of troops for attack, fortify, etc.*/}
            <TroopSelector isOpen={state.troopState.isOpen} onClose={() => dispatch({type: GameActionType.RESET})}
                           onSubmit={troopSelectorFunction}
                           maxTroops={state.troopState.maxTroops}
                           confirmButtonText={state.troopState.buttonText}/>
            {/*Dialog component for selecting cards to exchange*/}
            <CardSelector isOpen={state.isOpenCardSelector} onClose={() => dispatch({type: GameActionType.CLOSE_CARD_SELECTOR})}
                          onSubmit={handleExchangeCards} game={game}/>
            {/*Dialog component for showing the game over screen*/}
            <GameOverDialog game={game} isOpen={game.endTime !== null}/>
            {/*Temporary message for displaying user errors (ex: when user tries to attack from territory with only 1 troop)*/}
            <Snackbar open={state.isOpenErrorToast} autoHideDuration={4000} onClose={handleCloseSnackbar}>
                <AlertMui onClose={handleCloseSnackbar} severity="error" sx={{ width: '100%' }}>
                    {state.errorToastMessage}
                </AlertMui>
            </Snackbar>
        </>
    );
}
