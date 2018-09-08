package com.neperix.osiris.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author petarmitrovic
 */
@Data
@ConfigurationProperties(prefix = "com.neperix.osiris.messaging")
public class InProcessMessagingProperties {

    private SourceProperties source = new SourceProperties();

    @Data
    public static final class SourceProperties {

        private int threads = 10;
    }
}
