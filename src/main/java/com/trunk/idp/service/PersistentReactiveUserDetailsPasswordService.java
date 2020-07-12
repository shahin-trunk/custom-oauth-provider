package com.trunk.idp.service;

import com.trunk.idp.component.ReactiveUserDetailsEnhancedPasswordService;
import com.trunk.idp.component.UserDetailsEnhanced;
import com.trunk.idp.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PersistentReactiveUserDetailsPasswordService implements ReactiveUserDetailsEnhancedPasswordService {

    private final @NonNull UserRepository userRepository;

    @Override
    public Mono<UserDetailsEnhanced> updatePassword(UserDetailsEnhanced userDetails, String newPassword) {
        return userRepository.findById(userDetails.identifier())
                .flatMap(user -> {
                    user.setPassword(newPassword);
                    return userRepository.save(user);
                }).cast(UserDetailsEnhanced.class);
    }
}
