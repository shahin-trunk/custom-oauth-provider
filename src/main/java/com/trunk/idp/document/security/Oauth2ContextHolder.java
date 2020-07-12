package com.trunk.idp.document.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Oauth2ContextHolder {
    private String grant_type;
    private ClientCredential clientCredential;
    private UserCredential userCredential;
}
