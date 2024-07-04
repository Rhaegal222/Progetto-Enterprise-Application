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

                        // ADDRESS METHOD
                        .requestMatchers(HttpMethod.POST, "/api/v1/addresses/addAddress").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/addresses/setDefaultAddress/{id}", "/api/v1/addresses/updateAddress/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/addresses/deleteAddress/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/addresses/getAddress/{id}", "/api/v1/addresses/getAllAddresses",
                                "/api/v1/addresses/getAllLoggedUserAddresses").authenticated()

                        // BRAND
                        .requestMatchers(HttpMethod.POST, "/api/v1/brand/addBrand").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/brand/deleteBrand").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/brand/allBrands", "api/v1/brand/getBrandById", "api/v1/brand/getBrandByName").authenticated()

                        // CATEGORY
                        .requestMatchers(HttpMethod.POST, "/api/v1/category/addCategory").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/category/updateCategory").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/category/deleteCategory").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/category/getCategoryById", "/api/v1/category/getAllCategories").authenticated()

                        // CART
                        .requestMatchers(HttpMethod.POST, "/api/v1/cart/addProduct").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/cart/updateProduct/{cartItemId}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/cart/removeProduct/{cartItemId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/cart/getCartByUserId/{userId}").authenticated()

                        //ORDER
                        .requestMatchers(HttpMethod.POST, "/api/v1/orders/addOrder").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/orders/updateOrder/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/deleteOrder/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/getOrder/{id}", "/api/v1/orders/getAllOrders").authenticated()

                        // PAYMENT METHOD
                        .requestMatchers(HttpMethod.POST, "/api/v1/paymentMethods/addPaymentMethod").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/paymentMethods/updatePaymentMethod/{id}", "/setDefaultPaymentMethod/{id}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/paymentMethods/deletePaymentMethod/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/paymentMethods/getPaymentMethod/{id}", "/api/v1/paymentMethods/getAllPaymentMethods",
                                "api/v1/paymentMethods/getAllLoggedUserPaymentMethods").authenticated()

                        // PRODUCT
                        .requestMatchers(HttpMethod.POST, "/api/v1/products/addProduct").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/updateProduct/").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/deleteProduct/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/getProductById/","/api/v1/products/getProductsByCategory/" ,
                                "/api/v1/products/getProductsByCategory/", "/api/v1/products/getAllProducts",
                                "/api/v1/products/getProductsByBrand/","/api/v1/products/getProductsByPriceRange/", "/api/v1/products/getSalesProducts").authenticated()

                        // PRODUCT IMAGE
                        .requestMatchers(HttpMethod.POST, "/api/v1/productPicture/uploadImage").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/productPicture/getImage/{type}/{folder_name}/{file_name:.*}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/productPicture/deleteImage/{id}").authenticated()

                        // USER
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/logout", "/api/v1/users/changePassword", "/api/v1/users/changeRole/{userId}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/deleteUser/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/getUserById", "/api/v1/users/getUserByUsername", "/api/v1/users/getUserByEmail",
                                "/api/v1/users/me", "/api/v1/users/refreshToken", "/api/v1/users/rejectToken", "/api/v1/users/getAllUsers",
                                "/api/v1/users/{id}", "/api/v1/users/find-by-username", "/api/v1/users/resetPassword",
                                "/api/v1/users/retrieveUserProfile", "/api/v1/users/findUserById/", "/api/v1/users/findByUsername",
                                "/api/v1/users/updateUser/", "/api/v1/users/getNewPassword").authenticated()

                        // USER IMAGE
                        .requestMatchers(HttpMethod.POST, "/api/v1/profilePicture/uploadImage").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/profilePicture/getImage/{type}/{folder_name}/{file_name:.*}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/profilePicture/deleteImage/{id}").authenticated()

                        // WISHLIST
                        .requestMatchers(HttpMethod.POST, "/api/v1/wishlist/addWishlist", "/{wishlistId}/addProductsToWishlist/{productId}").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/wishlist/deleteWishlist/{id}", "/{wishlistId}/removeProductFromWishlist/{productId}",
                                "/{wishlistId}/removeProductsFromWishlist/{productId}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/wishlist/getWishlistById/{id}", "/api/v1/wishlist/getAllLoggedUserWishlists").authenticated()

                        // Richieste dove non è richiesta l'autenticazione
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/register", "/api/v1/users/login", "/api/v1/users/googleAuthentication").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/activate").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        .anyRequest().permitAll())

                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilter(new CustomAuthenticationFilter(authenticationManager, tokenStore))
                .addFilterBefore(requestFilter, UsernamePasswordAuthenticationFilter.class);
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
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
