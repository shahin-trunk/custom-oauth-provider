package com.trunk.idp.component;

import reactor.core.publisher.Mono;

public interface ReactiveUserDetailsEnhancedService {

    Mono<UserDetailsEnhanced> findByUsername(String username);

}
