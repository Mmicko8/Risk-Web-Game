import Container from "@mui/material/Container";
import CssBaseline from "@mui/material/CssBaseline";
import Box from "@mui/material/Box";
import Avatar from "@mui/material/Avatar";
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';
import Typography from "@mui/material/Typography";
import * as React from "react";

export default function LobbyFull() {
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
                <Avatar sx={{m: 1, bgcolor: 'error.main'}}>
                    <ErrorOutlineIcon/>
                </Avatar>
                <Typography component="p" sx={{fontSize:"large", textAlign:"center"}}>
                    <strong>Joining lobby failed</strong> <br/> The lobby you are trying to join is already full!
                </Typography>
            </Box>
        </Container>
    );
}