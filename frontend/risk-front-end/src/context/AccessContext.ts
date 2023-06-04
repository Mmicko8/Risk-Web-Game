import {createContext} from "react";

export interface IUserContext {
    accessToken: string | null;
    username: string | null;
    setAccessToken: (token: string) => void;
    setUsername: (username: string) => void;
    removeUsername: () => void;
    removeAccessToken: () => void;
}

export default createContext<IUserContext>(
    {
        accessToken: null,
        username: null,
        setAccessToken: () => {},
        setUsername: () => {},
        removeUsername: () => {},
        removeAccessToken: () => {},
    });
