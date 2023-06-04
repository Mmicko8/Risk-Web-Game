import Paper from "@mui/material/Paper";
import Typography from "@mui/material/Typography";
import StarIcon from "@mui/icons-material/Star";
import './TroopCard.css'
import {CardModel} from "../../../model/CardModel";
import {getTerritoryDrawData} from "../../../services/territoryService";

interface CardProps {
    card: CardModel
    isHighlighted: boolean
    onClick: () => void;
}

export default function TroopCard({card, isHighlighted, onClick}: CardProps) {
    const drawData = getTerritoryDrawData(card.name);

    return <Paper className={isHighlighted ? "highlight" : ""} elevation={6} onClick={onClick}
                  sx={{
                      display: "flex",
                      flexDirection: "column",
                      alignItems: "center",
                      justifyContent: "space-around",
                      height: "200px",
                      width: "160px"
                  }}
    >
        <Typography>{card.name}</Typography>
        <div style={{height: "60%", display: "flex", justifyContent: "center"}}>
            <svg xmlns="http://www.w3.org/2000/svg" viewBox={drawData.vbox} height="100%" width="90%">
                <path d={drawData.drawPath} fill="#000000"/>
            </svg>
        </div>
        <div>
            {[...Array(card.stars)].map((card, index) => <StarIcon key={index}/>)}
        </div>
    </Paper>
}