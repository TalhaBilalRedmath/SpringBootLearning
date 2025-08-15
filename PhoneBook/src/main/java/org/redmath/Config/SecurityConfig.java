package org.redmath.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.servlet.http.HttpServletResponse;
import org.redmath.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Configuration
@EnableWebSecurity
@OpenAPIDefinition(
        info = @Info(title = "Your API", version = "v1"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class SecurityConfig {

    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/configuration/**",
            "/webjars/**"
    };

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/login",
            "/actuator",
            "/swagger-ui/index.html",
            "/api/getContacts",
            "/login",
            "/api/saveContact",
            "/h2-console/**",
            "/putTesting.html",
            "/users",
            "/csrf"
    };

    @Autowired
    private OAuthConfig oAuthConfig;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthoritiesClaimName("authorities");
        authoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }

    @Bean
    public AuthenticationSuccessHandler formLoginSuccessHandler(JwtEncoder jwtEncoder) {
        return (request, response, authentication) -> writeJson(response,
                tokenResponse(generateJwtToken(authentication, jwtEncoder), authentication.getName()));
    }

    @Bean
    public AuthenticationSuccessHandler oauth2LoginSuccessHandler(JwtEncoder jwtEncoder) {
        return (request, response, authentication) -> {
            String jwtToken = generateJwtToken(authentication, jwtEncoder);
            String redirectUrl = "http:localhost:3000/?token=" + jwtToken + "&email=" + authentication.getName();
            response.sendRedirect(redirectUrl);
        };
    }

    @Bean
    public AuthenticationFailureHandler formLoginFailureHandler() {
        return (request, response, exception) -> writeJsonError(response, HttpServletResponse.SC_UNAUTHORIZED,
                Map.of("error", "Invalid credentials", "message", exception.getMessage()));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           UserService userService,
                                           JwtEncoder jwtEncoder) throws Exception {
        return http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SWAGGER_WHITELIST).permitAll()
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .requestMatchers("/api/deleteAll", "/api/deleteContact/{id}", "/api/updateContact").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .successHandler(formLoginSuccessHandler(jwtEncoder))
                        .failureHandler(formLoginFailureHandler())
                )
                .httpBasic(config -> config.disable())
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuthConfig))
                        .successHandler(oauth2LoginSuccessHandler(jwtEncoder))
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .userDetailsService(userService)
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                )
                .build();
    }

    private String generateJwtToken(org.springframework.security.core.Authentication auth, JwtEncoder jwtEncoder) {
        long expirySeconds = 3600;
        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(auth.getName())
                .claim("authorities", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .expiresAt(Instant.now().plusSeconds(expirySeconds))
                .build();
        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet));
        return jwt.getTokenValue();
    }

    private Map<String, Object> tokenResponse(String token, String email) {
        return Map.of(
                "token_type", "Bearer",
                "access_token", token,
                "expires_in", 3600,
                "email", email
        );
    }

    private void writeJson(HttpServletResponse response, Map<String, Object> body) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
    }

    private void writeJsonError(HttpServletResponse response, int status, Map<String, Object> body) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status);
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
    }

    @Bean
    public JwtEncoder jwtEncoder(@Value("${jwt.signing.key}") byte[] signingKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(new SecretKeySpec(signingKey, "HmacSHA256")));
    }

    @Bean
    public JwtDecoder jwtDecoder(@Value("${jwt.signing.key}") byte[] signingKey) {
        return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(signingKey, "HmacSHA256")).build();
    }
}
