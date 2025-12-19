package com.ems.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                    // Public Auth
                    .requestMatchers("/auth/**", "/api/auth/**").permitAll()

                    // ADMIN Routes (Users, Devices, Assignments, Monitoring)
                    .requestMatchers(
                            "/users/**", "/api/users/**",
                            "/devices/**", "/api/devices/**",
                            "/assignments/**", "/api/assignments/**"
                    ).hasAuthority("ADMIN")

                    // Client Routes
                    .requestMatchers("/client/**", "/api/client/**").hasAuthority("CLIENT")

                    // Shared Routes
                    .requestMatchers(
                            "/me/**", "/api/me/**",
                            "/monitoring/**", "/api/monitoring/**" // <--- Accessible to everyone logged in
                    ).authenticated()

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