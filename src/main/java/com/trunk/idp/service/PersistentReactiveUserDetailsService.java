package com.trunk.idp.service;

import com.trunk.idp.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PersistentReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final @NonNull UserRepository userRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        String[] usernameDetails = username.split("\\.");
        return userRepository.findByCountryCodeAndMobileNumber(usernameDetails[0], usernameDetails[1]).cast(UserDetails.class);
    }
}
