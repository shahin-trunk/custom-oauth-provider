package com.trunk.idp.document.persistence;

import com.trunk.idp.document.security.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document
public class Client implements CredentialsContainer {
    private String id;
    private String name;
    @Indexed(unique = true)
    private String clientId;
    private String clientSecret;
    private Set<UserRole> clientRoles;
    private Boolean enabled;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.clientRoles.stream().map(userRole -> new SimpleGrantedAuthority(userRole.name())).collect(Collectors.toSet());
    }

    @Override
    public void eraseCredentials() {
        this.setClientSecret("");
    }
}
