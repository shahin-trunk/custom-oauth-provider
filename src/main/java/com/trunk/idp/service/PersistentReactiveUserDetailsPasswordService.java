package com.trunk.idp.service;

import com.trunk.idp.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PersistentReactiveUserDetailsPasswordService implements ReactiveUserDetailsPasswordService {

    private final @NonNull UserRepository userRepository;

    @Override
    public Mono<UserDetails> updatePassword(UserDetails userDetails, String newPassword) {
        String[] usernameDetails = userDetails.getUsername().split("\\.");
        return userRepository.findByCountryCodeAndMobileNumber(usernameDetails[0], usernameDetails[1])
                .flatMap(user -> {
                    user.setPassword(newPassword);
                    return userRepository.save(user);
                }).cast(UserDetails.class);
    }
}
