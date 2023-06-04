import Box from "@mui/material/Box";
import Stepper from '@mui/material/Stepper';
import Step from '@mui/material/Step';
import StepLabel from '@mui/material/StepLabel';
import Fab from '@mui/material/Fab';
import SingleArrowIcon from '@mui/icons-material/KeyboardArrowRight';
import DoubleArrowIcon from '@mui/icons-material/KeyboardDoubleArrowRight';
import Avatar from "@mui/material/Avatar";
import {useContext} from "react";
import AccessContext from "../../context/AccessContext";
import {PlayerInGame} from "../../model/player/PlayerInGame";
import Typography from "@mui/material/Typography";
import {getPhaseFromNumber, Phases} from "../../services/gameService";
import TroopIndicator from "./TroopIcon";
import {getAvatar} from "../../services/utilsService";
import '../hideMobile.css'

interface CurrentPlayerProps {
    nextPhase: () => void;
    nextTurn: () => void;
    currentPhase: number;
    currentPlayer: PlayerInGame
}

const steps = [
    'Reinforce',
    'Attack',
    'Fortify',
];

export default function CurrentPlayer({nextPhase, nextTurn, currentPhase, currentPlayer}: CurrentPlayerProps) {
    const {username} = useContext(AccessContext);
    const handleNext = () => {
        if (steps[currentPhase] === 'Fortify') nextTurn();
        else nextPhase();
    }

    const isUserNotCurrentPlayer = () => {
        return !username || username !== currentPlayer.player.username;
    }

    const getFabIcon = (phaseNumber: number) => {
        const phase = getPhaseFromNumber(phaseNumber);
        if (phase === Phases.FORTIFICATION) return <DoubleArrowIcon sx={{fontSize: "3vw"}}/>;
        return <SingleArrowIcon sx={{fontSize: "3vw"}}/>;
    }

    const hasUnplacedTroops = (phaseNumber: number) => {
        const phase = getPhaseFromNumber(phaseNumber);
        return phase === Phases.REINFORCEMENT && currentPlayer.remainingTroopsToReinforce > 0;
    }

    return (
        <Box display="flex" alignContent="center" justifyContent="center" position="relative"
             sx={{
                 height: "15vh", border: 1, borderRadius: 3, backgroundColor: "rgba(98,98,98,0.61)",
                 display: "flex", justifyContent: "space-around", alignItems: "center"
             }}>
            <Avatar src={getAvatar(currentPlayer.player.profilePicture)}
                    sx={{height: "5vw", width: "5vw", marginLeft: "0.5vw", border: `5px solid ${currentPlayer.color}`}}/>
            <div style={{display: "flex", flexDirection: "column", alignItems: "center", justifyContent: "space-around" ,height:"100%"}}>
                <Typography className="hidden-mobile">{currentPlayer.player.username}</Typography>
                <Stepper activeStep={currentPhase} alternativeLabel>
                    {steps.map((label) => (
                        <Step key={label}>
                            <StepLabel>{label}</StepLabel>
                        </Step>
                    ))}
                </Stepper>
            </div>
            <div style={{height: "5.5vw", width: "5.5vw", display: "flex", justifyContent: "center", alignItems: "center"}}>
                {hasUnplacedTroops(currentPhase) ?
                    <TroopIndicator color={currentPlayer.color} troopCount={currentPlayer.remainingTroopsToReinforce}/>
                    :
                    <Fab color="primary" onClick={handleNext} disabled={isUserNotCurrentPlayer()}
                         style={{height: "4vw", width: "4vw"}}>
                        {getFabIcon(currentPhase)}
                    </Fab>
                }
            </div>
        </Box>
    )
}