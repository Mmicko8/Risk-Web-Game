import {Avatar, Box, Chip, Stack, chipClasses} from "@mui/material";
import PersonIcon from "@mui/icons-material/Person";
import AutorenewIcon from '@mui/icons-material/Autorenew';
import TimelineIcon from '@mui/icons-material/Timeline';
import Button from "@mui/material/Button";
import * as React from "react";
import {Item} from "./Lobbies"
import {GameInfo} from "../model/game/GameInfo";
import {useNavigate} from "react-router-dom";
import {styled, useTheme} from "@mui/material/styles";
import Typography from "@mui/material/Typography";

interface GamesProps {
    games: GameInfo[]
}

export function Games({games}: GamesProps) {
    const navigate = useNavigate();
    const theme = useTheme();

    const StyledChip = styled(Chip)(() => ({
        [`.${chipClasses.icon}`]: {
            color: theme.palette.secondary.contrastText
        },
        backgroundColor: theme.palette.secondary.main,
        color: theme.palette.secondary.contrastText
    }));

    return (
        <Box sx={{width: '100%'}}>
            <Stack spacing={1}>
                {games.length < 1 ?
                    <Typography>No active games! Join a lobby or create one to play a game!</Typography> :
                    games.map((game: GameInfo) => {
                        return <Item key={game.gameId} sx={{
                            display: "flex",
                            flexDirection: "row",
                            justifyContent: "space-between",
                            alignItems: "center"
                        }}>
                            <Stack direction="row" spacing={1}>
                                <StyledChip avatar={<Avatar alt="RiskIcon" src="../../riskIcon.png"/>} size="small"
                                            label={`Game: ${game.gameId}`}/>
                                <StyledChip icon={<AutorenewIcon/>} size="small"
                                            label={`Turn: ${game.turn}`}/>
                                <StyledChip icon={<PersonIcon/>} size="small"
                                            label={`Current player: ${game.currentPlayerIndex + 1}`}/>
                                <StyledChip icon={<TimelineIcon/>} size="small"
                                            label={`Current phase: ${game.phase.toLowerCase()}`}/>
                            </Stack>
                            <Button size="small" variant="contained" onClick={() => navigate(`/game/${game.gameId}`)}>
                                Go to game
                            </Button>
                        </Item>
                    })}
            </Stack>
        </Box>
    )
}