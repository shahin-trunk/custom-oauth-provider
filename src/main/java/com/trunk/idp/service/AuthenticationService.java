package com.trunk.idp.service;

import com.trunk.idp.component.WebExchangeCredentialParser;
import com.trunk.idp.document.security.ClientCredential;
import com.trunk.idp.document.security.Oauth2Token;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final @NonNull ReactiveAuthenticationManager authenticationManager;
    private final @NonNull WebExchangeCredentialParser webExchangeCredentialParser;

    public Mono<Oauth2Token> authenticate(ServerWebExchange serverWebExchange) {
        final Mono<ClientCredential> clientCredentialMono = Mono.justOrEmpty(serverWebExchange)
                .flatMap(webExchangeCredentialParser::clientCredential);
        final Mono<Authentication> authentication = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("user", "pass"));
        return Mono.just(new Oauth2Token());
    }

}
