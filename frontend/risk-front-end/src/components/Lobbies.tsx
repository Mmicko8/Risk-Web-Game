import {Box, Chip, Stack, Avatar} from "@mui/material";
import {Lobby} from "../model/lobby/Lobby";
import {styled} from "@mui/material/styles";
import Paper from "@mui/material/Paper";
import GroupsIcon from '@mui/icons-material/Groups';
import StarRateIcon from '@mui/icons-material/StarRate';
import * as React from "react";
import Button from "@mui/material/Button";
import {useNavigate} from "react-router-dom";
import {homeActions, joinLobby} from "../services/lobbyService";
import Typography from "@mui/material/Typography";
import {useContext} from "react";
import AccessContext from "../context/AccessContext";

interface OpenLobbiesProps {
    lobbies: Lobby[],
    action: string
}

export const Item = styled(Paper)(({theme}) => ({
    backgroundColor: theme.palette.mode === 'dark' ? '#1A2027' : 'rgba(143,143,143,0.63)',
    ...theme.typography.body2,
    padding: theme.spacing(1),
    textAlign: 'center',
    color: theme.palette.text.secondary,
}));

export function Lobbies({lobbies, action}: OpenLobbiesProps) {
    const navigate = useNavigate()
    const {username} = useContext(AccessContext)

    function buttonClick(lobbyId: number) {
        if (action === homeActions.GO_TO) {
            navigate(`/lobby/${lobbyId}`)
        }
        if (action === homeActions.JOIN) {
            if (!username) {
                navigate('/sign_in')
            } else {
                joinLobby(lobbyId).then(() => {
                    navigate(`/lobby/${lobbyId}`);
                }).catch(() => {
                    //Change to snackbar maybe? Or not needed?
                    console.warn("Something went wrong joining the lobby");
                })
            }
        }
    }

    const noLobbiesText = () => {
        if (action === homeActions.GO_TO) {
            return <Typography>No joined lobbies! Join a lobby or create one to play a game!</Typography>
        }
        if (action === homeActions.JOIN) {
            if (!username)
                return <Typography>No open lobbies! Sign in and create a lobby to play a game!</Typography>
            else
                return <Typography>No open lobbies! Create a lobby to play a game!</Typography>
        }
    }

    return (
        <Box sx={{width: '100%'}}>
            <Stack spacing={1}>
                {lobbies.length < 1 ? noLobbiesText() : lobbies.map((lobby: Lobby) => {
                    return <Item key={lobby.lobbyId} sx={{display: "flex", flexDirection: "row", justifyContent:"space-between", alignItems: "center"}}>
                        <Stack direction="row" spacing={1}>
                            <Chip
                                avatar={<Avatar alt="RiskIcon" src="../../riskIcon.png"/>}
                                label={`Lobby: ${lobby.lobbyId}`}
                                size="small"
                                color="success"
                            />
                            <Chip icon={<StarRateIcon/>} size="small" color="success"
                                  label={`Host: ${lobby.host.username}`}/>
                            <Chip
                                icon={<GroupsIcon/>}
                                size="small"
                                color="success"
                                label={`Players: ${lobby.players.length}/${lobby.maxPlayers}`}/>
                        </Stack>
                        <Button size="small" variant="contained"
                                disabled={lobby.players.length === lobby.maxPlayers && action === homeActions.JOIN}
                                onClick={() => buttonClick(lobby.lobbyId)}>
                            {action === homeActions.JOIN && lobby.players.length === lobby.maxPlayers?
                            "Lobby full" : action}
                        </Button>
                    </Item>
                })}
            </Stack>
        </Box>
    )
}