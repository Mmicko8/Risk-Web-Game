import {Card, CardContent, CardMedia, Stack, Typography} from "@mui/material";
import {itemNameToImage} from "../services/shopItemService";
import Button from "@mui/material/Button";
import React from "react";
import {Player} from "../model/player/Player";
import {FriendActionType} from "./pages/Friends";

interface FriendProps {
    player: Player;
    actionTypes: FriendActionType[];
    handleFriendAction: (actionType: FriendActionType, username: string) => void;
}

export default function Friend({player, actionTypes, handleFriendAction}:FriendProps) {
    return (
        <Card sx={{display: "flex", justifyContent: "space-between", margin: "10px"}}>
            <CardContent sx={{display: "flex", flexDirection: "column", justifyContent: "space-between", alignItems: "start"}}>
                <div>
                    <Typography variant="h6" component="div">
                        {player.username}
                    </Typography>
                    {player.title === null ? ""
                        :
                        <Typography sx={{marginY: "8px", fontStyle: "italic"}}>"{player.title}"</Typography>}
                </div>
                <Stack direction="row" spacing={2}>
                    {actionTypes.map((actionType, index) => {
                        return <Button key={index} size="small" variant="contained"
                                       color={actionType === FriendActionType.ACCEPT ? "success" : "error"}
                                       onClick={() => handleFriendAction(actionType, player.username)}>
                            {actionType}
                        </Button>
                    })}
                </Stack>
            </CardContent>
            <CardMedia
                component="img"
                sx={{width: 130}}
                image={itemNameToImage(player.profilePicture)}
                alt={player.profilePicture}
            />
        </Card>
    )
}