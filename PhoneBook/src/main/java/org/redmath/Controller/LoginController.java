package org.redmath.Controller;

import lombok.Getter;
import lombok.Setter;
import org.redmath.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtEncoder jwtEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            System.out.println("🔍 Login attempt for username: " + loginRequest.getUsername());

            UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsername());

            System.out.println("🔍 User found: " + userDetails.getUsername());
            System.out.println("🔍 Stored password (BCrypt): " + userDetails.getPassword());
            System.out.println("🔍 Input password (raw): " + loginRequest.getPassword());
            System.out.println("🔐 Authorities in token: " + userDetails.getAuthorities());

            boolean passwordMatches = passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword());
            System.out.println("🔍 Password matches: " + passwordMatches);

            if (!passwordMatches) {
                System.out.println("❌ Password validation failed");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Invalid credentials"));
            }

            System.out.println("✅ Password validation successful");

            long expirySeconds = 3600;
            JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

            JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                    .subject(userDetails.getUsername())
                    .claim("authorities", userDetails.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList())
                    .expiresAt(Instant.now().plusSeconds(expirySeconds))
                    .build();

            Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet));

            return ResponseEntity.ok(Map.of(
                    "token_type", "Bearer",
                    "access_token", jwt.getTokenValue(),
                    "expires_in", expirySeconds,
                    "email", userDetails.getUsername()
            ));

        } catch (Exception e) {
            System.out.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid credentials: " + e.getMessage()));
        }
    }

    // DTO for login request
    @Getter
    @Setter
    public static class LoginRequest {
        private String username;
        private String password;

        // Constructors
        public LoginRequest() {}

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }


    }
}