package com.trunk.idp.component;

import com.trunk.idp.document.security.Oauth2Token;
import net.minidev.json.JSONObject;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public interface TokenManager {
    Mono<Oauth2Token> getTokenFromAuthentication(Authentication authentication);

    JSONObject getJwkSet();
}
