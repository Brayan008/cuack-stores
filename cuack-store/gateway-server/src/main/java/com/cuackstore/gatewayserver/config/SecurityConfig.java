package com.cuackstore.gatewayserver.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

   @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
   private String jwkUri;

   @Bean
   public CorsConfigurationSource corsWebFilter() {
      CorsConfiguration corsConfig = new CorsConfiguration();
      corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173"));
      corsConfig.setMaxAge(3600L);
      corsConfig.addAllowedMethod("*");
      corsConfig.addAllowedHeader("*");
      corsConfig.setAllowCredentials(true);

      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", corsConfig);

      return source;
   }


   @Bean
   public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
      return http
              .cors(c -> c.configurationSource(corsWebFilter()))
              .authorizeExchange(exchange -> exchange
                      .pathMatchers("/swagger",
                              "/v3/api-docs/**",
                              "/api/auth/v3/api-docs",
                              "/api/inventory/v3/api-docs",
                              "/api/orders/v3/api-docs",
                              "/swagger-ui/**",
                              "/swagger-ui.html",
                              "/webjars/**",
                              "/doc/**").permitAll()
                      .pathMatchers("/test-admin").hasAuthority("ROLE_ADMIN")
                      .anyExchange().authenticated()
              )
              .oauth2ResourceServer(oauth2 -> oauth2
                      .jwt(jwt -> jwt
                              .jwkSetUri(jwkUri) // Configurar el URI de las claves públicas
                              .jwtAuthenticationConverter(grantedAuthoritiesExtractor()) // Mapear roles
                      )
              )
              .exceptionHandling(exceptionHandling -> exceptionHandling
                      .accessDeniedHandler(accessDeniedHandler())
              )
              .csrf(ServerHttpSecurity.CsrfSpec::disable)
              .build();
   }

   @Bean
   public ServerAccessDeniedHandler accessDeniedHandler() {
      return (exchange, denied) -> {
         exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
         return exchange.getResponse().setComplete();
      };
   }

   private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
      return jwt -> {
         Collection<GrantedAuthority> authorities = new ArrayList<>();
         String role = jwt.getClaim("role");
         if (role != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role)); // Agregar como autoridad
         }
         return Mono.just(new JwtAuthenticationToken(jwt, authorities));
      };
   }
}
