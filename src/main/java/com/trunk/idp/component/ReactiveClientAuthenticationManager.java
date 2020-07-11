package com.trunk.idp.component;

import com.trunk.idp.document.security.ClientCredential;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public interface ReactiveClientAuthenticationManager {
    Mono<Authentication> authenticate(ClientCredential clientCredential);
}
