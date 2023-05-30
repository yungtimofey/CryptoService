package com.example.cryptoservice.service;

import java.util.List;

public interface JwtTokenService {
    String createToken(String accountId, String username, List<String> grantedAuthorities);
    String getUsername(String token);
    List<String> getAuthorities(String token);
}