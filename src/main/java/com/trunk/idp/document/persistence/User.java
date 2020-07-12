package com.trunk.idp.document.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.lang.NonNull;
import com.trunk.idp.component.UserDetailsEnhanced;
import com.trunk.idp.support.StringConstants;
import com.trunk.idp.document.security.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document
public class User implements UserDetailsEnhanced, CredentialsContainer {

    @Id
    private String userId;

    @NonNull
    @JsonIgnore
    private String password;

    @NonNull
    private String mobileNumber;

    @NonNull
    private String countryCode;

    private String email;

    private String name;

    private Set<UserRole> userRoles;

    private Boolean accountNonExpired;

    private Boolean accountNonLocked;

    private Boolean credentialsNonExpired;

    private Boolean enabled;

    private Boolean mobileNumberVerified;

    private Boolean mobileNumberConfirmed;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.userRoles.stream().map(userRole -> new SimpleGrantedAuthority(userRole.name())).collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return this.countryCode.concat(StringConstants.PERIOD).concat(this.mobileNumber);
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void eraseCredentials() {
        setPassword(StringConstants.BLANK);
    }

    @Override
    public String identifier() {
        return this.getUserId();
    }
}
