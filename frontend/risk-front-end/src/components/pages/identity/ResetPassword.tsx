import Typography from "@mui/material/Typography";
import {useNavigate, useParams} from "react-router-dom";
import {Controller, useForm} from "react-hook-form";
import Container from "@mui/material/Container";
import CssBaseline from "@mui/material/CssBaseline";
import Box from "@mui/material/Box";
import Avatar from "@mui/material/Avatar";
import LockResetIcon from '@mui/icons-material/LockReset';
import Grid from "@mui/material/Grid";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import * as React from "react";
import {PasswordReset} from "../../../model/player/PasswordReset";
import {resetPassword} from "../../../services/identityService";
import {useState} from "react";

function Copyright(props: any) {
    return (
        <Typography variant="body2" color="text.secondary" align="center" {...props}>
            Copyright © Risky Business {new Date().getFullYear()}.
        </Typography>
    );
}

const MIN_PASSWORD_LENGTH = 6;
const MIN_PASSWORD_MSG = `Password must contain at least ${MIN_PASSWORD_LENGTH} characters`;
const MAX_PASSWORD_LENGTH = 50;
const MAX_PASSWORD_MSG = `Password can not exceed ${MAX_PASSWORD_LENGTH} characters`;

export default function ResetPassword() {
    const navigate = useNavigate()
    const {token} = useParams<{ token: string }>()
    const [isServerError, setIsServerError] = useState(true)

    const {
        control,
        handleSubmit,
        reset,
        setError,
        formState: {errors}
    } = useForm({
        defaultValues: {
            username: '',
            newPassword: '',
            confirmNewPassword: '',
        }
    })

    const _onSubmit = (data: PasswordReset) => {
        if (data.newPassword !== data.confirmNewPassword) {
            setError('confirmNewPassword', {
                type: "validation",
                message: "Please make sure your passwords match."
            })
        } else {
            if (token !== undefined) {
                resetPassword(data.username, data.newPassword, token)
                    .then(() => {
                        navigate('/sign_in')
                    })
                    .catch(() => {
                        setIsServerError(false)
                    })
                reset()
            }
        }
    };

    return (
        <Container component="main" maxWidth="xs">
            <CssBaseline/>
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
                <Avatar sx={{m: 1, bgcolor: 'secondary.main'}}>
                    <LockResetIcon/>
                </Avatar>
                <Typography component="h1" variant="h5">
                    Reset password
                </Typography>
                <Box component="form" onSubmit={handleSubmit(_onSubmit)} sx={{mt: 3}}>
                    <Grid container spacing={2}>
                        <Grid item xs={12}>
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
                                        autoComplete="username"
                                    />
                                )}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <Controller
                                name="newPassword"
                                control={control}
                                rules={{minLength: {value: MIN_PASSWORD_LENGTH, message:MIN_PASSWORD_MSG},
                                    maxLength: {value: MAX_PASSWORD_LENGTH, message:MAX_PASSWORD_MSG}}}
                                render={({field}) => (
                                    <TextField
                                        error={!!errors.newPassword}
                                        helperText={errors.newPassword?.message}
                                        {...field}
                                        required
                                        fullWidth
                                        id="newPassword"
                                        label="New Password"
                                        type="password"
                                        autoComplete="New Password"
                                    />
                                )}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <Controller
                                name="confirmNewPassword"
                                control={control}
                                render={({field}) => (
                                    <TextField
                                        error={!!errors.confirmNewPassword}
                                        helperText={errors.confirmNewPassword?.message}
                                        {...field}
                                        required
                                        fullWidth
                                        label="Confirm New Password"
                                        type="password"
                                        id="confirmNewPassword"
                                        autoComplete="Confirm New Password"
                                    />
                                )}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <Typography hidden={isServerError} sx={{color:"red"}}>
                                Something went wrong resetting the password.
                            </Typography>
                        </Grid>
                    </Grid>
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{mt: 3, mb: 2}}
                    >
                        Reset password
                    </Button>
                </Box>
            </Box>
            <Copyright sx={{mt: 5}}/>
        </Container>
    );
}