import {Dialog, DialogActions, DialogContent} from "@mui/material";
import Button from "@mui/material/Button";
import {GameModel} from "../../../model/game/GameModel";
import {useState} from "react";
import TroopCard from "./TroopCard";
import Grid from "@mui/material/Grid";

interface CardSelectorProps {
    isOpen: boolean;
    onClose: () => void;
    onSubmit: (cardNames: string[]) => void;
    game: GameModel
}

export default function CardSelector({isOpen, onClose, onSubmit, game}: CardSelectorProps) {
    const currentPlayer = game.playersInGame[game.currentPlayerIndex];
    const cards = currentPlayer.playerCards.map(pc => pc.card);
    const [selectedCards, setSelectedCards] = useState<string[]>([]);

    const toggleCard = (cardName: string) => {
        if (selectedCards.includes(cardName)) setSelectedCards(selectedCards.filter(c => c !== cardName))
        else setSelectedCards([...selectedCards, cardName]);
    }

    function handleSubmit() {
        const cards = selectedCards;
        setSelectedCards([]);
        onSubmit(cards);
    }

    return (
        <Dialog open={isOpen} onClose={onClose} maxWidth="md">
            <DialogContent>
                <Grid container spacing={4} style={{display: "flex", justifyContent: "center"}}>
                    {cards.length === 0 ? <Grid item xs={12}><p>You don't have any cards</p></Grid> : ""}
                    {cards.map(c => (
                        <Grid item key={c.name} xs={3} sx={{minWidth: "200px"}}>
                            <TroopCard card={c}
                                       onClick={() => toggleCard(c.name)}
                                       isHighlighted={selectedCards.includes(c.name)}/>
                        </Grid>))}
                </Grid>
            </DialogContent>
            <DialogActions>
                <Button variant="contained" fullWidth onClick={() => handleSubmit()}>
                    Exchange Cards
                </Button>
            </DialogActions>
        </Dialog>);
}
