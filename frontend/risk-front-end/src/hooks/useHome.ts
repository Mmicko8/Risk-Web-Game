import {useQuery} from "react-query";
import {getFirstNLobbies, getJoinedLobbies} from "../services/lobbyService";
import {getActiveGames} from "../services/gameService";

export function useHome(amount: number, username: string | null) {
    const {
        isLoading: isLoadingLobbies,
        isError: isErrorLobbies,
        data: lobbies
    } = useQuery(['lobbies'], () => getFirstNLobbies(amount))

    const {
        isLoading: loadingJoined,
        isError: errorJoined,
        data: joinedLobbies
    } = useQuery(['joinedLobbies', username], () => getJoinedLobbies(username));

    const {
        isLoading: loadingGames,
        isError: errorGettingGames,
        data: activeGames
    } = useQuery(['activeGames', username], () => getActiveGames(username));

    const isLoading = isLoadingLobbies || loadingJoined || loadingGames
    const isError = isErrorLobbies || errorJoined || errorGettingGames

    return {
        isLoading,
        isError,
        lobbies,
        joinedLobbies,
        activeGames
    }
}