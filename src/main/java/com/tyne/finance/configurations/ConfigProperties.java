package com.tyne.finance.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConfigurationProperties(prefix = "tyne")
public class ConfigProperties {
    @NestedConfigurationProperty
    private Security security;

    @Data
    public static class Security {
        private String secretKey;
        private long tokenExpirationTime;
        private String hashAlgorithm;
        private int hashIterations;
        private int hashLen;
        private int saltLen;
        private String commonPasswordsFile;
        private int passwordMinLen;
        private double maxPasswordSimilarity;
    }
}
