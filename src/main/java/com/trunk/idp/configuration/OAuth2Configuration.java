package com.trunk.idp.configuration;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;

@Configuration
@RequiredArgsConstructor
public class OAuth2Configuration {

    private final @NonNull ReactiveUserDetailsService reactiveUserDetailsService;

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService);
    }

}
