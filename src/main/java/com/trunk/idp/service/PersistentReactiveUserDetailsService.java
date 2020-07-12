package com.trunk.idp.service;

import com.trunk.idp.component.ReactiveUserDetailsEnhancedService;
import com.trunk.idp.component.UserDetailsEnhanced;
import com.trunk.idp.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PersistentReactiveUserDetailsService implements ReactiveUserDetailsEnhancedService {

    private final @NonNull UserRepository userRepository;

    @Override
    public Mono<UserDetailsEnhanced> findByUsername(String username) {
        String[] usernameDetails = username.split("\\.");
        return userRepository.findByCountryCodeAndMobileNumber(usernameDetails[0], usernameDetails[1]).cast(UserDetailsEnhanced.class);
    }
}
