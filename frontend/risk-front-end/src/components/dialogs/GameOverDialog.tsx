import {Dialog, DialogActions, DialogContent} from "@mui/material";
import Typography from "@mui/material/Typography";
import {GameModel} from "../../model/game/GameModel";
import {getWinner} from "../../services/gameService";
import Button from "@mui/material/Button";
import {useNavigate} from "react-router-dom";

interface GameOverDialogProps {
    game: GameModel;
    isOpen: boolean;
}
export default function GameOverDialog({game, isOpen} : GameOverDialogProps) {
    const navigate = useNavigate();
    const winner = getWinner(game);
    const message = winner ? winner.player.username + " has won!" : "Game Over!";
    return <Dialog open={isOpen}>
            <DialogContent>
                <Typography fontFamily="Courier" fontWeight="bolder" variant="h3">{message}</Typography>
            </DialogContent>
            <DialogActions>
                <Button variant="contained" onClick={() => navigate('/')} fullWidth>Go back to home</Button>
            </DialogActions>
        </Dialog>;
}