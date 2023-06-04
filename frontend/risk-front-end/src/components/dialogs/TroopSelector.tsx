import {styled} from '@mui/material/styles';
import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Typography from '@mui/material/Typography';
import Slider from '@mui/material/Slider';
import MuiInput from '@mui/material/Input';
import {ChangeEvent, useState} from 'react';
import {
    Dialog,
    DialogActions,
    DialogContent
} from "@mui/material";
import Button from "@mui/material/Button";


interface ReinforceDialogProps {
    isOpen: boolean;
    onClose: () => void;
    onSubmit: (troops: number, action: string) => void;
    maxTroops: number;
    confirmButtonText: string;
}

export default function TroopSelector({isOpen, onClose, onSubmit, maxTroops, confirmButtonText}: ReinforceDialogProps) {

    const Input = styled(MuiInput)`
      width: 42px;
    `;

    const [troops, setTroops] = useState(1);

    const handleSliderChange = (event: Event, newValue: number | number[]) => {
        if (typeof newValue === "number") {
            setTroops(newValue)
        }
        else {
            setTroops(1);
        }
    };

    const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
        setTroops(event.target.value === '' ? 1 : Number(event.target.value));
    };

    const handleBlur = () => {
        if (troops < 1) {
            setTroops(1);
        } else if (troops > maxTroops) {
            setTroops(maxTroops);
        }
    };

    const sxprops =  {"& .MuiDialog-container": {alignItems: "end"}}; // aligns component on bottom of page instead of middle
    return (
        <Dialog open={isOpen} onClose={onClose} fullWidth maxWidth="xs" sx={sxprops} hideBackdrop>
            <DialogContent>
                <Box sx={{paddingY: "1rem", paddingX: "4rem"}}>
                    <Typography id="input-slider" gutterBottom>Troops</Typography>
                    <Grid container spacing={2} alignItems="center">
                        <Grid item xs>
                            <Slider value={troops} min={1} max={maxTroops} onChange={handleSliderChange} aria-labelledby="input-slider"/>
                        </Grid>
                        <Grid item>
                            <Input
                                value={troops}
                                size="small"
                                onChange={handleInputChange}
                                onBlur={handleBlur}
                                inputProps={{
                                    step: 1,
                                    min: 1,
                                    max: maxTroops,
                                    type: 'number',
                                    'aria-labelledby': 'input-slider',
                                }}
                            />
                        </Grid>
                    </Grid>
                </Box>
            </DialogContent>
            <DialogActions>
                <Button variant="contained" fullWidth onClick={() => {onSubmit(troops, confirmButtonText); setTroops(1)}}>
                    {confirmButtonText}
                </Button>
            </DialogActions>
        </Dialog>);
}