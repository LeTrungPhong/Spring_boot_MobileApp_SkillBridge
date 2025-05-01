package com._NguoiDev.SkillBridge.websocket;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {

    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        URI uri = request.getURI();
        System.out.println(request.getURI().getPath());
        String token = UriComponentsBuilder.fromUri(uri).build().getQueryParams().getFirst("token");
        System.out.println(token);
        if (token != null) {
            try {
                JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
                SignedJWT signedJWT = SignedJWT.parse(token);
                if (signedJWT.verify(verifier)) {
                    String username = signedJWT.getJWTClaimsSet().getSubject();
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                    attributes.put("user", authentication); // lưu thông tin user vào session
                    return true;
                }
            } catch (Exception e) {
                System.out.println("JWT không hợp lệ: " + e.getMessage());
            }
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
