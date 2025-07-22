package org.redmath.Config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.redmath.Service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, UserService userService, JwtEncoder jwtEncoder) throws Exception {
        System.out.println("Security config loaded");
        return http
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/getContacts", "/api/saveContact", "/deleteUser/{id}").permitAll()
                        .requestMatchers("/h2-console/**", "/users/add", "/users").permitAll()
                        .requestMatchers("/api/deleteAll").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(config -> config.successHandler((request, response, auth) -> {
                    long expirySeconds = 3600;
                    JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

                    JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                            .subject(auth.getName())
                            .claim("authorities",auth.getAuthorities().stream()
                                    .map(GrantedAuthority::getAuthority)
                                    .toList())
                            .expiresAt(Instant.now().plusSeconds(expirySeconds))
                            .build();
                    Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, jwtClaimsSet));
                    String tokenResponse = "{\"token_type\":\"Bearer\",\"access_token\":\"" + jwt.getTokenValue()
                            + "\",\"expires_in\":" + expirySeconds + "}";
                    response.getWriter().print(tokenResponse);
                }))
                .httpBasic(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .build();
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
