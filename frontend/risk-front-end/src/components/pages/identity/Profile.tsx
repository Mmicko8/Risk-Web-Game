import {useProfile} from "../../../hooks/useProfile";
import Loading from "../../Loading";
import {Alert} from "../../Alert";
import Container from "@mui/material/Container";
import Grid from "@mui/material/Grid";
import Avatar from "@mui/material/Avatar";
import * as React from "react";
import {useContext} from "react";
import EditProfile from "../../EditProfile";
import AccessContext from "../../../context/AccessContext";
import AchievementIcon from '@mui/icons-material/MilitaryTech';
import LoyaltyPointsIcon from '@mui/icons-material/MonetizationOn';
import ShopItemIcon from '@mui/icons-material/ShoppingBag';
import Typography from "@mui/material/Typography";
import {useTheme} from "@mui/material/styles";
import Divider from "@mui/material/Divider"
import {convertAchievementNameToImagePath, getAvatar} from "../../../services/utilsService";
import {Achievement} from "../../../model/Achievement";
import {ShopItemModel} from "../../../model/ShopItemModel";
import {ShopActionType, ShopItems} from "../Shop";

interface GameStatsProps {
    played: number;
    won: number;
    lost: number;
}
function GameStats({played, won, lost}: GameStatsProps) {
    const theme = useTheme();
    return <div>
        <div style={{display: "flex", justifyContent: "space-around", alignItems: "center"}}>
            <div style={{display: "flex", flexDirection: "column", alignItems: "center"}}>
                <Typography variant="h4">{played}</Typography>
                <div style={{color: theme.palette.grey.A700}}>PLAYED</div>
            </div>

            <Divider orientation="vertical" flexItem/>

            <div style={{display: "flex", flexDirection: "column", alignItems: "center"}}>
                <Typography variant="h4">{won}</Typography>
                <div style={{color: theme.palette.grey.A700}}>WON</div>
            </div>

            <Divider orientation="vertical" flexItem/>

            <div style={{display: "flex", flexDirection: "column", alignItems: "center"}}>
                <Typography variant="h4">{lost}</Typography>
                <div style={{color: theme.palette.grey.A700}}>LOST</div>
            </div>
        </div>
    </div>;
}

function Achievements({achievements}: {achievements: Achievement[]}) {
    const theme = useTheme();
    if (achievements.length < 1) return <></>

    return <div style={{marginTop: "50px"}}>
        <Typography variant="h4" sx={{display:"flex", justifyContent:"center"}}>
            Achievements
        </Typography>
        <Grid container style={{justifyContent: "space-evenly", marginTop: "20px"}}>
            {achievements.map((a: Achievement) => {
                return <Grid item xs={12} md={6} xl={4} key={a.achievementId} style={{display: "flex", flexDirection: "column",
                    alignItems: "center", minWidth: "300px"}}>
                    <img src={convertAchievementNameToImagePath(a.name)} alt={a.name}/>
                    <Typography variant="h5">{a.name}</Typography>
                    <Typography sx={{color: theme.palette.grey.A700}}>{a.description}</Typography>
                </Grid>
            })}
        </Grid>
    </div>;
}

interface OwnedItemsProps {
    shopItems: ShopItemModel[];
    equipItem: (shopItemId: number) => void;
}
function OwnedItems({shopItems, equipItem}: OwnedItemsProps) {
    const emptyMessage = "You don't own any items of this type yet. Head over to the shop to check them out!";

    if (shopItems.length < 1) return <></>;

    return <div style={{marginTop: "50px", marginBottom: "50px"}}>
        <ShopItems emptyMessage={emptyMessage} shopItems={shopItems} actionType={ShopActionType.EQUIP}
                   action={(shopItemId) => equipItem(shopItemId)}/>
    </div>;
}

export default function Profile() {
    const {isLoading, isError, profile, equipItemMutation} = useProfile();
    const {username} = useContext(AccessContext);
    const theme = useTheme();

    if (isLoading) {
        return <Loading/>
    }
    if (isError) {
        return <Alert message={"Error loading the profile"}/>
    }

    const statSx = {color: theme.palette.primary.main, fontSize: "80px"}
    const stats = [
        {icon: <AchievementIcon sx={statSx}/>, amount: profile.achievements.length, text: "Achievements"},
        {icon: <LoyaltyPointsIcon sx={statSx}/>, amount: profile.loyaltyPoints, text: "Loyalty Points"},
        {icon: <ShopItemIcon sx={statSx}/>, amount: profile.shopItems.length, text: "Owned Items"}
    ]

    return <Container>
        <Grid container columnSpacing={10} sx={{marginTop: "10vh"}}>
            <Grid item xs={12} md={6}>
                <div style={{display: "flex", flexDirection: "column", alignItems: "center"}}>
                    <Avatar sx={{width: "120px", height: "120px"}} src={getAvatar(profile.profilePicture)}/>
                    {profile.title === null ? ""
                        :
                        <Typography sx={{marginY: "8px", fontStyle: "italic"}}>"{profile.title}"</Typography>
                    }
                    <EditProfile id={profile.id} username={username!} email={profile.email}/>
                </div>
            </Grid>
            <Grid item xs={12} md={6} style={{display: "flex", flexDirection: "column", justifyContent: "center", gap: "20px"}}>
                <div>
                    <Typography variant="h4"  sx={{display:"flex", justifyContent:"center", marginBottom: "20px"}}>
                        Statistics
                    </Typography>
                    <div style={{display: "flex", justifyContent: "space-around", alignItems: "center"}}>
                        {stats.map((stat, index) =>
                            <div key={index} style={{display: "flex", flexDirection: "column", alignItems: "center"}}>
                                {stat.icon}
                                <Typography variant="h4">{stat.amount}</Typography>
                                <div style={{color: theme.palette.grey.A700}}>{stat.text}</div>
                            </div>
                        )}
                    </div>
                </div>
                <Divider flexItem/>
                <GameStats played={profile.gamesPlayed} won={profile.gamesWon} lost={profile.gamesLost}/>
            </Grid>
        </Grid>
        <Achievements achievements={profile.achievements}/>
        <OwnedItems shopItems={profile.shopItems} equipItem={equipItemMutation}/>
    </Container>
}