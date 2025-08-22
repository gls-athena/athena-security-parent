package com.gls.athena.security.oauth2.authorization.server.config;

import com.gls.athena.security.oauth2.authorization.server.customizer.JwtEncodingContextCustomizer;
import com.gls.athena.security.oauth2.authorization.server.customizer.TokenClaimsContextCustomizer;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.token.*;

import java.util.Optional;

/**
 * 令牌配置类，用于配置OAuth2令牌生成器和JWT编码器等相关组件。
 *
 * @author george
 */
@Configuration
public class TokenConfig {
    /**
     * JWT自定义器，用于对JWT编码上下文进行自定义处理。
     */
    @Resource
    private Optional<JwtEncodingContextCustomizer> jwtEncodingContextCustomizer;
    /**
     * 访问令牌自定义器，用于对访问令牌的声明内容进行自定义处理。
     */
    @Resource
    private Optional<TokenClaimsContextCustomizer> tokenClaimsContextCustomizer;

    /**
     * 创建OAuth2令牌生成器，用于生成JWT、访问令牌和刷新令牌。
     *
     * @param jwtEncoder JWT编码器，用于JWT的编码操作
     * @return OAuth2令牌生成器，支持JWT、访问令牌和刷新令牌的生成
     */
    @Bean
    @ConditionalOnMissingBean
    public OAuth2TokenGenerator<? extends OAuth2Token> oauth2TokenGenerator(JwtEncoder jwtEncoder) {
        // JWT生成器
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder);
        jwtEncodingContextCustomizer.ifPresent(jwtGenerator::setJwtCustomizer);
        // 访问令牌生成器
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        tokenClaimsContextCustomizer.ifPresent(accessTokenGenerator::setAccessTokenCustomizer);
        // 刷新令牌生成器
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        // 委托OAuth2令牌生成器
        return new DelegatingOAuth2TokenGenerator(jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
    }

    /**
     * 创建JWT编码器，用于对JWT进行编码操作。
     *
     * @param jwkSource JWK源，提供用于JWT签名的密钥
     * @return JWT编码器，使用Nimbus库实现
     */
    @Bean
    @ConditionalOnMissingBean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }
}
