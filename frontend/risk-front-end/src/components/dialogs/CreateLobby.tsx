import {
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    FormControl,
    InputLabel,
    MenuItem,
    Select,
} from "@mui/material";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import {Controller, useForm} from "react-hook-form";
import {CreateLobbyDataNoUsername} from "../../model/lobby/CreateLobbyData";

interface CreateLobbyProps {
    isOpen: boolean;
    onClose: () => void;
    onSubmit: (data: CreateLobbyDataNoUsername) => void;
}

export default function CreateLobby({isOpen, onClose, onSubmit}: CreateLobbyProps) {
    const {
        control,
        handleSubmit,
        reset,
    } = useForm({
        defaultValues: {
            amountOfPlayers: 5,
            timer: 60
        }
    })

    const _onSubmit = (data: CreateLobbyDataNoUsername) => {
        onSubmit(data);
        reset();
        onClose();
    }

    return (
        <Dialog open={isOpen} onClose={onClose} fullWidth maxWidth="xs">
            <form onSubmit={handleSubmit(_onSubmit)}>
                <DialogTitle>Create lobby</DialogTitle>
                <DialogContent>
                    <Box sx={{paddingY: "1rem", paddingX: "4rem"}}>
                        <FormControl fullWidth>
                            <InputLabel id="amount-of-players-label">Amount of players</InputLabel>
                            <Controller
                                name="amountOfPlayers"
                                control={control}
                                render={({field}) => (
                                    <Select
                                        {...field}
                                        labelId="amount-of-players-label"
                                        id="amount-of-players"
                                        label="Amount of players"
                                    >
                                        <MenuItem value={2}>2</MenuItem>
                                        <MenuItem value={3}>3</MenuItem>
                                        <MenuItem value={4}>4</MenuItem>
                                        <MenuItem value={5}>5</MenuItem>
                                    </Select>
                                )}
                            />
                        </FormControl>
                        <FormControl fullWidth sx={{mt: "10px"}}>
                            <InputLabel id="timer-label">Turn timer</InputLabel>
                            <Controller
                                name="timer"
                                control={control}
                                render={({field}) => (
                                    <Select
                                        {...field}
                                        labelId="timer-label"
                                        id="timer"
                                        label="Turn timer"
                                    >
                                        <MenuItem value={10}>10s</MenuItem>
                                        <MenuItem value={30}>30s</MenuItem>
                                        <MenuItem value={60}>60s</MenuItem>
                                        <MenuItem value={90}>90s</MenuItem>
                                        <MenuItem value={120}>120s</MenuItem>
                                    </Select>
                                )}
                            />
                        </FormControl>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button type="submit" variant="contained" fullWidth data-testid="CreateLobbySubmit">Create</Button>
                </DialogActions>
            </form>
        </Dialog>
    );
}