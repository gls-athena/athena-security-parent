package com.gls.athena.security.oauth2.client;

import com.gls.athena.security.oauth2.client.config.Oauth2ClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author george
 */
@Configuration
@ComponentScan
@EnableConfigurationProperties(Oauth2ClientProperties.class)
public class Oauth2ClientAutoConfig {
}
