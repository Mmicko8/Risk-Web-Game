import {useLeaderboard} from "../../hooks/useLeaderboard";
import * as React from "react";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import {styled} from '@mui/material/styles';
import TableCell, {tableCellClasses} from '@mui/material/TableCell';
import {Player} from "../../model/player/Player";
import { useTheme } from '@mui/material/styles';
import {Alert} from "../Alert";
import Loading from "../Loading";

export default function Leaderboard() {
    const theme = useTheme();
    const {isLoading, isError, players} = useLeaderboard();

    if (isLoading) {
        return <Loading/>
    }
    if (isError) {
        return <Alert message={"Error loading the leaderboard"}/>
    }
    //style on table cell
    const StyledTableCell = styled(TableCell)(() => ({
        [`&.${tableCellClasses.head}`]: {
            backgroundColor: theme.palette.primary.main,
            color: theme.palette.primary.contrastText,
        }
    }));
    //style on table row
    const StyledTableRow = styled(TableRow)(({theme}) => ({
        '&:nth-of-type(odd)': {
            backgroundColor: theme.palette.action.hover,
        },
        // hide last border
        '&:last-child td, &:last-child th': {
            border: 0,
        },
    }));

    function Rows() {
        return (
            <>{players.map((row: Player, i: number) => (
                <StyledTableRow key={row.id}>
                    <StyledTableCell component="th" scope="row">{i+1}</StyledTableCell>
                    <StyledTableCell>{row.username}</StyledTableCell>
                    <StyledTableCell>{row.gamesWon}</StyledTableCell>
                </StyledTableRow>
            ))
            }
            </>
        )
    }

    return (
        <TableContainer sx={{width: 300, margin: "auto", marginTop: "20px"}} component={Paper}>
            <Table aria-label="customized table">
                <TableHead>
                    <TableRow>
                        {["Ranking", "Player", "Wins"].map(str => <StyledTableCell key={str}>{str}</StyledTableCell>)}
                    </TableRow>
                </TableHead>
                <TableBody>
                    <Rows/>
                </TableBody>
            </Table>
        </TableContainer>
    );
}