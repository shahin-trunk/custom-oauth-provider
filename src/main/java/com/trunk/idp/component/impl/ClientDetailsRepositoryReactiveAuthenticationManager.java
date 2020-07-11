package com.trunk.idp.component.impl;

import com.trunk.idp.component.ReactiveClientAuthenticationManager;
import com.trunk.idp.component.ReactiveClientDetailsService;
import com.trunk.idp.document.security.ClientCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ClientDetailsRepositoryReactiveAuthenticationManager implements ReactiveClientAuthenticationManager {

    private final ReactiveClientDetailsService reactiveClientDetailsService;

    @Override
    public Mono<Authentication> authenticate(ClientCredential clientCredential) {
        return reactiveClientDetailsService.findByClientId(clientCredential.getClientId())
                .
    }
}
