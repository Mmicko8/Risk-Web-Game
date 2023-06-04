import Container from "@mui/material/Container";
import CssBaseline from "@mui/material/CssBaseline";
import Box from "@mui/material/Box";
import Avatar from "@mui/material/Avatar";
import DoneIcon from "@mui/icons-material/Done";
import Typography from "@mui/material/Typography";
import * as React from "react";

export default function PwResetReqConfirmation() {
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
                    <strong>Password reset request successful!</strong> <br/> An email has been sent with a link to reset your password!
                </Typography>
            </Box>
        </Container>
    );
}