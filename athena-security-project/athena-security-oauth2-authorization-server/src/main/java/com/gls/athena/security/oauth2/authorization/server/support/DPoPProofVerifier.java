package com.gls.athena.security.oauth2.authorization.server.support;

import lombok.experimental.UtilityClass;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import org.springframework.util.StringUtils;

/**
 * DPoPProofVerifier 用于验证 OAuth2 授权请求中的 DPoP Proof（DPoP 证明）。
 * <p>
 * 该工具类提供方法从认证令牌中提取 DPoP 相关参数，并使用 JwtDecoder 验证 DPoP Proof 的有效性。
 * 如果验证失败，将抛出 OAuth2AuthenticationException 异常。
 * </p>
 *
 * @author george
 */
@UtilityClass
public class DPoPProofVerifier {
    /**
     * DPoP Proof 上下文，用于构建 DPoP Proof 的解码器。
     * 该上下文包含 DPoP Proof、HTTP 方法和目标 URI 等信息。
     */
    private final JwtDecoderFactory<DPoPProofContext> dPoPProofVerifierFactory = new DPoPProofJwtDecoderFactory();

    /**
     * 验证 OAuth2 授权请求中是否包含有效的 DPoP Proof。
     * <p>
     * 该方法会从认证令牌中提取 dpop_proof、dpop_method 和 dpop_target_uri 参数，
     * 并使用 JwtDecoderFactory 构造解码器来验证 DPoP Proof 的有效性。
     * 如果没有提供 dpop_proof 参数，则返回 null。
     * </p>
     *
     * @param authorizationGrantAuthentication OAuth2 授权请求的认证令牌，包含附加参数
     * @return 如果提供了有效的 DPoP Proof，则返回解析后的 Jwt 对象；否则返回 null
     * @throws OAuth2AuthenticationException 如果 DPoP Proof 验证失败或格式不正确
     */
    public Jwt verifyIfAvailable(OAuth2AuthorizationGrantAuthenticationToken authorizationGrantAuthentication) {
        // 提取 DPoP Proof 参数
        String dPoPProof = (String) authorizationGrantAuthentication.getAdditionalParameters().get("dpop_proof");
        if (!StringUtils.hasText(dPoPProof)) {
            return null;
        }

        String method = (String) authorizationGrantAuthentication.getAdditionalParameters().get("dpop_method");
        String targetUri = (String) authorizationGrantAuthentication.getAdditionalParameters().get("dpop_target_uri");

        Jwt dPoPProofJwt;
        try {
            // 构造 DPoP Proof 上下文并创建对应的 JwtDecoder
            // @formatter:off
            DPoPProofContext dPoPProofContext = DPoPProofContext.withDPoPProof(dPoPProof)
                    .method(method)
                    .targetUri(targetUri)
                    .build();
            // @formatter:on
            JwtDecoder dPoPProofVerifier = dPoPProofVerifierFactory.createDecoder(dPoPProofContext);
            dPoPProofJwt = dPoPProofVerifier.decode(dPoPProof);
        } catch (Exception ex) {
            // 如果验证过程中出现异常，则抛出 OAuth2 认证异常
            throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_DPOP_PROOF), ex);
        }

        return dPoPProofJwt;
    }
}

