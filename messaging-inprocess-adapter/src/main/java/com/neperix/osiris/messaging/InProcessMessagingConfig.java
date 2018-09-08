package com.neperix.osiris.messaging;

import java.util.concurrent.Executors;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableConfigurationProperties
@Profile("messaging.inProcess")
class InProcessMessagingConfig {

    @Bean
    AddressBook addressBook() {
        return new InProcessAddressBook();
    }

    @Bean
    Source source(AddressBook addressBook, InProcessMessagingProperties config) {
        return new InProcessSource(
                (InProcessAddressBook) addressBook,
                Executors.newFixedThreadPool(config.getSource().getThreads()));
    }

    @Bean
    InProcessMessagingProperties inProcessMessagingProperties() {
        return new InProcessMessagingProperties();
    }
}
