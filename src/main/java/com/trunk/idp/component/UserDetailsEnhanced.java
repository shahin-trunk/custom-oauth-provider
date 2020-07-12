package com.trunk.idp.component;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsEnhanced extends UserDetails {
    String identifier();
}
