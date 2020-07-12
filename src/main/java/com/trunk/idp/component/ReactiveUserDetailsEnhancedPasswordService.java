package com.trunk.idp.component;

import reactor.core.publisher.Mono;

public interface ReactiveUserDetailsEnhancedPasswordService {

    Mono<UserDetailsEnhanced> updatePassword(UserDetailsEnhanced user, String newPassword);

}
