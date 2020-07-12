package com.trunk.idp.component.impl;

import com.trunk.idp.component.ReactiveUserDetailsEnhancedPasswordService;
import com.trunk.idp.component.ReactiveUserDetailsEnhancedService;
import com.trunk.idp.component.UserDetailsEnhanced;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class UserDetailsEnhancedReactiveAuthenticationManager extends AbstractUserDetailsEnhancedReactiveAuthenticationManager {

    private final ReactiveUserDetailsEnhancedService userDetailsService;

    public UserDetailsEnhancedReactiveAuthenticationManager(@NonNull ReactiveUserDetailsEnhancedService userDetailsService,
                                                            @NonNull ReactiveUserDetailsEnhancedPasswordService userDetailsEnhancedPasswordService) {
        this.userDetailsService = userDetailsService;
        super.setUserDetailsPasswordService(userDetailsEnhancedPasswordService);
    }

    @Override
    protected Mono<UserDetailsEnhanced> retrieveUser(String username) {
        return this.userDetailsService.findByUsername(username);
    }
}
