import {useMutation, useQuery, useQueryClient} from "react-query";
import {equipShopItem, getProfile} from "../services/playerService";

export function useProfile() {
    const queryClient = useQueryClient();

    const {
        isLoading,
        isError: isErrorFetching,
        data: profile
    } = useQuery(['profile'], () => getProfile())

    const {
        mutate: equipItemMutation,
        isError: isErrorEquiping,
    } = useMutation((shopItemId: number) => equipShopItem(profile.id, shopItemId), {
        onSuccess: () => {
            queryClient.invalidateQueries(['profile'])
        }
    })

    const isError = isErrorFetching || isErrorEquiping;

    return {
        isLoading,
        isError,
        profile,
        equipItemMutation
    }
}
