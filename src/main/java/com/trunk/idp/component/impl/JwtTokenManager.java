package com.trunk.idp.component.impl;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.trunk.idp.component.TokenManager;
import com.trunk.idp.component.UserDetailsEnhanced;
import com.trunk.idp.document.persistence.Client;
import com.trunk.idp.document.security.Oauth2Token;
import com.trunk.idp.properties.SecurityProperties;
import com.trunk.idp.support.StringConstants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenManager implements TokenManager {

    private final @NonNull RSAKey rsaKey;
    private final @NonNull JWSSigner jwsSigner;
    private final @NonNull SecurityProperties securityProperties;

    @Override
    public Mono<Oauth2Token> getTokenFromAuthentication(Authentication authentication) {
        try {
            final JWTClaimsSet claimsSet = this.makeClaimsSetFromAuthentication((UsernamePasswordAuthenticationToken) authentication);
            final SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.RS512).keyID(rsaKey.getKeyID()).build(), claimsSet);
            signedJWT.sign(jwsSigner);
            return Mono.just(Oauth2Token.builder().accessToken(signedJWT.serialize()).build());
        } catch (Exception e) {
            log.error("Failed to generate JWT token", e);
            return Mono.error(e);
        }
    }

    private JWTClaimsSet makeClaimsSetFromAuthentication(UsernamePasswordAuthenticationToken authentication) {
        final Object authenticationPrincipal = authentication.getPrincipal();
        final JWTClaimsSet.Builder claimSetBuilder = new JWTClaimsSet.Builder();
        if(authenticationPrincipal instanceof Client){
            claimSetBuilder.subject(((Client) authenticationPrincipal).getClientId());
        }else if(authenticationPrincipal instanceof UserDetailsEnhanced){
            claimSetBuilder.subject(((UserDetailsEnhanced) authenticationPrincipal).identifier());
        }

        final Date issueTime = Calendar.getInstance().getTime();
        claimSetBuilder.issueTime(issueTime);
        claimSetBuilder.issuer(securityProperties.jwt.issuer);
        claimSetBuilder.jwtID(UUID.randomUUID().toString());
        claimSetBuilder.expirationTime(this.getTokenExpiry());
        claimSetBuilder.audience(securityProperties.jwt.audience);
        claimSetBuilder.claim(StringConstants.ORG, securityProperties.jwt.organization);
        claimSetBuilder.notBeforeTime(issueTime);

        return claimSetBuilder.build();
    }

    private Date getTokenExpiry() {
        final Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, securityProperties.jwt.expiry);
        return instance.getTime();
    }
}
