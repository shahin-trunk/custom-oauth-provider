package com.trunk.idp.configuration;

import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.trunk.idp.properties.SecurityProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class OAuth2Configuration {

    private final @NonNull SecurityProperties securityProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public RSAKey rsaKey() throws Exception {
        return new RSAKeyGenerator(15360)
                .keyID(securityProperties.jwt.key)
                .generate();
    }

    @Bean
    public JWSSigner jwsSigner(@NonNull RSAKey rsaKey) throws Exception {
        return new RSASSASigner(rsaKey);
    }

}
