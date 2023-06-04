import {Controller, useForm} from "react-hook-form";
import {
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    FormControl,
    InputLabel,
    MenuItem,
    Select
} from "@mui/material";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import * as React from "react";
import {FriendInvite, Player} from "../../model/player/Player";

interface FriendEmailInviteProps {
    isOpen: boolean;
    onClose: () => void;
    onSubmit: (data: FriendInvite) => void;
    friends: Player[];
}

export default function FriendEmailInviteDialog({isOpen, onClose, onSubmit, friends}: FriendEmailInviteProps) {
    const {
        control,
        handleSubmit,
        reset,
    } = useForm({
        defaultValues: {
            username: '',
        }
    })

    const _onSubmit = (data: FriendInvite) => {
        onSubmit(data);
        reset();
        onClose();
    }

    return (
        <Dialog open={isOpen} onClose={onClose} fullWidth maxWidth="xs">
            <form onSubmit={handleSubmit(_onSubmit)}>
                <DialogTitle>Send email invitation to friend</DialogTitle>
                <DialogContent>
                    <Box sx={{paddingY: "1rem", paddingX: "4rem"}}>
                        <FormControl fullWidth>
                            <InputLabel id="friendUsername-label">Friend name</InputLabel>
                            <Controller
                                name="username"
                                control={control}
                                render={({field}) => (
                                    <Select
                                        {...field}
                                        labelId="friendUsername"
                                        id="friendUsername"
                                        label="Friend username"
                                    >
                                        {friends.map((friend) => {
                                            return <MenuItem value={friend.username}>{friend.username}</MenuItem>
                                        })}
                                    </Select>
                                )}
                            />
                        </FormControl>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button type="submit" variant="contained" fullWidth>Send invite</Button>
                </DialogActions>
            </form>
        </Dialog>
    );
}