import SpeedDial from '@mui/material/SpeedDial';
import SpeedDialAction from '@mui/material/SpeedDialAction';
import HomeIcon from '@mui/icons-material/Home';
import FingerprintIcon from '@mui/icons-material/Fingerprint';
import LogoutIcon from '@mui/icons-material/Logout';
import AccountBoxIcon from '@mui/icons-material/AccountBox';
import PeopleIcon from '@mui/icons-material/People';
import {useNavigate} from "react-router-dom";
import {useContext} from "react";
import AccessContext from "../context/AccessContext";
import MenuIcon from '@mui/icons-material/Menu';
import PersonAddIcon from '@mui/icons-material/PersonAdd';
import LeaderboardIcon from '@mui/icons-material/Leaderboard';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';

export function NavigationDial() {
    const {username, removeUsername, removeAccessToken} = useContext(AccessContext);

    const navigate = useNavigate();

    function getNavigationActions() {
        let actions = [
            {icon: <HomeIcon/>, name: 'Home', action: () => navigate("/")},
            {icon: <LeaderboardIcon/>, name: "Leaderboard", action: () => navigate("/leaderboard")}];
        if (username) {
            actions.push({icon: <ShoppingCartIcon/>, name: "Shop", action: () => navigate("/shop")})
            actions.push({icon: <AccountBoxIcon/>, name: "Profile", action: () => navigate("/profile")})
            actions.push({icon: <PeopleIcon/>, name: "Social hub", action: () => navigate("/friends")})
            actions.push({icon: <LogoutIcon/>, name: "Logout", action: () => { removeUsername(); removeAccessToken();
                    navigate("/")
            }});
        } else {
            actions.push({icon: <PersonAddIcon/>, name: "Register", action: () => navigate("/register")})
            actions.push({icon: <FingerprintIcon/>, name: "Login", action: () => navigate("/sign_in")})
        }
        return actions;
    }

    return (
        <div style={{margin: 0, left: 8, top: 8, position: "fixed"}}>
            <SpeedDial
                ariaLabel="SpeedDial openIcon example"
                sx={{position: 'absolute', top: 4, left: 4}}
                icon={<MenuIcon/>}
                direction={"right"}
            >
                {getNavigationActions().map((action) => (
                    <SpeedDialAction
                        key={action.name}
                        icon={action.icon}
                        tooltipTitle={action.name}
                        onClick={action.action}
                    />
                ))}
            </SpeedDial>
        </div>
    );
}