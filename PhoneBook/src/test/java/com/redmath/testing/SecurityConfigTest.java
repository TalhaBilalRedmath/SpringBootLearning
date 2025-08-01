package com.redmath.testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redmath.Config.OAuthConfig;
import org.redmath.Config.SecurityConfig;
import org.redmath.Service.UserService;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class SecurityConfigTest {

    @InjectMocks
    private SecurityConfig securityConfig;

    @Mock
    private OAuthConfig oAuthConfig;

    @Mock
    private UserService userService;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private HttpSecurity httpSecurity;

    private byte[] testSigningKey;

    @BeforeEach
    void setUp() {
        testSigningKey = "test-signing-key-for-jwt-tokens-must-be-long-enough".getBytes();
    }

    @Test
    void testPasswordEncoderBean() {
        // Test that passwordEncoder returns BCryptPasswordEncoder
        var passwordEncoder = securityConfig.passwordEncoder();

        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);

        // Test encoding functionality
        String rawPassword = "testPassword123";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(passwordEncoder.matches(rawPassword, encodedPassword));
    }

    @Test
    void testJwtAuthenticationConverterBean() {
        // Test JWT authentication converter configuration
        JwtAuthenticationConverter converter = securityConfig.jwtAuthenticationConverter();

        assertNotNull(converter);

        // Use reflection to verify internal configuration
        JwtGrantedAuthoritiesConverter authoritiesConverter =
                (JwtGrantedAuthoritiesConverter) ReflectionTestUtils.getField(converter, "jwtGrantedAuthoritiesConverter");

        assertNotNull(authoritiesConverter);
    }

    @Test
    void testJwtEncoderBean() {
        // Test JWT encoder bean creation
        JwtEncoder encoder = securityConfig.jwtEncoder(testSigningKey);

        assertNotNull(encoder);
    }

    @Test
    void testJwtDecoderBean() {
        // Test JWT decoder bean creation
        JwtDecoder decoder = securityConfig.jwtDecoder(testSigningKey);

        assertNotNull(decoder);
    }


    @Test
    void testGenerateJwtTokenMethod() throws Exception {
        // Create a mock authentication object
        org.springframework.security.core.Authentication mockAuth = mock(org.springframework.security.core.Authentication.class);
        when(mockAuth.getName()).thenReturn("testuser@example.com");
        when(mockAuth.getAuthorities()).thenReturn(java.util.Collections.emptyList());

        // Create a real JWT encoder for testing
        JwtEncoder realJwtEncoder = securityConfig.jwtEncoder(testSigningKey);

        // Use reflection to access the private method
        java.lang.reflect.Method generateJwtTokenMethod = SecurityConfig.class
                .getDeclaredMethod("generateJwtToken", org.springframework.security.core.Authentication.class, JwtEncoder.class);
        generateJwtTokenMethod.setAccessible(true);

        // Test token generation
        String token = (String) generateJwtTokenMethod.invoke(securityConfig, mockAuth, realJwtEncoder);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.length() > 50); // JWT tokens are typically much longer
    }

    @Test
    void testGenerateJwtTokenWithAuthorities() throws Exception {
        // Create a mock authentication with authorities
        org.springframework.security.core.Authentication mockAuth = mock(org.springframework.security.core.Authentication.class);
        when(mockAuth.getName()).thenReturn("admin@example.com");

        java.util.List<org.springframework.security.core.GrantedAuthority> authorities =
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN"));

        // Use doReturn instead of when().thenReturn() for generic types
        doReturn(authorities).when(mockAuth).getAuthorities();

        // Create a real JWT encoder for testing
        JwtEncoder realJwtEncoder = securityConfig.jwtEncoder(testSigningKey);

        // Use reflection to access the private method
        java.lang.reflect.Method generateJwtTokenMethod = SecurityConfig.class
                .getDeclaredMethod("generateJwtToken", org.springframework.security.core.Authentication.class, JwtEncoder.class);
        generateJwtTokenMethod.setAccessible(true);

        // Test token generation with authorities
        String token = (String) generateJwtTokenMethod.invoke(securityConfig, mockAuth, realJwtEncoder);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Decode and verify the token contains authorities
        JwtDecoder decoder = securityConfig.jwtDecoder(testSigningKey);
        var jwt = decoder.decode(token);

        assertNotNull(jwt.getClaimAsStringList("authorities"));
        assertTrue(jwt.getClaimAsStringList("authorities").contains("ROLE_ADMIN"));
    }

    @Test
    void testJwtTokenExpiration() throws Exception {
        // Test that JWT tokens have proper expiration
        org.springframework.security.core.Authentication mockAuth = mock(org.springframework.security.core.Authentication.class);
        when(mockAuth.getName()).thenReturn("testuser@example.com");
        when(mockAuth.getAuthorities()).thenReturn(java.util.Collections.emptyList());

        JwtEncoder realJwtEncoder = securityConfig.jwtEncoder(testSigningKey);

        java.lang.reflect.Method generateJwtTokenMethod = SecurityConfig.class
                .getDeclaredMethod("generateJwtToken", org.springframework.security.core.Authentication.class, JwtEncoder.class);
        generateJwtTokenMethod.setAccessible(true);

        String token = (String) generateJwtTokenMethod.invoke(securityConfig, mockAuth, realJwtEncoder);

        // Decode token and check expiration
        JwtDecoder decoder = securityConfig.jwtDecoder(testSigningKey);
        var jwt = decoder.decode(token);

        assertNotNull(jwt.getExpiresAt());
        assertTrue(jwt.getExpiresAt().isAfter(java.time.Instant.now()));

        // Check that expiration is approximately 1 hour from now (3600 seconds)
        long expirationDiff = jwt.getExpiresAt().getEpochSecond() - java.time.Instant.now().getEpochSecond();
        assertTrue(expirationDiff > 3500 && expirationDiff <= 3600);
    }

    @Test
    void testJwtEncoderWithDifferentKeys() {
        // Test with different signing keys
        byte[] key1 = "first-test-signing-key-for-jwt-tokens-must-be-long".getBytes();
        byte[] key2 = "second-test-signing-key-for-jwt-tokens-must-be-long".getBytes();

        JwtEncoder encoder1 = securityConfig.jwtEncoder(key1);
        JwtEncoder encoder2 = securityConfig.jwtEncoder(key2);

        assertNotNull(encoder1);
        assertNotNull(encoder2);
        assertNotSame(encoder1, encoder2);
    }

    @Test
    void testJwtDecoderWithDifferentKeys() {
        // Test with different signing keys
        byte[] key1 = "first-test-signing-key-for-jwt-tokens-must-be-long".getBytes();
        byte[] key2 = "second-test-signing-key-for-jwt-tokens-must-be-long".getBytes();

        JwtDecoder decoder1 = securityConfig.jwtDecoder(key1);
        JwtDecoder decoder2 = securityConfig.jwtDecoder(key2);

        assertNotNull(decoder1);
        assertNotNull(decoder2);
        assertNotSame(decoder1, decoder2);
    }

    @Test
    void testPasswordEncoderEncryption() {
        // Test password encoder with multiple passwords
        var passwordEncoder = securityConfig.passwordEncoder();

        String[] testPasswords = {"password123", "admin", "test@123", "verylongpasswordwithspecialchars!@#"};

        for (String password : testPasswords) {
            String encoded = passwordEncoder.encode(password);
            assertNotNull(encoded);
            assertNotEquals(password, encoded);
            assertTrue(passwordEncoder.matches(password, encoded));
            assertFalse(passwordEncoder.matches("wrongpassword", encoded));
        }
    }
}