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
import {EmailInvitation} from "../../model/lobby/EmailInvitation";
import TextField from "@mui/material/TextField";
import * as React from "react";

interface EmailInvitationProps {
    isOpen: boolean;
    onClose: () => void;
    onSubmit: (data: EmailInvitation) => void;
}

export default function EmailInvitationDialog({isOpen, onClose, onSubmit}: EmailInvitationProps) {
    const {
        control,
        handleSubmit,
        reset,
    } = useForm({
        defaultValues: {
            type: 'username',
            usernameOrEmail: ''
        }
    })

    const _onSubmit = (data: EmailInvitation) => {
        onSubmit(data);
        reset();
        onClose();
    }

    return (
        <Dialog open={isOpen} onClose={onClose} fullWidth maxWidth="xs">
            <form onSubmit={handleSubmit(_onSubmit)}>
                <DialogTitle>Send email invitation to player</DialogTitle>
                <DialogContent>
                    <Box sx={{paddingY: "1rem", paddingX: "4rem"}}>
                        <FormControl fullWidth>
                            <InputLabel id="type-of-invitation-label">Type of Invitation</InputLabel>
                            <Controller
                                name="type"
                                control={control}
                                render={({field}) => (
                                    <Select
                                        {...field}
                                        labelId="type-of-invitation"
                                        id="type-of-invitation"
                                        label="Type of Invitation"
                                    >
                                        <MenuItem value={'username'}>With Username</MenuItem>
                                        <MenuItem value={'email'}>With Email</MenuItem>
                                    </Select>
                                )}
                            />
                        </FormControl>
                        <FormControl fullWidth sx={{mt: "10px"}}>
                            <Controller
                                name="usernameOrEmail"
                                control={control}
                                render={({field}) => (
                                    <TextField
                                        {...field}
                                        required
                                        fullWidth
                                        id="usernameOrEmail"
                                        label="Username or Email"
                                        autoComplete="Username or email"
                                    />
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