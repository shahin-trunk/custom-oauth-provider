package com.trunk.idp.component.impl;

import com.trunk.idp.component.ClientDetailsChecker;
import com.trunk.idp.component.ReactiveClientAuthenticationManager;
import com.trunk.idp.component.ReactiveClientDetailsService;
import com.trunk.idp.document.security.ClientCredential;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClientDetailsRepositoryReactiveAuthenticationManager implements ReactiveClientAuthenticationManager {

    private final ReactiveClientDetailsService reactiveClientDetailsService;
    private final PasswordEncoder passwordEncoder;

    private final ClientDetailsChecker preAuthenticationChecks = client -> {
        if (!client.getEnabled()) {
            final String message = String.format("Client account:%s is disabled.", client.getName());
            log.error(message);
            throw new DisabledException(message);
        }
    };

    @Override
    public Mono<Authentication> authenticate(ClientCredential clientCredential) {
        return reactiveClientDetailsService.findByClientId(clientCredential.getClientId())
                .doOnNext(this.preAuthenticationChecks::check)
                .publishOn(Schedulers.boundedElastic())
                .filter(client -> this.passwordEncoder.matches(clientCredential.getClientSecret(), client.getClientSecret()))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid client credentials."))))
                .map(client -> new UsernamePasswordAuthenticationToken(client, client.getClientSecret(), client.getAuthorities()));
    }
}
