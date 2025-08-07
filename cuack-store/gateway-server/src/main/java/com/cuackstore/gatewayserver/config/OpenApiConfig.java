package com.cuackstore.gatewayserver.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "OAuth2 API",
                version = "1.0"
        )
)
public class OpenApiConfig {
   @Bean
   public OpenAPI customOpenAPI() {
      return new OpenAPI()
              .components(new Components()
                      .addSecuritySchemes("auth0", new SecurityScheme()
                              .type(SecurityScheme.Type.OAUTH2)
                              .description("Autenticación con Auth0 para obtener el ID Token")
                              .flows(new OAuthFlows()
                                      .authorizationCode(new OAuthFlow()
                                              .authorizationUrl("https://dev-9pn-820c.us.auth0.com/authorize")
                                              .tokenUrl("https://dev-9pn-820c.us.auth0.com/oauth/token")
                                              .scopes(new Scopes()
                                                      .addString("openid", "Obtener ID Token")
                                                      .addString("profile", "Información del perfil del usuario")
                                                      .addString("email", "Email del usuario")
                                              )
                                      )
                              )
                      )
                      .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                              .type(SecurityScheme.Type.HTTP)
                              .scheme("bearer")
                              .bearerFormat("JWT")
                              .description("Proporcione el ID Token obtenido de Auth0")
                      )
              )
              .addSecurityItem(new SecurityRequirement().addList("auth0"))
              .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
   }
}

