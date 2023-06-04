import {useMutation ,useQuery, useQueryClient} from "react-query";
import {
    acceptFriendRequest,
    declineFriendRequest,
    getFriendRequests,
    getFriends,
    removeFriendRequest
} from "../services/playerService";

export function useFriends() {
    const queryClient = useQueryClient();

    const {
        isLoading: loadingFriends,
        isError: isErrorFriends,
        data: friends,
    } = useQuery(['friends'], () => getFriends(), {
        refetchInterval: 8000});

    const {
        isLoading: loadingRequests,
        isError: isErrorRequests,
        data: requests
    } = useQuery(['requests'], () => getFriendRequests(), {
        refetchInterval: 8000});

    const {
        isError: errorAcceptingRequest,
        mutate: acceptRequest
    } = useMutation((username: string) => acceptFriendRequest(username),
        {onSuccess: () => {
                queryClient.invalidateQueries(['friends'])
                queryClient.invalidateQueries(['requests'])
            }})

    const {
        isError: errorDecliningRequest,
        mutate: declineRequest
    } = useMutation((username: string) => declineFriendRequest(username),
        {onSuccess: () => {
                queryClient.invalidateQueries(['requests'])
            }})

    const {
        isError: errorRemovingFriend,
        mutate: removeFriend
    } = useMutation((username: string) => removeFriendRequest(username),
        {onSuccess: () => {
                queryClient.invalidateQueries(['friends'])
            }})

    const isError = isErrorFriends || isErrorRequests || errorAcceptingRequest || errorDecliningRequest || errorRemovingFriend
    const isLoading = loadingFriends || loadingRequests

    return {
        isLoading,
        isError,
        friends,
        requests,
        acceptRequest,
        declineRequest,
        removeFriend
    }
}