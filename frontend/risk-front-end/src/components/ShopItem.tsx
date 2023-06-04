import {Card, CardContent, CardMedia, Typography} from "@mui/material";
import React from "react";
import {ShopItemModel} from "../model/ShopItemModel";
import Button from "@mui/material/Button";
import {capitalizeItemCategory, itemNameToImage} from "../services/shopItemService";
import {ShopActionType} from "./pages/Shop";

interface ShopItemProps {
    item: ShopItemModel;
    actionType: string;
    action: (id: number) => void;
}

export function ShopItem({item, actionType, action}: ShopItemProps) {
    return (
        <Card sx={{width: "340px", display: "flex", justifyContent: "space-between"}}>
            <CardContent sx={{display: "flex", flexDirection: "column", justifyContent: "space-between", alignItems: "start"}}>
                <div>
                    <Typography sx={{fontSize: 14}} color="text.secondary" gutterBottom>
                        {capitalizeItemCategory(item.itemCategory)}
                    </Typography>
                    <Typography variant="h5" component="div">
                        {item.name}
                    </Typography>
                    {actionType === ShopActionType.PURCHASE ?
                        <Typography sx={{mb: 1.5}} color="text.secondary">$
                            {item.price}
                        </Typography>
                        :
                        ""
                    }
                </div>
                <Button onClick={() => action(item.shopItemId)}>{actionType}</Button>
            </CardContent>
            {item.itemCategory === 'PROFILE_PICTURE' ?
                <CardMedia
                    component="img"
                    sx={{width: 151}}
                    image={itemNameToImage(item.name)}
                    alt={item.name}
                />
                :
                ""
            }
        </Card>
    )
}
