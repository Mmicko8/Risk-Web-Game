import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import Grid from "@mui/material/Grid";
import {useFriends} from "../../hooks/useFriends";
import Loading from "../Loading";
import {Alert} from "../Alert";
import Friend from "../Friend";
import {FriendRequest, Player} from "../../model/player/Player";
import Divider from "@mui/material/Divider";
import Button from "@mui/material/Button";
import FriendRequestDialog from "../dialogs/FriendRequestDialog";
import {SyntheticEvent, useState} from "react";
import {sendFriendRequestCall} from "../../services/playerService";
import AlertMui from "@mui/material/Alert";
import {Snackbar} from "@mui/material";

export enum FriendActionType {
    REMOVE = "REMOVE",
    ACCEPT = "ACCEPT",
    DECLINE = "DECLINE"
}

export default function Friends() {
    const {isLoading, isError, friends, requests, acceptRequest, declineRequest, removeFriend} = useFriends()
    const [isSendFriendRequestOpen, setIsSendFriendRequestOpen] = useState(false)
    const [isFriendRequestSnackbarOpen, setIsFriendRequestSnackbarOpen] = useState(false)

    if (isLoading) {
        return <Loading/>
    }

    if (isError || !friends || !requests) {
        return <Alert message={"Error loading social hub"}/>
    }

    const handleCloseSnackbar = (event?: SyntheticEvent | Event, reason?: string) => {
        if (reason === 'clickaway') return;
        setIsFriendRequestSnackbarOpen(false)
    };

    function sendFriendRequest(data: FriendRequest) {
        sendFriendRequestCall(data.username)
            .then(() => {
                setIsFriendRequestSnackbarOpen(true)
            })
    }

    function handleFriendAction(actionType: FriendActionType, username: string) {
        if (actionType === FriendActionType.ACCEPT) {
            acceptRequest(username)
        }
        if (actionType === FriendActionType.DECLINE) {
            declineRequest(username)
        }
        if (actionType === FriendActionType.REMOVE) {
            removeFriend(username)
        }
    }

    return (
        <Container component="main" maxWidth="md" sx={{mt: "30px"}}>
            <Typography component="h1" variant="h3" textAlign="center" mb="10px">Social hub</Typography>
            <Divider/>
            <Grid container mt="5px" spacing={4}>
                <Grid item xs={6} justifyContent="center">
                    <Typography component="h2" variant="h4" textAlign="center">Friends</Typography>
                    <Button fullWidth variant="contained" onClick={() => setIsSendFriendRequestOpen(true)}>
                        Add friend
                    </Button>
                    {friends.length < 1 ? <Typography textAlign="center" mt="10px">You don't have any friends yet,
                        try adding a friend with the 'Add friend' button</Typography>
                        :
                        friends.map((friend: Player) => {
                            return <Friend key={friend.id} player={friend} actionTypes={[FriendActionType.REMOVE]}
                                           handleFriendAction={handleFriendAction}/>
                        })
                    }
                </Grid>
                <Grid item xs={6} justifyContent="center">
                    <Typography component="h2" variant="h4" textAlign="center">Friend requests</Typography>
                    {requests.length < 1 ? <Typography textAlign="center">You currently have no friend requests</Typography>
                        :
                        requests.map((friend: Player) => {
                            return <Friend key={friend.id} player={friend}
                                           actionTypes={[FriendActionType.ACCEPT, FriendActionType.DECLINE]}
                                           handleFriendAction={handleFriendAction}/>
                        })
                    }
                </Grid>
            </Grid>
            <FriendRequestDialog isOpen={isSendFriendRequestOpen} onClose={() => setIsSendFriendRequestOpen(false)}
                                 onSubmit={sendFriendRequest}/>
            <Snackbar open={isFriendRequestSnackbarOpen} autoHideDuration={4000} onClose={handleCloseSnackbar}>
                <AlertMui onClose={handleCloseSnackbar} severity="success" sx={{ width: '100%' }}>
                    Friend request sent!
                </AlertMui>
            </Snackbar>
        </Container>
    )
}