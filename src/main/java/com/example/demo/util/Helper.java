package com.example.demo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.modelmapper.ModelMapper;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class Helper {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ModelMapper modelMapper = new ModelMapper();

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public ObjectWriter getObjectWriter() {
        return getObjectMapper().writer().withDefaultPrettyPrinter();
    }

    public ModelMapper getModelMapper() {
        return modelMapper;
    }
}