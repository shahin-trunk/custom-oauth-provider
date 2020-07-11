package com.trunk.idp.service;

import com.trunk.idp.component.ReactiveClientAuthenticationManager;
import com.trunk.idp.component.WebExchangeCredentialParser;
import com.trunk.idp.document.security.ClientCredential;
import com.trunk.idp.document.security.Oauth2Token;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final @NonNull ReactiveAuthenticationManager userAuthenticationManager;
    private final @NonNull WebExchangeCredentialParser webExchangeCredentialParser;
    private final @NonNull ReactiveClientAuthenticationManager clientAuthenticationManager;
    UserDetailsRepositoryReactiveAuthenticationManager

    public Mono<Oauth2Token> authenticate(ServerWebExchange serverWebExchange) {
        Mono.justOrEmpty(serverWebExchange)
                .flatMap(webExchangeCredentialParser::clientCredential)
                .flatMap(clientAuthenticationManager::authenticate)
                .doOnNext(webExchangeCredentialParser.userCredential(serverWebExchange))
                .flatMap(userAuthenticationManager.authenticate())
        final Mono<Authentication> authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("user", "pass"));
        return Mono.just(new Oauth2Token());
    }

}
