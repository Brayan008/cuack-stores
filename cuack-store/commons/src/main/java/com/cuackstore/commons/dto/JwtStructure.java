package com.cuackstore.commons.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtStructure {

    private String role;
    @JsonProperty("given_name")
    private String giveName;
    @JsonProperty("family_name")
    private String familyName;
    private String name;
    private String picture;
    private String email;
    private String nickname;

}
