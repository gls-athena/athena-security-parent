package com.gls.athena.security.oauth2.authorization.server.redis.converter;

import com.gls.athena.security.oauth2.authorization.server.redis.domain.Oauth2AuthorizationConsent;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;

/**
 * OAuth2授权同意信息转换器接口
 * 用于在Spring Security OAuth2授权同意对象和自定义的授权同意实体之间进行转换
 *
 * @author george
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface Oauth2AuthorizationConsentConverter {

    /**
     * 将Spring Security OAuth2授权同意对象转换为自定义的授权同意实体
     *
     * @param authorizationConsent Spring Security OAuth2授权同意对象
     * @return 转换后的自定义授权同意实体
     */
    Oauth2AuthorizationConsent convert(OAuth2AuthorizationConsent authorizationConsent);

    /**
     * 将自定义的授权同意实体转换回Spring Security OAuth2授权同意对象
     *
     * @param consent 自定义的授权同意实体
     * @return 转换后的Spring Security OAuth2授权同意对象
     */
    default OAuth2AuthorizationConsent reverse(Oauth2AuthorizationConsent consent) {
        if (consent == null) {
            return null;
        }
        return OAuth2AuthorizationConsent.withId(consent.getPrincipalName(), consent.getRegisteredClientId())
                .authorities(authority -> authority.addAll(consent.getAuthorities()))
                .build();
    }
}
