import {Controller, useForm} from "react-hook-form";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import Box from "@mui/material/Box";
import * as React from "react";
import {editPlayerUsername, resetPasswordRequest} from "../services/identityService";
import {useContext} from "react";
import AccessContext from "../context/AccessContext";
import {useNavigate} from "react-router-dom";
import Grid from "@mui/material/Grid";

interface EditProfileProps {
    id: number,
    username: string;
    email: string;
}

export default function EditProfile({id, username, email}: EditProfileProps) {
    const navigate = useNavigate();
    const {removeUsername, removeAccessToken} = useContext(AccessContext);
    const {
        control,
        handleSubmit,
        reset,
        setError,
        formState: {errors}
    } = useForm({
        defaultValues: {
            username
        }
    });

    const MIN_USERNAME_LENGTH = 2;
    const MIN_USERNAME_MSG = `Username must contain at least ${MIN_USERNAME_LENGTH} characters`;
    const MAX_USERNAME_LENGTH = 50;
    const MAX_USERNAME_MSG = `Username can not exceed ${MAX_USERNAME_LENGTH} characters`;

    const _onSubmit = async (data: { username: string }) => {
        editPlayerUsername(id, data.username).then(() => {
            removeUsername();
            removeAccessToken();
            navigate("/sign_in");
        }).catch(() => {
            setError('username', {
                type: "server",
                message: "Something went wrong updating the user"
            })
        });
        reset();
    };

    return <Box component="form" onSubmit={handleSubmit(_onSubmit)} noValidate sx={{mt: 1}}>
        <Controller
            name="username"
            control={control}
            rules={{
                required: "Username cannot be empty!",
                minLength: {value: MIN_USERNAME_LENGTH, message: MIN_USERNAME_MSG},
                maxLength: {value: MAX_USERNAME_LENGTH, message: MAX_USERNAME_MSG}
            }}
            render={({field}) => (
                <TextField
                    {...field}
                    error={!!errors.username}
                    helperText={errors.username?.message}
                    margin="normal"
                    fullWidth
                    id="username"
                    label="Username"
                    autoComplete="username"
                />
            )}
        />

        <TextField
            name="email"
            margin="normal"
            fullWidth
            id="email"
            label="Email"
            defaultValue={email}
            disabled
        />

        {/*sx={{mt: 3, mb: 2}}*/}
        <Grid container spacing={2}>
            <Grid item xs={6}>
                <Button type="submit" variant="contained" fullWidth>Update and log out</Button>
            </Grid>
            <Grid item xs={6}>
                <Button variant="contained" fullWidth onClick={() => {resetPasswordRequest(); navigate('/password/reset_request/confirmation');}}>
                    Reset password
                </Button>
            </Grid>
        </Grid>
    </Box>
}