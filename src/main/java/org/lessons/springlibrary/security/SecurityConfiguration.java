package org.lessons.springlibrary.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfiguration {

  /* per definire un AuthenticationProvider ho bisogno di:
    - uno UserDetailsService
    - un PasswordEncoder
   */
  // questo è lo UserDetailsService
  @Bean
  DatabaseUserDetailsService userDetailsService() {
    return new DatabaseUserDetailsService();
  }

  // questo è il PasswordEncoder (che deduce l'algoritmo di encoding da una stringa nella password
  // stessa)
  @Bean
  PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  DaoAuthenticationProvider authenticationProvider() {
    // creo l'authenticationProvider
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    // gli setto il PasswordEncoder
    provider.setPasswordEncoder(passwordEncoder());
    // gli setto lo UserDetailsService
    provider.setUserDetailsService(userDetailsService());
    return provider;
  }

  /*
   * /categories solo ADMIN
   * /books, /books/{id} ADMIN e USER
   * /books/edit/** ADMIN
   * /books/create ADMIN
    /borrowings/** ADMIN
   */


  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // definisco la catena di filtri
    http.authorizeHttpRequests()
        .requestMatchers("/categories").hasAuthority("ADMIN")
        .requestMatchers("/books/edit/**").hasAuthority("ADMIN")
        .requestMatchers("/books/create").hasAuthority("ADMIN")
        .requestMatchers("/books/**").hasAnyAuthority("ADMIN", "USER")
        .requestMatchers("/borrowings/**").hasAuthority("ADMIN")
        .requestMatchers(HttpMethod.POST, "/books/**").hasAuthority("ADMIN")
        .requestMatchers("/**").permitAll()
        .and().formLogin()
        .and().logout();
    // disabilitiamo csfr per poter invocare le api da Postman
    http.csrf().disable();
    return http.build();

  }
}
