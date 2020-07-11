package com.trunk.idp.configuration;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;

@Configuration
@RequiredArgsConstructor
public class OAuth2Configuration {

    private final @NonNull ReactiveUserDetailsService reactiveUserDetailsService;
    private final @NonNull ReactiveUserDetailsPasswordService reactiveUserDetailsPasswordService;

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        final UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService);
        authenticationManager.setUserDetailsPasswordService(reactiveUserDetailsPasswordService);
        return authenticationManager;
    }

}
