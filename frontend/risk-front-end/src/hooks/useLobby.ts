import {useMutation, useQuery, useQueryClient} from "react-query";
import {addAiToGame, getLobby} from "../services/lobbyService";

export function useLobby(id: string) {
    const queryClient = useQueryClient();

    const {
        isLoading,
        isError,
        data: lobby,
    } = useQuery(['lobby', id], () => getLobby(id),
        {refetchInterval: 3000});

    const {
        mutate: addAi,
        isError: errorAddingAi,
    } = useMutation(() => addAiToGame(id), {
        onSuccess: () => {
            queryClient.invalidateQueries(['lobby', id])
        }
    })

    return {
        isLoading,
        isError,
        lobby,
        addAi,
        errorAddingAi
    }
}