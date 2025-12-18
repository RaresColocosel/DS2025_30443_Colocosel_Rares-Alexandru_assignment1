package com.ems.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
    this.jwtFilter = jwtFilter;
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    // -----------------------------------------------------------------
                    //  PUBLIC AUTH ENDPOINTS  (with AND without /api prefix)
                    // -----------------------------------------------------------------
                    .requestMatchers(
                            "/auth/login",
                            "/auth/register-client",
                            "/auth/register",
                            "/api/auth/login",
                            "/api/auth/register-client",
                            "/api/auth/register"
                    ).permitAll()

                    // -----------------------------------------------------------------
                    //  ADMIN-ONLY ENDPOINTS
                    // -----------------------------------------------------------------
                    .requestMatchers(
                            "/users/**",
                            "/devices/**",
                            "/assignments/**",
                            "/api/users/**",
                            "/api/devices/**",
                            "/api/assignments/**"
                    ).hasAuthority("ADMIN")

                    // -----------------------------------------------------------------
                    //  CLIENT-ONLY ENDPOINTS
                    // -----------------------------------------------------------------
                    .requestMatchers("/client/**", "/api/client/**").hasAuthority("CLIENT")

                    // “My profile / my devices” – any authenticated user
                    .requestMatchers("/me/**", "/api/me/**").permitAll()

                    // everything else: authenticated
                    .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
