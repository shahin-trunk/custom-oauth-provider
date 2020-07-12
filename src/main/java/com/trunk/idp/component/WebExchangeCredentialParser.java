package com.trunk.idp.component;

import com.trunk.idp.document.security.ClientCredential;
import com.trunk.idp.document.security.Oauth2ContextHolder;
import com.trunk.idp.document.security.UserCredential;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface WebExchangeCredentialParser {

    Mono<String> grantType(ServerWebExchange serverWebExchange);

    Mono<ClientCredential> clientCredential(ServerWebExchange serverWebExchange);

    Mono<UserCredential> userCredential(ServerWebExchange serverWebExchange);

}

