export type CreateLobbyData = {
    username: string;
    amountOfPlayers: number;
    timer: number;
}

export type CreateLobbyDataNoUsername = Omit<CreateLobbyData, "username">