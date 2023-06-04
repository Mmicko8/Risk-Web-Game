package kdg.be.riskbackend.identity.api_controllers;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class is used for testing purposes only.
 */
@RestController
@RequestMapping(path = "/api/test")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5000","http://localhost","http://127.0.0.1",
        "http://frontend-service", "http://backend-service", "http://ai-service", "http://www.risk.gq",
        "https://localhost:3000", "https://localhost:5000","https://localhost","https://127.0.0.1",
        "https://frontend-service", "https://backend-service", "https://ai-service", "https://www.risk.gq"})
public class TestController {
    /**
     * This method is used for testing purposes only.
     */
    @GetMapping("/test")
    public String register() {
        return "hello";
    }

    @GetMapping("/openEndpoint")
    public ResponseEntity<String> openEndpoint() {
        return new ResponseEntity<>("success", HttpStatus.OK);
    }
}
