package com.redmath.testing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redmath.Main;
import org.redmath.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = Main.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtEncoder jwtEncoder;

    @Test
    @Order(1)
    public void testLoginWithValidCredentials() throws Exception {
        // Setup mock user details
        List<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("READ_PRIVILEGE")
        );

        UserDetails mockUserDetails = User.builder()
                .username("testuser@example.com")
                .password("$2a$10$encodedHashedPassword")
                .authorities(authorities)
                .build();

        // Setup mock JWT
        Jwt mockJwt = Jwt.withTokenValue("mock-jwt-token")
                .header("alg", "HS256")
                .claim("sub", "testuser@example.com")
                .claim("authorities", Arrays.asList("ROLE_USER", "READ_PRIVILEGE"))
                .claim("exp", Instant.now().plusSeconds(3600))
                .build();

        // Configure mocks
        when(userService.loadUserByUsername("testuser@example.com")).thenReturn(mockUserDetails);
        when(passwordEncoder.matches("password123", "$2a$10$encodedHashedPassword")).thenReturn(true);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        String loginJson = """
                {
                    "username": "testuser@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token_type").value("Bearer"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expires_in").value(3600))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("testuser@example.com"));
    }

    @Test
    @Order(2)
    public void testLoginWithWrongPassword() throws Exception {
        // Setup mock user details
        List<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        UserDetails mockUserDetails = User.builder()
                .username("testuser@example.com")
                .password("$2a$10$encodedHashedPassword")
                .authorities(authorities)
                .build();

        // Configure mocks
        when(userService.loadUserByUsername("testuser@example.com")).thenReturn(mockUserDetails);
        when(passwordEncoder.matches("wrongpassword", "$2a$10$encodedHashedPassword")).thenReturn(false);

        String loginJson = """
                {
                    "username": "testuser@example.com",
                    "password": "wrongpassword"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(3)
    public void testLoginWithNonExistentUser() throws Exception {
        // Configure mock to throw exception
        when(userService.loadUserByUsername("nonexistent@example.com"))
                .thenThrow(new UsernameNotFoundException("User not found"));

        String loginJson = """
                {
                    "username": "nonexistent@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(4)
    public void testLoginWithEmptyUsername() throws Exception {
        String loginJson = """
                {
                    "username": "",
                    "password": "password123"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(5)
    public void testLoginWithEmptyPassword() throws Exception {
        String loginJson = """
                {
                    "username": "testuser@example.com",
                    "password": ""
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(6)
    public void testLoginWithNullUsername() throws Exception {
        String loginJson = """
                {
                    "password": "password123"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(7)
    public void testLoginWithNullPassword() throws Exception {
        String loginJson = """
                {
                    "username": "testuser@example.com"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(8)
    public void testLoginWithAdminUser() throws Exception {
        // Setup mock admin user details
        List<GrantedAuthority> adminAuthorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("WRITE_PRIVILEGE"),
                new SimpleGrantedAuthority("DELETE_PRIVILEGE")
        );

        UserDetails adminUserDetails = User.builder()
                .username("admin@example.com")
                .password("$2a$10$adminHashedPassword")
                .authorities(adminAuthorities)
                .build();

        // Setup mock JWT
        Jwt mockJwt = Jwt.withTokenValue("mock-admin-jwt-token")
                .header("alg", "HS256")
                .claim("sub", "admin@example.com")
                .claim("authorities", Arrays.asList("ROLE_ADMIN", "WRITE_PRIVILEGE", "DELETE_PRIVILEGE"))
                .claim("exp", Instant.now().plusSeconds(3600))
                .build();

        // Configure mocks
        when(userService.loadUserByUsername("admin@example.com")).thenReturn(adminUserDetails);
        when(passwordEncoder.matches("adminpass", "$2a$10$adminHashedPassword")).thenReturn(true);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        String loginJson = """
                {
                    "username": "admin@example.com",
                    "password": "adminpass"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token_type").value("Bearer"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expires_in").value(3600))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("admin@example.com"));
    }

    @Test
    @Order(9)
    public void testLoginWithSpecialCharactersInUsername() throws Exception {
        // Setup mock user details
        List<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        UserDetails mockUserDetails = User.builder()
                .username("user@123.com")
                .password("$2a$10$encodedHashedPassword")
                .authorities(authorities)
                .build();

        // Setup mock JWT
        Jwt mockJwt = Jwt.withTokenValue("mock-special-jwt-token")
                .header("alg", "HS256")
                .claim("sub", "user@123.com")
                .claim("authorities", Arrays.asList("ROLE_USER"))
                .claim("exp", Instant.now().plusSeconds(3600))
                .build();

        // Configure mocks
        when(userService.loadUserByUsername("user@123.com")).thenReturn(mockUserDetails);
        when(passwordEncoder.matches("password123", "$2a$10$encodedHashedPassword")).thenReturn(true);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        String loginJson = """
                {
                    "username": "user@123.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token_type").value("Bearer"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("user@123.com"));
    }

    @Test
    @Order(10)
    public void testLoginWithEmptyRequestBody() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(11)
    public void testLoginWithJwtEncodingException() throws Exception {
        // Setup mock user details
        List<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        UserDetails mockUserDetails = User.builder()
                .username("testuser@example.com")
                .password("$2a$10$encodedHashedPassword")
                .authorities(authorities)
                .build();

        // Configure mocks - JWT encoding will fail
        when(userService.loadUserByUsername("testuser@example.com")).thenReturn(mockUserDetails);
        when(passwordEncoder.matches("password123", "$2a$10$encodedHashedPassword")).thenReturn(true);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenThrow(new JwtEncodingException("JWT encoding failed"));

        String loginJson = """
                {
                    "username": "testuser@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @Order(12)
    public void testLoginSuccessWithLongUsername() throws Exception {
        String longUsername = "verylongusernamethatmightcauseissuesifnothandledproperly@example.com";

        // Setup mock user details
        List<GrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        UserDetails mockUserDetails = User.builder()
                .username(longUsername)
                .password("$2a$10$encodedHashedPassword")
                .authorities(authorities)
                .build();

        // Setup mock JWT
        Jwt mockJwt = Jwt.withTokenValue("mock-long-jwt-token")
                .header("alg", "HS256")
                .claim("sub", longUsername)
                .claim("authorities", Arrays.asList("ROLE_USER"))
                .claim("exp", Instant.now().plusSeconds(3600))
                .build();

        // Configure mocks
        when(userService.loadUserByUsername(longUsername)).thenReturn(mockUserDetails);
        when(passwordEncoder.matches("password123", "$2a$10$encodedHashedPassword")).thenReturn(true);
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

        String loginJson = String.format("""
                {
                    "username": "%s",
                    "password": "password123"
                }
                """, longUsername);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.access_token").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token_type").value("Bearer"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(longUsername));
    }
}