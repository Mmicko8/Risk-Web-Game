export type Player = {
    id: number;
    username: string;
    email: string;
    gamesWon: number;
    profilePicture: string;
    title: string;
}

export type FriendRequest = Omit<Player, "id" | "email" | "gamesWon" | "profilePicture" | "title">;
export type FriendInvite = Omit<Player, "id" | "email" | "gamesWon" | "profilePicture" | "title">;