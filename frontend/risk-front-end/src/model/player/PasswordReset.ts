export type PasswordReset = {
    username: string;
    newPassword: string;
    confirmNewPassword: string;
}

export type ForgotPasswordData = Omit<PasswordReset, "newPassword" | "confirmNewPassword">