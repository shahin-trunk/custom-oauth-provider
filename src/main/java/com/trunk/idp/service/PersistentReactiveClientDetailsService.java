package com.trunk.idp.service;

import com.trunk.idp.component.ReactiveClientDetailsService;
import com.trunk.idp.document.persistence.Client;
import com.trunk.idp.repository.ClientRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PersistentReactiveClientDetailsService implements ReactiveClientDetailsService {

    private final @NonNull ClientRepository clientRepository;

    @Override
    public Mono<Client> findByClientId(String clientId) {
        return clientRepository.findByClientId(clientId);
    }
}
