package com.trunk.idp.component;

import com.trunk.idp.document.persistence.Client;
import reactor.core.publisher.Mono;

public interface ReactiveClientDetailsService {
    Mono<Client> findByClientId(String clientId);
}
