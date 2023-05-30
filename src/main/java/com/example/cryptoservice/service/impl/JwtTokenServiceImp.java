package com.example.cryptoservice.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.cryptoservice.service.JwtTokenService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JwtTokenServiceImp implements JwtTokenService {
    private static final Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
    private static final JWTVerifier jwtVerifier = JWT.require(algorithm).build();

    @Override
    public String createToken(String accountId, String username, List<String> grantedAuthorities) {
        return JWT.create()
                .withSubject(username)
                .withClaim("id", accountId)
                .withClaim("authorities", grantedAuthorities)
                .sign(algorithm);
    }

    @Override
    public String getUsername(String token) {
        return jwtVerifier.verify(token).getSubject();
    }

    @Override
    public List<String> getAuthorities(String token) {
        return jwtVerifier.verify(token).getClaim("authorities").asList(String.class);
    }

}
