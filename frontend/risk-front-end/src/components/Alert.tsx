import AlertMui from "@mui/material/Alert";
import * as React from "react";

interface ErrorAlertProps {
    message: string;
    severity?: "error" | "info" | "warning";
}
export function Alert({message, severity}: ErrorAlertProps) {
    if (!severity) severity = "error"
    return <AlertMui severity={severity} sx={{justifyContent: "center"}}>{message}</AlertMui>
}