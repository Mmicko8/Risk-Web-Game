import {TerritoryModel} from "../model/territory/TerritoryModel";
import {GameModel} from "../model/game/GameModel";
import {Phases} from "../services/gameService";
import {PlayerInGame} from "../model/player/PlayerInGame";
import {getAttackableTerritoryNames, getMaxTroopsForAttack} from "../services/attackService";
import {getFortifiableTerritoryNames} from "../services/fortifyService";
import {getTerritoryData} from "../services/territoryService";

export const GameActionType = {
    REINFORCEMENT: Phases.REINFORCEMENT,
    ATTACK: Phases.ATTACK,
    FORTIFICATION: Phases.FORTIFICATION,
    RESET: "RESET",
    CLOSE_ERROR_TOAST: "CLOSE ERROR TOAST",
    ANNEXATION_FORTIFICATION: "ANNEXATION FORTIFICATION",
    CLOSE_CARD_SELECTOR: "CLOSE CARD SELECTOR",
    OPEN_CARD_SELECTOR: "OPEN CARD SELECTOR",
    CONTINUE_ATTACK: "CONTINUE ATTACK"
}

interface TroopSelectorState {
    isOpen: boolean;
    buttonText: string;
    maxTroops: number;
}

interface InteractionGameState {
    isOpenErrorToast: boolean;
    errorToastMessage: string;
    isOpenCardSelector: boolean;
    selectedStartTerritory: TerritoryModel | null;
    selectedEndTerritory: TerritoryModel | null;
    troopState: TroopSelectorState;
    attackableTerritoryNames: string[];
    fortifiableTerritoryNames: string[];
}

interface GameAction {
    type: string;
    payload?: GameActionPayload;
}

interface GameActionPayload {
    game: GameModel; // BEWARE: contains territories BUT WITHOUT neighbors, use 'territories' from payload instead
    selectedTerritoryName: string;
    territories: TerritoryModel[]; // contains neighbors when necessary (e.g. attack or fortify, NOT for reinforce)
}


// returns new state if action is of a payloadless type, otherwise returns null so that actions with payload can be handled by gameInteractionStateReducer
function gameInteractionStateReducerWithoutPayload(state: InteractionGameState, action: GameAction) {
    switch (action.type) {
        case GameActionType.CLOSE_ERROR_TOAST:
            return {...state, isOpenErrorToast: false};

        case GameActionType.ANNEXATION_FORTIFICATION:
            return {...state,
                troopState: {
                    maxTroops: state.selectedStartTerritory!.troops - 1,
                    buttonText: "Fortify",
                    isOpen: true,
                }
            };

        case GameActionType.RESET:
            return {...state,
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
            };

        case GameActionType.OPEN_CARD_SELECTOR:
            return {...state, isOpenCardSelector: true};

        case GameActionType.CLOSE_CARD_SELECTOR:
            return {...state, isOpenCardSelector: false};

        default:
            return state;
    }
}

function reinforcementReducer(state: InteractionGameState, selectedTerritory: TerritoryModel, currentPlayer: PlayerInGame) {
    // player doesn't own selected territory => cant reinforce
    if (selectedTerritory.ownerId !== currentPlayer.playerInGameId) return state;

    // check if player has enough troops to be able to reinforce
    if (currentPlayer.remainingTroopsToReinforce < 1) return {...state,
        errorToastMessage: "No more troops remaining!",
        isOpenErrorToast: true
    }

    return {...state,
        selectedStartTerritory: selectedTerritory,
        troopState: {
            maxTroops: currentPlayer.remainingTroopsToReinforce,
            buttonText: "Reinforce",
            isOpen: true
        }
    }
}

function attackReducer(state: InteractionGameState, territories: TerritoryModel[], selectedTerritory: TerritoryModel, currentPlayer: PlayerInGame) {
    // check if selected territory is your own territory, to attack from
    if (selectedTerritory.ownerId === currentPlayer.playerInGameId) {
        // can't attack if territory has less than 2 troops
        if (selectedTerritory.troops < 2) return {...state,
            errorToastMessage: "Territory must at least have 2 troops to attack!",
            isOpenErrorToast: true,
            attackableTerritoryNames: []
        };

        return {...state,
            selectedStartTerritory: selectedTerritory,
            attackableTerritoryNames: getAttackableTerritoryNames(territories, selectedTerritory)
        };
    }

    // if selected territory is not your own territory it will select it for attack, unless startTerritory is null
    if (!state.selectedStartTerritory) return state;

    // check if selected territory to attack is attackable neighbor
    const attackableTerritoryNames = getAttackableTerritoryNames(territories, state.selectedStartTerritory);
    if (!attackableTerritoryNames.includes(selectedTerritory.name)) return state;

    return {...state,
        selectedEndTerritory: selectedTerritory,
        troopState: {
            maxTroops: getMaxTroopsForAttack(state.selectedStartTerritory.troops),
            buttonText: "Attack",
            isOpen: true
        }

    }
}

function fortificationReducer(state: InteractionGameState, territories: TerritoryModel[], selectedTerritory: TerritoryModel, currentPlayer: PlayerInGame) {
    if (selectedTerritory.ownerId !== currentPlayer.playerInGameId) return state;

    if (!state.selectedStartTerritory) {
        if (selectedTerritory.troops < 2) return {...state,
            errorToastMessage: "Territory must at least have 2 troops to fortify!",
            isOpenErrorToast: true,
            fortifiableTerritoryNames: []
        };

        return {...state,
            selectedStartTerritory: selectedTerritory,
            fortifiableTerritoryNames: getFortifiableTerritoryNames(territories, selectedTerritory),
        }
    }
    // if selected territory is not your own territory it will select it for fortification

    // check if selected territory to attack is fortifiable neighbor
    const fortifiableTerritoryNames = getFortifiableTerritoryNames(territories, state.selectedStartTerritory);
    if (!fortifiableTerritoryNames.includes(selectedTerritory.name)) return state;

    return {...state,
        selectedEndTerritory: selectedTerritory,
        troopState: {
            maxTroops: state.selectedStartTerritory.troops - 1,
            buttonText: "Fortify",
            isOpen: true
        }
    }
}

function attackContinueReducer(state: InteractionGameState, territories: TerritoryModel[]) {
    const updatedStartTerritory = getTerritoryData(territories, state.selectedStartTerritory!.name)!;
    return {...state,
        selectedStartTerritory: updatedStartTerritory,
        selectedEndTerritory: getTerritoryData(territories, state.selectedEndTerritory!.name),
        troopState: {...state.troopState, maxTroops: getMaxTroopsForAttack(updatedStartTerritory.troops)}
    }
}

export function GameInteractionStateReducer(state: InteractionGameState, action: GameAction) {
    if (!action.payload) return gameInteractionStateReducerWithoutPayload(state, action);
    const pl = action.payload;
    const currentPlayer = pl.game.playersInGame[pl.game.currentPlayerIndex];

    if (action.type === GameActionType.CONTINUE_ATTACK)
        return attackContinueReducer(state, pl.territories);

    const selectedTerritory = getTerritoryData(pl.territories, pl.selectedTerritoryName);
    if (!selectedTerritory) return state;

    switch (action.type) {
        case GameActionType.REINFORCEMENT:
            return reinforcementReducer(state, selectedTerritory, currentPlayer);

        case GameActionType.ATTACK:
            return attackReducer(state, pl.territories, selectedTerritory, currentPlayer);

        case GameActionType.FORTIFICATION:
            return fortificationReducer(state, pl.territories, selectedTerritory, currentPlayer)

        default: return state;
    }
}