import {useMutation, useQuery, useQueryClient} from "react-query";
import {buyShopItem, getShopItems} from "../services/shopItemService";
import {getLoyaltyPoints} from "../services/playerService";
import {AxiosError, AxiosResponse} from "axios";

export function useShopItems() {
    const queryClient = useQueryClient();

    const {
        isLoading: isLoadingItems,
        isError: isErrorItems,
        data: shopItems,
    } = useQuery(['shopItems'], () => getShopItems());

    const {
        isLoading: isLoadingLoyalty,
        isError: isErrorLoyalty,
        data: loyaltyPoints,
    } = useQuery(['loyaltyPoints'], () => getLoyaltyPoints());

    const {
        mutate,
        isError: isErrorBuying,
        error: buyError
    } = useMutation<AxiosResponse, AxiosError, number>(
        (itemId: number) => {
            return buyShopItem(itemId);
        },
        {
            onSuccess: () => {
                queryClient.invalidateQueries(["shopItems"]);
                queryClient.invalidateQueries(["loyaltyPoints"]);
            }
        }
    );

    const isError = isErrorItems || isErrorLoyalty || isErrorBuying;
    const isLoading = isLoadingItems || isLoadingLoyalty;
    let errorMessage = "";
    if (isErrorItems) errorMessage = "Error loading the shop items";
    else if (isErrorLoyalty) errorMessage = "Error loading the loyalty points"
    else if (isErrorBuying) {
        // @ts-ignore message exists but don't know how to tell typescript that
        if (buyError?.response?.data?.message === "You don't have enough loyalty points to buy this item")
            errorMessage = "You don't have enough loyalty points to buy this item"
        else
            errorMessage = "Error buying item"
    }

    return {
        isLoading,
        isError,
        errorMessage,
        shopItems,
        loyaltyPoints,
        buyShopItemMutation: mutate
    }
}