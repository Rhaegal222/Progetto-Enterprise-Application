package it.unical.inf.ea.backend.config.security;

import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static io.swagger.v3.oas.annotations.enums.SecuritySchemeIn.HEADER;
import static io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@SecurityScheme(name = AppSecurityConfig.SECURITY_CONFIG_NAME, in = HEADER, type = HTTP, scheme = "bearer", bearerFormat = "JWT")
public class AppSecurityConfig {

    public static final String SECURITY_CONFIG_NAME = "App_Bearer_token";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    private final RequestFilter requestFilter;
    private final TokenStore tokenStore;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/admin/**", "/api/v1/reports/close/**").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/reports").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers("/api/v1/superAdmin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**", "/api/v1/deliveries/address/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/{id}", "/api/v1/users/find-by-username").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/{id}", "/api/v1/products/filter", "/api/v1/images/**").permitAll()
                        .requestMatchers("/api/v1/demo", "/api/v1/users/register", "/api/v1/users/authenticate", "/api/v1/users/login-with-google", "api/v1/users/google-auth", "/api/v1/users/login-with-keycloak", "api/v1/users/keycloak-auth",
                                "/api/v1/users/refreshToken", "/api/v1/users/google_auth", "swagger-ui/**", "/v3/api-docs/**", "/api/v1/products/categories", "/api/v1/products/sizes",
                                "user_photos/**", "images/**", "api/v1/users/search-by-username").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(new CustomAuthenticationFilter(authenticationManager, tokenStore))
                .addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);

        // Configurazione CORS
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        // Configurazione CSRF
        http.csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
        configuration.setAllowedHeaders(List.of("X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization", "refresh-token"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
