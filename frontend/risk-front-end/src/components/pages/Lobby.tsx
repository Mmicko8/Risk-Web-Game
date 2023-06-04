import {useNavigate, useParams} from "react-router-dom";
import {useLobby} from "../../hooks/useLobby";
import {Stack, Chip} from "@mui/material";
import {FriendInvite, Player} from "../../model/player/Player";
import Box from "@mui/material/Box";
import Avatar from "@mui/material/Avatar";
import Typography from "@mui/material/Typography";
import StarRateIcon from '@mui/icons-material/StarRate';
import Container from "@mui/material/Container";
import * as React from "react";
import PersonIcon from '@mui/icons-material/Person';
import AccessTimeIcon from '@mui/icons-material/AccessTime';
import Button from "@mui/material/Button";
import {useContext, useState} from "react";
import AccessContext from "../../context/AccessContext";
import {Alert} from "../Alert";
import {invitePlayerWithEmail, invitePlayerWithUsername, startGameCall} from "../../services/lobbyService";
import {getAvatar} from "../../services/utilsService";
import EmailInvitationDialog from "../dialogs/EmailInvitationDialog";
import {EmailInvitation} from "../../model/lobby/EmailInvitation";
import Loading from "../Loading";
import {useFriends} from "../../hooks/useFriends";
import FriendEmailInviteDialog from "../dialogs/FriendEmailInviteDialog";

export function Lobby() {
    const navigate = useNavigate();
    const [isFriendInviteDialogOpen, setIsFriendInviteDialogOpen] = useState(false);
    const [isInviteDialogOpen, setIsInviteDialogOpen] = useState(false);
    const {username} = useContext(AccessContext);
    const {friends, isLoading: isLoadingFriends} = useFriends()
    const {id} = useParams<{ id: string }>();
    const {isLoading, isError, lobby, addAi, errorAddingAi} = useLobby(id!);

    if (!username) {
        navigate("/sign_in")
    }

    if (isLoading || isLoadingFriends) {
        return <Loading/>
    }
    if (isError || errorAddingAi) {
        return <Alert message={"Error loading the lobby"}/>
    }
    if (lobby.closed) {
        return <Alert severity={"info"} message={"Lobby is already closed"}/>
    }

    function startGame() {
        startGameCall(lobby.lobbyId).then((response) => {
            navigate(`/game/${response.data}`)
        }).catch(() => {
            //Other way of showing warning?
            console.warn("Something went wrong starting the game");
        })
    }

    function addAI() {
        addAi()
    }

    function invitePlayer(data: EmailInvitation) {
        if (data.type === 'username') {
            invitePlayerWithUsername(data.usernameOrEmail, parseInt(id!))
        }
        if (data.type === 'email') {
            invitePlayerWithEmail(data.usernameOrEmail, parseInt(id!))
        }
    }

    function inviteFriend(data: FriendInvite) {
        invitePlayerWithUsername(data.username, parseInt(id!))
    }

    return (
        <Container component="main" maxWidth="xl">
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
                <Typography component="h1" variant="h5" fontFamily="Courier" fontWeight="bolder">
                    Lobby: {lobby.lobbyId}
                </Typography>
                <Stack direction="row" spacing={4} sx={{mt: "20px"}}>
                    <Chip icon={<PersonIcon/>} color="success" label={`Amount of players: ${lobby.maxPlayers}`}/>
                    <Chip icon={<AccessTimeIcon/>} color="success" label={`Round timer: ${lobby.timer}s`}/>
                    {lobby.host.username === username?
                        <>
                            <Button disabled={lobby.players.length < 2} variant="contained" data-testid="StartGame"
                                    onClick={() => startGame()}>
                                Start game
                            </Button>
                            <Button disabled={lobby.players.length >= lobby.maxPlayers} variant="contained" data-testid="AddAi"
                                    onClick={() => addAI()}>
                                Add AI
                            </Button>
                        </>
                        :""}
                    <Button disabled={lobby.players.length >= lobby.maxPlayers} variant="contained" onClick={() => setIsInviteDialogOpen(true)}>
                        Send email invitation
                    </Button>
                    <Button disabled={lobby.players.length >= lobby.maxPlayers || friends.length <= 0} variant="contained"
                            onClick={() => setIsFriendInviteDialogOpen(true)}>
                        Send email invitation to friend
                    </Button>
                </Stack>
                <div style={{display: "flex", flexDirection: "row"}}>
                    {lobby.players.map((player: Player, index: number) => {
                        return <Box key={index} className="playerFrameBox" sx={{borderRadius: 3, width:"15vw"}}>
                            <Avatar sx={{width: "6vw", height: "6vw", margin:"0.5vw"}}
                                    src={getAvatar(player.profilePicture)}/>
                            <Stack direction="column" spacing={2}>
                                {lobby.host.username === player.username? <Chip icon={<StarRateIcon/>} color="primary" label="Host"/>: ""}
                                <Typography>{player.username}</Typography>
                            </Stack>
                        </Box>
                    })}
                    {[...Array(lobby.maxPlayers - lobby.players.length)].map((value, index: number) => {
                        return <Box key={index} className="playerFrameBox" sx={{borderRadius: 3, width:"15vw"}}>
                            <Avatar sx={{width: "6vw", height: "6vw", margin:"0.5vw"}}/>
                            <Typography>Waiting for player...</Typography>
                        </Box>
                    })}
                </div>
            </Box>
            <FriendEmailInviteDialog isOpen={isFriendInviteDialogOpen} onClose={() => setIsFriendInviteDialogOpen(false)}
                                     onSubmit={inviteFriend} friends={friends}/>
            <EmailInvitationDialog isOpen={isInviteDialogOpen} onClose={() => setIsInviteDialogOpen(false)} onSubmit={invitePlayer}/>
        </Container>
    )
}