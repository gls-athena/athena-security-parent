package com.gls.athena.security.oauth2.authorization.server;

import com.gls.athena.security.oauth2.authorization.server.config.AuthorizationServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author george
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(AuthorizationServerProperties.class)
public class AuthorizationServerAutoConfig {
}
