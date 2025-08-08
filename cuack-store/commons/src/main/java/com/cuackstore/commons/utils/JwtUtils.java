package com.cuackstore.commons.utils;

import com.cuackstore.commons.dto.JwtStructure;

public interface JwtUtils {
    JwtStructure getJwtStructure(String token);
}
