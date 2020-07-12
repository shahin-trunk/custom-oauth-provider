package com.trunk.idp.service;

import com.trunk.idp.component.ReactiveClientAuthenticationManager;
import com.trunk.idp.component.TokenManager;
import com.trunk.idp.component.WebExchangeCredentialParser;
import com.trunk.idp.document.security.ClientCredential;
import com.trunk.idp.document.security.Oauth2Token;
import com.trunk.idp.document.security.UserCredential;
import com.trunk.idp.support.StringConstants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final @NonNull ReactiveAuthenticationManager userAuthenticationManager;
    private final @NonNull WebExchangeCredentialParser webExchangeCredentialParser;
    private final @NonNull ReactiveClientAuthenticationManager clientAuthenticationManager;
    private final @NonNull TokenManager tokenManager;

    public Mono<Oauth2Token> authenticate(ServerWebExchange serverWebExchange) {
        return Mono.justOrEmpty(serverWebExchange)
                .flatMap(webExchangeCredentialParser::grantType)
                .flatMap(grantType -> Objects.equals(grantType, StringConstants.GRANT_TYPE_CLIENT_CREDENTIALS)
                        ? this.doClientAuthentication(webExchangeCredentialParser.clientCredential(serverWebExchange))
                        : this.doUserAuthentication(webExchangeCredentialParser.clientCredential(serverWebExchange), webExchangeCredentialParser.userCredential(serverWebExchange))
                )
                .flatMap(tokenManager::getTokenFromAuthentication);
    }

    public Mono<JSONObject> jwkSet() {
        return Mono.just(tokenManager.getJwkSet());
    }

    private Mono<Authentication> doUserAuthentication(Mono<ClientCredential> clientCredential, Mono<UserCredential> userCredential) {
        return clientCredential
                .flatMap(clientAuthenticationManager::authenticate)
                .filter(Authentication::isAuthenticated)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new InsufficientAuthenticationException("Client authentication failed."))))
                .flatMap(clientAuthentication -> userCredential)
                .map(uc -> new UsernamePasswordAuthenticationToken(uc.getUsername(), uc.getPassword()))
                .flatMap(userAuthenticationManager::authenticate);
    }

    private Mono<Authentication> doClientAuthentication(Mono<ClientCredential> clientCredential) {
        return clientCredential.flatMap(clientAuthenticationManager::authenticate);
    }


}
