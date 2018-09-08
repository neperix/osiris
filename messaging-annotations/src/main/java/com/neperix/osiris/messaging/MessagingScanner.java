package com.neperix.osiris.messaging;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import lombok.extern.slf4j.Slf4j;

/**
 * @author petarmitrovic
 */
@Slf4j
@Configuration
@ComponentScan
class MessagingScanner {

    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshed() {
        log.info("Loaded component (messaging)");
    }
}
