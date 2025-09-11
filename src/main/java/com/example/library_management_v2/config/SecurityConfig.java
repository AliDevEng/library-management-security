package com.example.library_management_v2.config;

import com.example.library_management_v2.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Tillåt alla att komma åt startsidan och publika resurser
                        .requestMatchers("/", "/home", "/public/**").permitAll()

                        // ADMIN område
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers("/authors/**").hasRole("ADMIN")

                        // USER område
                        .requestMatchers("/books/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/loans/**").hasAnyRole("USER", "ADMIN")

                        // Alla andra requests kräver autentisering
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults())

                // Logout-funktionalitet
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                )

        // Ta bort CSRF-disable för nu - vi vill ha CSRF-skydd
        // .csrf(csrf -> csrf.disable()); // KOMMENTERAD BORT
        ;

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        // Använd vår UserDetailsService från databasen
        authProvider.setUserDetailsService(userDetailsService);

        // Använd BCrypt för lösenordsjämförelse
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }
}
