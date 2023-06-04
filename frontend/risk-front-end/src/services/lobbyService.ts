import axios from "axios";
import {CreateLobbyData} from "../model/lobby/CreateLobbyData";

export const homeActions = {
    GO_TO: "Go to lobby",
    JOIN: "Join lobby"
}

export async function getLobby (id: string) {
    const lobby = await axios.get(`/api/lobby/${id}`);
    return lobby.data;
}

export async function createLobbyCall (createLobbyData: CreateLobbyData) {
    const response = await axios.post("/api/lobby/create",
        {username: createLobbyData.username, maxPlayers: createLobbyData.amountOfPlayers, timer: createLobbyData.timer});
    return response.data;
}

export async function getFirstNLobbies(n: number) {
    let lobbies;
    lobbies = await axios.get(`/api/lobby/openLobbies/${n}`);
    return lobbies.data;
}

export async function getJoinedLobbies(username: string | null) {
    if (!username) return null;
    const response = await axios.get(`/api/lobby/joinedNotStartedLobbies`);
    return response.data;
}

export function joinLobby(lobbyId: number) {
    return axios.put(`/api/lobby/joinLobby/${lobbyId}`)
}

export function startGameCall(lobbyId: number) {
    return axios.post(`/api/game/startGame/lobby/${lobbyId}`)
}

export function addAiToGame(lobbyId: string) {
    return axios.put(`/api/lobby/${lobbyId}/addAi`)
}

export function invitePlayerWithUsername(username: string, lobbyId: number) {
    axios.post('/api/invite/emailInviteWithUsername', {username: username, lobbyId: lobbyId})
}

export function invitePlayerWithEmail(email: string, lobbyId: number) {
    axios.post('/api/invite/emailInviteWithEmail', {email: email, lobbyId: lobbyId})
}