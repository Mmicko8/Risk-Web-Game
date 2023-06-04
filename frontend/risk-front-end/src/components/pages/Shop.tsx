import {useShopItems} from "../../hooks/useShopItems";
import * as React from "react";
import {ShopItemModel} from "../../model/ShopItemModel";
import {CircularProgress, Grid} from "@mui/material";
import {ShopItem} from "../ShopItem";
import Container from "@mui/material/Container";
import {Alert} from "../Alert";
import Typography from "@mui/material/Typography";

export enum ShopActionType {
    PURCHASE= "PURCHASE",
    EQUIP = "EQUIP"
}
interface ShopItemsProps {
    shopItems: ShopItemModel[];
    emptyMessage: string;
    actionType: ShopActionType;
    action: (id: number) => void;
}

export function ShopItems({shopItems, emptyMessage, actionType, action}: ShopItemsProps) {
    const profilePictures = shopItems.filter((item: ShopItemModel) => item.itemCategory === "PROFILE_PICTURE");
    const titles = shopItems.filter((item: ShopItemModel) => item.itemCategory === "TITLE");

    return <>
        <Typography variant="h4" sx={{marginTop: "30px"}}>Profile Pictures</Typography>
        {profilePictures.length === 0 ? <p>{emptyMessage}</p>
            :
            <Grid container sx={{marginTop: "20px", gap: "10px"}}>
                {profilePictures.map((item: ShopItemModel) => (
                    <Grid item key={item.shopItemId}>
                        <ShopItem item={item} actionType={actionType} action={action}/>
                    </Grid>)
                )
                }
            </Grid>
        }
        <Typography variant="h4" sx={{marginTop: "30px"}}>Titles</Typography>
        {titles.length === 0 ? <p>{emptyMessage}</p>
            :
            <Grid container sx={{marginTop: "20px", gap: "10px"}}>
                {titles.map((item: ShopItemModel) => (
                    <Grid item key={item.shopItemId}>
                        <ShopItem item={item} actionType={actionType} action={action}/>
                    </Grid>)
                )
                }
            </Grid>
        }
    </>;
}

export function Shop() {
    const {isLoading, isError, errorMessage, shopItems, loyaltyPoints, buyShopItemMutation} = useShopItems();
    const emptyMessage = "You already own every item of this type, but don't worry more items are on the way!"

    function buyShopItem(itemId: number) {
        buyShopItemMutation(itemId);
    }

    if (isLoading) {
        return <CircularProgress sx={{position: "fixed", top: "50%", left: "50%"}}/>
    }
    return (
        <>
            {isError ? <Alert message={errorMessage}/> : ""}
            <Container sx={{marginY: "80px"}}>
                <Typography variant="h3" component="h1">Shop</Typography>
                <p>Remaining loyalty points: {loyaltyPoints}</p>
                {shopItems.length === 0 ?
                    "You already own everything, but don't worry more items are on the way!"
                    :
                    <ShopItems shopItems={shopItems} emptyMessage={emptyMessage} actionType={ShopActionType.PURCHASE} action={buyShopItem}/>
                }
            </Container>
        </>
    )
}
