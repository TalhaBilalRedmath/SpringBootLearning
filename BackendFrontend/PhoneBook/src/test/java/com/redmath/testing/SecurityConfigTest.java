package com.redmath.testing;

import org.junit.jupiter.api.Test;
import org.redmath.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Main.class)
public class SecurityConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void contextLoads() {
        assertThat(context).isNotNull();
    }

    @Test
    public void jwtEncoderBeanExists() {
        assertThat(context.getBean(JwtEncoder.class)).isNotNull();
    }

    @Test
    public void jwtDecoderBeanExists() {
        assertThat(context.getBean(JwtDecoder.class)).isNotNull();
    }

    @Test
    public void passwordEncoderBeanExists() {
        assertThat(context.getBean(PasswordEncoder.class)).isNotNull();
    }

    @Test
    public void securityFilterChainLoads() {
        assertThat(context.getBean(SecurityFilterChain.class)).isNotNull();
    }
}
