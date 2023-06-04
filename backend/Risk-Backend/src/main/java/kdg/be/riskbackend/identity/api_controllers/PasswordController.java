package kdg.be.riskbackend.identity.api_controllers;

import kdg.be.riskbackend.identity.dtos.password.PasswordResetRequest;
import kdg.be.riskbackend.identity.services.PasswordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.*;

/**
 * controller for password reset
 */
@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000","http://localhost","http://127.0.0.1",
        "http://frontend-service", "http://backend-service", "http://ai-service", "http://www.risk.gq",
        "https://localhost:3000", "https://localhost:5000","https://localhost","https://127.0.0.1",
        "https://frontend-service", "https://backend-service", "https://ai-service", "https://www.risk.gq"}
        , exposedHeaders = HttpHeaders.AUTHORIZATION)
@RequestMapping(path = "/api/password")
public class PasswordController {
    private final PasswordService passwordService;

    /**
     * sends email to a user with a link to reset his password
     */
    @PostMapping("/forgot/{username}")
    public ResponseEntity<String> forgotPassword(@PathVariable String username) {
        try {
            passwordService.resetRequest(username);
            log.info("Password reset email sent to the emails of " + username);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Error while resetting password: " + e.getMessage());
            throw new ResponseStatusException(CONFLICT, e.getMessage());
        }
    }

    /**
     * sends email to a user with a link to reset his password after requesting for a password change
     */
    @PostMapping("/resetRequest")
    public ResponseEntity<String> resetRequest() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            passwordService.resetRequest(authentication.getName());
            log.info("Password reset email sent to the emails of " + authentication.getName());
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.error("Error while resetting password: " + e.getMessage());
            throw new ResponseStatusException(CONFLICT, e.getMessage());
        }
    }

    /**
     * resets the password of a user
     *
     * @param passwordResetRequest the information needed to reset the password
     * @return HttpStatus
     */
    @PutMapping("/reset")
    public ResponseEntity<?> reset(@Valid @RequestBody PasswordResetRequest passwordResetRequest) {
        try {
            passwordService.resetPassword(passwordResetRequest);
            log.info("Password changed");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            log.error("Error while activating account: " + e.getMessage());
            throw new ResponseStatusException(NOT_FOUND, e.getMessage());
        }
    }
}
