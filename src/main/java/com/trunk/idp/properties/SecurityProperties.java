package com.trunk.idp.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "spring.security.oauth2")
@Component
public class SecurityProperties {

    public Jwt jwt;

    @Data
    public static class Jwt {
        public String key;
        public String issuer;
        public String audience;
        public String organization;
        public Integer expiry;
        public Integer expiryBuffer;
    }
}
