package com.example.cryptoservice.component;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class EntityToDTOMapper {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
