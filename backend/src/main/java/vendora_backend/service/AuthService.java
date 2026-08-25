package vendora_backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import vendora_backend.model.User;
import vendora_backend.repository.UserRepository;
import vendora_backend.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtService = jwtService;
    }

    public User registerUser(String name, String email, String password) {

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user = new User(
                name,
                email,
                hashedPassword
        );

        return userRepository.save(user);
    }

    public String loginUser(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole()
        );
    }
}