package vendora_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vendora_backend.dto.AuthResponse;
import vendora_backend.dto.RegisterRequest;
import vendora_backend.model.User;
import vendora_backend.service.AuthService;
import vendora_backend.dto.LoginRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        User user = authService.registerUser(
                request.getName(),
                request.getEmail(),
                request.getPassword()
        );

        AuthResponse response = new AuthResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
public ResponseEntity<?> login(
        @Valid @RequestBody LoginRequest request) {

    String token = authService.loginUser(
            request.getEmail(),
            request.getPassword()
    );

    return ResponseEntity.ok(
            java.util.Map.of(
                    "message", "Login successful",
                    "token", token
            )
    );
}
}