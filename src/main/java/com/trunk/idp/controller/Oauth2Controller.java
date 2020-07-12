package com.trunk.idp.controller;

import com.trunk.idp.document.security.Oauth2Token;
import com.trunk.idp.service.AuthenticationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class Oauth2Controller {

    private final @NonNull AuthenticationService authenticationService;

    @PostMapping("/token")
    public ResponseEntity<Mono<Oauth2Token>> token(ServerWebExchange exchange){
        return ResponseEntity.ok(authenticationService.authenticate(exchange));
    }

    @GetMapping("/.well-known/jwks.json")
    public ResponseEntity<Mono<JSONObject>> jwkSet(){
        return ResponseEntity.ok(authenticationService.jwkSet());
    }
}
