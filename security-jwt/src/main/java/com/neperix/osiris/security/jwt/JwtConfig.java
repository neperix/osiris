package com.neperix.osiris.security.jwt;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@ComponentScan
@EnableWebMvc
@EnableConfigurationProperties(JwtProperties.class)
class JwtConfig {

    @Bean
    JwtFactory jwtFactory(JwtProperties props) {
        return new DefaultJwtFactory(props);
    }
}
