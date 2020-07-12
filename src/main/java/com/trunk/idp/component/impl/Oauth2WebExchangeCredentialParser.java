package com.trunk.idp.component.impl;

import com.trunk.idp.component.WebExchangeCredentialParser;
import com.trunk.idp.document.security.ClientCredential;
import com.trunk.idp.document.security.Oauth2ContextHolder;
import com.trunk.idp.document.security.UserCredential;
import com.trunk.idp.support.StringConstants;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

@Component
public class Oauth2WebExchangeCredentialParser implements WebExchangeCredentialParser {

    @Override
    public Mono<String> grantType(ServerWebExchange serverWebExchange) {
        return serverWebExchange.getFormData()
                .filter(map -> map.containsKey(StringConstants.GRANT_TYPE))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid grant type."))))
                .flatMap(map -> Mono.justOrEmpty(map.getFirst(StringConstants.GRANT_TYPE)))
                .filter(grantType -> Objects.equals(grantType, StringConstants.GRANT_TYPE_PASSWORD) || Objects.equals(grantType,StringConstants.GRANT_TYPE_CLIENT_CREDENTIALS))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid grant type."))));
    }

    @Override
    public Mono<ClientCredential> clientCredential(ServerWebExchange serverWebExchange) {
        return Mono.justOrEmpty(serverWebExchange
                .getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION))
                .filter(auth -> auth != null && auth.toLowerCase().startsWith(StringConstants.BASIC))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid client credentials."))))
                .flatMap(this::makeClientCredential);
    }

    private Mono<? extends ClientCredential> makeClientCredential(String authorization) {
        final String[] clientCred = new String(Base64.getDecoder()
                .decode(authorization.substring(StringConstants.BASIC.length()).trim()), StandardCharsets.UTF_8)
                .split(StringConstants.COLON, 2);
        return Mono.just(ClientCredential.builder()
                .clientId(clientCred[0])
                .clientSecret(clientCred[1])
                .build());
    }

    @Override
    public Mono<UserCredential> userCredential(ServerWebExchange serverWebExchange) {
        return serverWebExchange.getFormData()
                .filter(map -> Objects.equals(map.getFirst(StringConstants.GRANT_TYPE), StringConstants.GRANT_TYPE_PASSWORD))
                .switchIfEmpty(Mono.defer(Mono::empty))
                .filter(map -> map.containsKey(StringConstants.USERNAME) && map.containsKey(StringConstants.PASSWORD))
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid user credentials."))))
                .flatMap(map -> Mono.just(UserCredential.builder()
                        .username(map.getFirst(StringConstants.USERNAME))
                        .password(map.getFirst(StringConstants.PASSWORD))
                        .build()));
    }


}
