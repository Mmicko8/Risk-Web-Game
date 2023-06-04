import './App.css';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import React from 'react';
import axios from "axios";
import Home from "./components/pages/Home";
import {NavigationDial} from "./components/NavigationDial";
import SignIn from "./components/pages/identity/SignIn";
import Register from "./components/pages/identity/Register";
import Game from "./components/pages/Game";
import {QueryClient, QueryClientProvider} from "react-query";
import AccessTokenContextProvider from "./context/AccessContextProvider";
import localForage from "localforage";
import RegisterConfirmation from "./components/pages/identity/RegisterConfirmation";
import {Lobby} from "./components/pages/Lobby";
import Leaderboard from "./components/pages/Leaderboard";
import {createTheme, ThemeProvider} from '@mui/material/styles';
import {green} from '@mui/material/colors';
import {Shop} from "./components/pages/Shop";
import Profile from "./components/pages/identity/Profile";
import { ForgotPassword } from './components/pages/identity/ForgotPassword';
import ResetPassword from "./components/pages/identity/ResetPassword";
import PwResetReqConfirmation from "./components/pages/identity/PwResetReqConfirmation";
import LobbyFull from "./components/pages/LobbyFull";
import Friends from "./components/pages/Friends";


axios.defaults.baseURL = process.env.REACT_APP_BACKEND_URL
// Add a request interceptor
axios.interceptors.request.use(async config => {
    // Do something before request is sent
    const accessToken = await localForage.getItem<string>("accessToken");
    if (accessToken) {
        config.headers = config.headers ?? {};
        config.headers.authorization = `Bearer ${accessToken}`;
    }
    return config;
}, error => {
    // Do something with request error
    return Promise.reject(error);
});

const queryClient = new QueryClient();
const theme = createTheme({
    palette: {
        secondary: {
            main: green[800],
            dark: green[900],
            light: green[600]
        },
    },
});


function App() {
    return (
        <QueryClientProvider client={queryClient}>
            <ThemeProvider theme={theme}>
                <AccessTokenContextProvider>
                    <BrowserRouter>
                        <NavigationDial/>
                        <Routes>
                            <Route path="/" element={<Home/>}/>
                            <Route path="/sign_in" element={<SignIn/>}/>
                            <Route path="/register" element={<Register/>}/>
                            <Route path="/register/confirmation" element={<RegisterConfirmation/>}/>
                            <Route path="/password/forgot" element={<ForgotPassword/>}/>
                            <Route path="/password/reset_request/confirmation" element={<PwResetReqConfirmation/>}/>
                            <Route path="/password/reset/:token" element={<ResetPassword/>}/>
                            <Route path="/game/:id" element={<Game/>}/>
                            <Route path="/lobby/:id" element={<Lobby/>}/>
                            <Route path="/lobbyFull" element={<LobbyFull/>}/>
                            <Route path="/leaderboard" element={<Leaderboard/>}/>
                            <Route path="/shop" element={<Shop/>}/>
                            <Route path="/profile" element={<Profile/>}/>
                            <Route path="/friends" element={<Friends/>}/>
                        </Routes>
                    </BrowserRouter>
                </AccessTokenContextProvider>
            </ThemeProvider>
        </QueryClientProvider>
    );
}

export default App;
