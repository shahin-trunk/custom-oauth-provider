package com.trunk.idp.component;

import com.trunk.idp.document.persistence.Client;

public interface ClientDetailsChecker {
    void check(Client toCheck);
}
