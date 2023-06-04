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
import {SignInCredentials} from "../../../model/player/SignInCredentials";
import {useContext} from "react";
import AccessContext from "../../../context/AccessContext";
import {login} from "../../../services/identityService";

function Copyright(props: any) {
    return (
        <Typography variant="body2" color="text.secondary" align="center" {...props}>
            Copyright © Risky Business {new Date().getFullYear()}.
        </Typography>
    );
}

export default function SignIn() {
    const {
        control,
        handleSubmit,
        reset,
        setError,
        formState: {errors}
    } = useForm({
        defaultValues: {
            username: '',
            password: '',
        }
    })

    const {setAccessToken, setUsername} = useContext(AccessContext);
    const navigate = useNavigate()

    const _onSubmit = async (data: SignInCredentials) => {
        login(data.username, data.password)
            .then((response) => {
                setAccessToken(response?.headers?.authorization!);
                setUsername(data.username);
                navigate('/');
            })
            .catch(() => {
                setError('password', {
                    type: "server",
                    message: "The username or password was incorrect"
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
                        <LockOutlinedIcon/>
                    </Avatar>
                    <Typography component="h1" variant="h5">
                        Sign in
                    </Typography>
                    <Box component="form" onSubmit={handleSubmit(_onSubmit)} noValidate sx={{mt: 1}}>
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
                        <Controller
                            name="password"
                            control={control}
                            render={({field}) => (
                                <TextField
                                    {...field}
                                    margin="normal"
                                    fullWidth
                                    label="Password"
                                    type="password"
                                    id="password"
                                    autoComplete="current-password"
                                />
                            )}
                        />
                        <div style={{color:"red"}}>{errors.password?.message}</div>
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            sx={{mt: 3, mb: 2}}
                        >
                            Sign In
                        </Button>
                        <Grid container>
                            <Grid item xs>
                                <Link to={`/password/forgot`}>
                                    Forgot password?
                                </Link>
                            </Grid>
                            <Grid item>
                                <Link to={`/register`}>
                                    {"Don't have an account? Sign Up"}
                                </Link>
                            </Grid>
                        </Grid>
                    </Box>
                </Box>
                <Copyright sx={{mt: 8, mb: 4}}/>
            </Container>
    );
}
