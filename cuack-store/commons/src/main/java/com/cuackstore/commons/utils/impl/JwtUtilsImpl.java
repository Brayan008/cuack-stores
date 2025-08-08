package com.cuackstore.commons.utils.impl;

import com.cuackstore.commons.dto.JwtStructure;
import com.cuackstore.commons.exceptions.ServicesException;
import com.cuackstore.commons.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class JwtUtilsImpl implements JwtUtils {
    @Override
    public JwtStructure getJwtStructure(String token) {
        try {
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String[] chunks = token.split("\\.");
            String payload = new String(decoder.decode(chunks[1]));
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(payload, JwtStructure.class);
        } catch (Exception e) {
            throw new ServicesException("No se pudo parsear el JWT", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
