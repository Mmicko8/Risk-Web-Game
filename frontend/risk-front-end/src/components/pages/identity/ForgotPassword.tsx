import {Controller, useForm} from "react-hook-form";
import {useNavigate} from "react-router-dom";
import Container from "@mui/material/Container";
import CssBaseline from "@mui/material/CssBaseline";
import Box from "@mui/material/Box";
import Avatar from "@mui/material/Avatar";
import HelpOutlineIcon from '@mui/icons-material/HelpOutline';
import Typography from "@mui/material/Typography";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import * as React from "react";
import {ForgotPasswordData} from "../../../model/player/PasswordReset";
import {forgotPassword} from "../../../services/identityService";
import {FormControl} from "@mui/material";

function Copyright(props: any) {
    return (
        <Typography variant="body2" color="text.secondary" align="center" {...props}>
            Copyright © Risky Business {new Date().getFullYear()}.
        </Typography>
    );
}

export function ForgotPassword() {
    const {
        control,
        handleSubmit,
        reset,
        setError,
        formState: {errors}
    } = useForm({
        defaultValues: {
            username: ''
        }
    })

    const navigate = useNavigate()

    const _onSubmit = async (data: ForgotPasswordData) => {
        forgotPassword(data.username)
            .then(() => {
                navigate('/password/reset_request/confirmation');
            })
            .catch(() => {
                setError('username', {
                    type: "server",
                    message: "No account with this username was found!"
                })
            })
        reset();
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
                    <HelpOutlineIcon/>
                </Avatar>
                <Typography component="h1" variant="h5">
                    Forgot password
                </Typography>
                <Box component="form" onSubmit={handleSubmit(_onSubmit)} noValidate sx={{mt: 1}}>
                    <FormControl fullWidth sx={{mt: "10px"}}>
                        <Controller
                            name="username"
                            control={control}
                            render={({field}) => (
                                <TextField
                                    {...field}
                                    margin="normal"
                                    fullWidth
                                    id="username"
                                    label="Username"
                                    autoComplete="username"
                                    autoFocus
                                />
                            )}
                        />
                    </FormControl>
                    <div style={{color:"red"}}>{errors.username?.message}</div>
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{mt: 3, mb: 2}}
                    >
                        Request password reset
                    </Button>
                </Box>
            </Box>
            <Copyright sx={{mt: 8, mb: 4}}/>
        </Container>
    );
}