package com.trunk.idp.service;

import com.trunk.idp.component.ReactiveClientDetailsService;
import com.trunk.idp.document.persistence.Client;
import com.trunk.idp.repository.ClientRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PersistentReactiveClientDetailsService implements ReactiveClientDetailsService {

    private static final Map<String, Client> IN_MEMORY_CLIENT_MAP = new LinkedHashMap<>();
    private final @NonNull ClientRepository clientRepository;

    @Override
    public Mono<Client> findByClientId(String clientId) {
        return Mono.just(IN_MEMORY_CLIENT_MAP)
                .flatMap(map -> map.containsKey(clientId) ? Mono.just(map.get(clientId)) : clientRepository.findByClientId(clientId))
                .map(client -> {
                    if (!IN_MEMORY_CLIENT_MAP.containsKey(clientId)) {
                        IN_MEMORY_CLIENT_MAP.put(clientId, client);
                    }
                    return client;
                })
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new ProviderNotFoundException(String.format("Client provider: %s not found.", clientId)))));

    }
}
