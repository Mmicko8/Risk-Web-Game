import axios from "axios";
import {GameModel} from "../model/game/GameModel";
import {PlayerInGame} from "../model/player/PlayerInGame";
import {getAllTerritoriesFromGameState} from "./territoryService";

export async function getProfile() {
    const shopItem = await axios.get(`/api/player/profile`);
    return shopItem.data;
}

export async function getLoyaltyPoints() {
    const profileInfo = await getProfile()
    return profileInfo.loyaltyPoints;
}

export async function getLeaderboard() {
    const lobby = await axios.get(`/api/player/leaderboard`);
    return lobby.data;
}

export async function equipShopItem(playerId: number, shopItemId: number) {
    return await axios.put('/api/player/equip', {playerId, shopItemId});
}

export function getPlayerInGameStats(game: GameModel, playerInGame: PlayerInGame) {
    const territories = getAllTerritoriesFromGameState(game)
    let amountOfTerritories = 0, amountOfTroops = 0;
    for (const territory of territories) {
        if (territory.ownerId === playerInGame.playerInGameId) {
            amountOfTroops += territory.troops;
            amountOfTerritories++;
        }
    }
    return {
        amountOfTroops,
        amountOfTerritories,
        amountOfCards: playerInGame.playerCards.length
    }
}

export async function getFriends() {
    const friends = await axios.get('/api/friend')
    return friends.data;
}

export async function getFriendRequests() {
    const friendRequests = await axios.get('/api/friend/requests')
    return friendRequests.data
}

export async function sendFriendRequestCall(username: string) {
    return await axios.put(`/api/friend/send/${username}`)
}

export async function acceptFriendRequest(username: string) {
    return await axios.put(`/api/friend/accept/${username}`)
}

export async function declineFriendRequest(username: string) {
    return await axios.put(`/api/friend/decline/${username}`)
}

export async function removeFriendRequest(username: string) {
    return await axios.delete(`/api/friend/remove/${username}`)
}