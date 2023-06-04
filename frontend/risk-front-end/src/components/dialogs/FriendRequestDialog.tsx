import {Controller, useForm} from "react-hook-form";
import {
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    FormControl,
} from "@mui/material";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import TextField from "@mui/material/TextField";
import * as React from "react";
import {FriendRequest} from "../../model/player/Player";

interface FriendRequestProps {
    isOpen: boolean;
    onClose: () => void;
    onSubmit: (data: FriendRequest) => void;
}

export default function FriendRequestDialog({isOpen, onClose, onSubmit}: FriendRequestProps) {
    const {
        control,
        handleSubmit,
        reset,
    } = useForm({
        defaultValues: {
            username: '',
        }
    })

    const _onSubmit = (data: FriendRequest) => {
        onSubmit(data);
        reset();
        onClose();
    }

    return (
        <Dialog open={isOpen} onClose={onClose} fullWidth maxWidth="xs">
            <form onSubmit={handleSubmit(_onSubmit)}>
                <DialogTitle>Friend request</DialogTitle>
                <DialogContent>
                    <Box sx={{paddingY: "1rem", paddingX: "4rem"}}>
                        <FormControl fullWidth sx={{mt: "10px"}}>
                            <Controller
                                name="username"
                                control={control}
                                render={({field}) => (
                                    <TextField
                                        {...field}
                                        required
                                        fullWidth
                                        id="username"
                                        label="Username"
                                    />
                                )}
                            />
                        </FormControl>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button type="submit" variant="contained" fullWidth>Send friend request</Button>
                </DialogActions>
            </form>
        </Dialog>
    );
}