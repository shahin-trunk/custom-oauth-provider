package com.trunk.idp.repository;

import com.trunk.idp.document.persistence.Client;
import com.trunk.idp.document.persistence.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ClientRepository extends ReactiveMongoRepository<Client, String> {
    Mono<Client> findByClientId(String clientId);
}
