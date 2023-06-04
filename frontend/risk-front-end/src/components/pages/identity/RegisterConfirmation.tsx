import CssBaseline from "@mui/material/CssBaseline";
import Avatar from "@mui/material/Avatar";
import DoneIcon from '@mui/icons-material/Done';
import * as React from "react";
import Container from "@mui/material/Container";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";

export default function RegisterConfirmation() {
    return (
        <Container component="main" maxWidth="lg">
            <CssBaseline/>
            <Box
                sx={{
                    marginTop: 8,
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                }}
            >
                <Avatar sx={{m: 1, bgcolor: 'success.main'}}>
                    <DoneIcon/>
                </Avatar>
                <Typography component="p" sx={{fontSize:"large", textAlign:"center"}}>
                    <strong>Registration successful!</strong> <br/> Email confirmation has been sent to your inbox, confirm and then sign in to access your account
                </Typography>
            </Box>
        </Container>
    );
}