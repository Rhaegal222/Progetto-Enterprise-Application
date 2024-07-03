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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
                        // BRAND
                        .requestMatchers(HttpMethod.POST, "/api/v1/brand/addBrand").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/brand/deleteBrand").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/brand/allBrands", "api/v1/brand/getBrandById", "api/v1/brand/getBrandByName").authenticated()

                        // PRODUCT
                        .requestMatchers(HttpMethod.POST, "/api/v1/products/addProduct", "/api/v1/products/uploadImage").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/updateProduct").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/deleteProduct").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/getProductById/","/api/v1/products/getProductsByCategory/" ,
                                "/api/v1/products/getProductsByCategory/", "/api/v1/products/getAllProducts",
                                "/api/v1/products/getProductsByBrand/","/api/v1/products/getProductsByPriceRange/", "/api/v1/products/getSalesProducts").authenticated()

                        // CATEGORY
                        .requestMatchers(HttpMethod.POST, "/api/v1/category/addCategory").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/category/updateCategory").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/category/deleteCategory").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/category/getCategoryById", "/api/v1/category/getAllCategories").authenticated()

                        // Wishlist
                        .requestMatchers(HttpMethod.POST, "/api/v1/wishList/createWishlist").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/wishList/getAllWishlists", "/api/v1/wishList/getWishlistById").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/wishList/deleteWishlist").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/wishList/addProductToWishlist").authenticated()

                        // PAYMENT METHOD
                        .requestMatchers(HttpMethod.POST, "/api/v1/payment-methods/addPaymentMethod").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/payment-methods/updatePaymentMethod/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/payment-methods/deletePaymentMethod/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/payment-methods/getPaymentMethod/{id}", "/api/v1/payment-methods/getAllPaymentMethods").authenticated()

                        // ADDRESS METHOD
                        .requestMatchers(HttpMethod.POST, "/api/v1/shipping-addresses/addAddress").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/shipping-addresses/setDefaultAddress/{id}", "/api/v1/shipping-addresses/updateAddress/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/shipping-addresses/deleteAddress/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/shipping-addresses/getAddress/{id}", "/api/v1/shipping-addresses/getAllAddresses").authenticated()

                        // CART
                        .requestMatchers(HttpMethod.POST, "/api/v1/cart/addProduct").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/cart/updateProduct/{cartItemId}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/cart/removeProduct/{cartItemId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/cart/getCartByUserId/{userId}").authenticated()

                        // USER
                        .requestMatchers(HttpMethod.POST, "/api/v1/user/logout", "api/v1/users/changePassword", "/api/v1/users/changeRole/{userId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/user/getUserById", "/api/v1/user/getUserByUsername", "/api/v1/user/getUserByEmail",
                                "/api/v1/users/me", "/api/v1/users/refreshToken", "/api/v1/users/rejectToken", "/api/v1/users/getAllUsers").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/deleteUser/{id}").authenticated()

                        //ORDER
                        .requestMatchers(HttpMethod.POST, "/api/v1/orders/addOrder").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/orders/updateOrder/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/deleteOrder/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/getOrder/{id}", "/api/v1/orders/getAllOrders").authenticated()

                        // Richieste dove non è richiesta l'autenticazione
                        .requestMatchers(HttpMethod.POST, "/api/v1/user/register", "/api/v1/users/authenticate").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/{id}", "/api/v1/users/find-by-username").permitAll()

                        .anyRequest().permitAll())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(new CustomAuthenticationFilter(authenticationManager, tokenStore))
                .addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);

        // Configurazione CORS
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        // Configurazione CSRF
        http.csrf(AbstractHttpConfigurer::disable);
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
        source.registerCorsConfiguration("/", configuration);
        return source;
    }
}