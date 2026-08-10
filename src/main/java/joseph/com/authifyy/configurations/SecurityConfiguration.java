package joseph.com.authifyy.configurations;

import jakarta.servlet.http.HttpServletResponse;
import joseph.com.authifyy.services.Oauth2UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final JWTFilter jwtFilter;
    private final Oauth2UserServiceImpl oauth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final GeneralAuthenticationFailureHandler generalAuthenticationFailureHandler;

    public SecurityConfiguration(JWTFilter jwtFilter,
                                 Oauth2UserServiceImpl oauth2UserService,
                                 OAuth2SuccessHandler oAuth2SuccessHandler,
                                 GeneralAuthenticationFailureHandler generalAuthenticationFailureHandler) {
        this.jwtFilter = jwtFilter;
        this.oauth2UserService = oauth2UserService;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.generalAuthenticationFailureHandler = generalAuthenticationFailureHandler;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) {
        return httpSecurity.
                cors(cors ->
                        cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(a ->
                        a.requestMatchers("/auth/**" , "/error", "/oauth2/login","/favicon.ico").permitAll()
                                .anyRequest().authenticated())
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        // TODO: switch oauth2 from a session based to a cookie based auth request repo.
                )
                .oauth2Login(oauth2 -> {
                    oauth2.authorizationEndpoint(authorizationEndpointConfig ->
                            authorizationEndpointConfig.baseUri("/oauth2/login"));
                    oauth2.successHandler(oAuth2SuccessHandler);
                    oauth2.userInfoEndpoint((userInfo) -> userInfo
                            .userService(this.oauth2UserService));
                })
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(generalAuthenticationFailureHandler))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        // TODO: CREATE A CUSTOM COMPONENT FOR THE LOGOUT OR..
                        // switch to a custom service method.
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("Auth-Token")
                        .logoutSuccessHandler((request,
                                               response,
                                               authentication) -> {
                            org.springframework.http.ResponseCookie deleteCookie =
                                    org.springframework.http.ResponseCookie.from("Auth-Token", "")
                                            .path("/")
                                            .httpOnly(true)
                                            .secure(true)
                                            .sameSite("Strict") // This must match exactly!
                                            .maxAge(0)          // 0 seconds forces the browser to delete it immediately
                                            .build();

                            // 2. Add it directly to the response header
                            response.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, deleteCookie.toString());

                            // 3. Send your success message back to Axios
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\":\"Logged out successfully\"}");
                        }))
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(daoAuthenticationProvider);
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "https://teachassist-delta.vercel.app"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
