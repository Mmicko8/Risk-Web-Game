import * as React from 'react';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import {Link, useNavigate} from "react-router-dom";
import {useForm, Controller} from "react-hook-form";
import {SignUpCredentials} from "../../../model/player/SignUpCredentials";
import {register} from "../../../services/identityService";

function Copyright(props: any) {
    return (
        <Typography variant="body2" color="text.secondary" align="center" {...props}>
            Copyright © Risky Business {new Date().getFullYear()}.
        </Typography>
    );
}

const MIN_USERNAME_LENGTH = 2;
const MIN_USERNAME_MSG = `Username must contain at least ${MIN_USERNAME_LENGTH} characters`;
const MAX_USERNAME_LENGTH = 50;
const MAX_USERNAME_MSG = `Username can not exceed ${MAX_USERNAME_LENGTH} characters`;
const MIN_PASSWORD_LENGTH = 6;
const MIN_PASSWORD_MSG = `Password must contain at least ${MIN_PASSWORD_LENGTH} characters`;
const MAX_PASSWORD_LENGTH = 50;
const MAX_PASSWORD_MSG = `Password can not exceed ${MAX_PASSWORD_LENGTH} characters`;

export default function SignUp() {
    const navigate = useNavigate()

    const {
        control,
        handleSubmit,
        reset,
        formState: {errors}
    } = useForm({
        defaultValues: {
            username: '',
            email: '',
            password: '',
        }
    })

    const _onSubmit = (data: SignUpCredentials) => {
        register(data.username, data.email, data.password);
        navigate('/register/confirmation');
        reset()
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
                    <LockOutlinedIcon/>
                </Avatar>
                <Typography component="h1" variant="h5">
                    Sign up
                </Typography>
                <Box component="form" onSubmit={handleSubmit(_onSubmit)} sx={{mt: 3}}>
                    <Grid container spacing={2}>
                        <Grid item xs={12}>
                            <Controller
                                name="username"
                                control={control}
                                rules={{minLength: {value: MIN_USERNAME_LENGTH, message:MIN_USERNAME_MSG},
                                    maxLength: {value: MAX_USERNAME_LENGTH, message:MAX_USERNAME_MSG}}}
                                render={({field}) => (
                                    <TextField
                                        error={!!errors.username}
                                        helperText={errors.username?.message}
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
                                name="email"
                                control={control}
                                rules={{pattern: {
                                        value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                                        message: "Invalid email address"
                                    }}}
                                render={({field}) => (
                                    <TextField
                                        error={!!errors.email}
                                        helperText={errors.email?.message}
                                        {...field}
                                        required
                                        fullWidth
                                        id="email"
                                        label="Email Address"
                                        autoComplete="email"
                                    />
                                )}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <Controller
                                name="password"
                                control={control}
                                rules={{minLength: {value: MIN_PASSWORD_LENGTH, message:MIN_PASSWORD_MSG},
                                maxLength: {value: MAX_PASSWORD_LENGTH, message:MAX_PASSWORD_MSG}}}
                                render={({field}) => (
                                    <TextField
                                        error={!!errors.password}
                                        helperText={errors.password?.message}
                                        {...field}
                                        required
                                        fullWidth
                                        label="Password"
                                        type="password"
                                        id="password"
                                        autoComplete="new-password"
                                    />
                                )}
                            />
                        </Grid>
                    </Grid>
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{mt: 3, mb: 2}}
                    >
                        Sign Up
                    </Button>
                    <Grid container justifyContent="flex-end">
                        <Grid item>
                            <Link to={"/sign_in"}>
                                Already have an account? Sign in
                            </Link>
                        </Grid>
                    </Grid>
                </Box>
            </Box>
            <Copyright sx={{mt: 5}}/>
        </Container>
    );
}

