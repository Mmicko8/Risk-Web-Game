import {useQuery} from "react-query";
import {getLeaderboard} from "../services/playerService";

export function useLeaderboard() {
    const {
        isLoading,
        isError,
        data: players
    } = useQuery(['leaderboard'], () => getLeaderboard())

    return {
        isLoading,
        isError,
        players
    }
}