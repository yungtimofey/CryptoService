package com.example.cryptoservice.service.impl;

import com.example.cryptoservice.db.models.AccountEntity;
import com.example.cryptoservice.db.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AccountEntity> accountOptional = accountRepository.findAccountByName(username);

        AccountEntity account = accountOptional.orElseThrow(() -> new UsernameNotFoundException("No user with such email!"));
        List<SimpleGrantedAuthority> authorityList = Collections.singletonList(new SimpleGrantedAuthority("user"));
        return new User(
                account.getId(),
                account.getPassword(),
                authorityList
        );
    }
}
