package com.trunk.idp.repository;

import com.trunk.idp.document.persistence.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByCountryCodeAndMobileNumber(String countryCode, String mobileNumber);
}
