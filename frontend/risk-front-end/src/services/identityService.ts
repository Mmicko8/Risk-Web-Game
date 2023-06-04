import axios from "axios";

export function register (username: string, email: string, password: string) {
    axios.post("/api/player/register", {username: username, email: email, password: password})
}

export function login (username: string, password: string) {
    return axios.post("/api/player/login", {username: username, password: password})
}

export function forgotPassword(username: string) {
    return axios.post(`/api/password/forgot/${username}`)
}

export function resetPassword(username: string, password: string, token: string) {
    return axios.put(`/api/password/reset`, {username: username, password: password, token: token})
}

export function resetPasswordRequest() {
    return axios.post('/api/password/resetRequest')
}

export function editPlayerUsername (id: number, username: string ) {
    return axios.put("/api/player/edit/username", {id, username})
}
