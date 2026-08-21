package com.hemanth.slmsuite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isDealer = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DEALER"));

            if (isAdmin) {
                response.sendRedirect("/parts");
            } else if (isDealer) {
                response.sendRedirect("/orders");
            } else {
                response.sendRedirect("/claims");
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/parts/**").hasRole("ADMIN")
                        .requestMatchers("/warehouses/**").hasRole("ADMIN")
                        .requestMatchers("/stock/**").hasRole("ADMIN")
                        .requestMatchers("/claims/approve/**", "/claims/reject/**", "/claims/review/**", "/claims/pay/**").hasRole("ADMIN")
                        .requestMatchers("/claims/**").hasAnyRole("ADMIN", "CUSTOMER")
                        .requestMatchers("/orders/approve/**", "/orders/reject/**", "/orders/ship/**", "/orders/deliver/**").hasRole("ADMIN")
                        .requestMatchers("/orders/**").hasAnyRole("ADMIN", "DEALER")
                        .requestMatchers("/pricing/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler())
                        .permitAll()
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}