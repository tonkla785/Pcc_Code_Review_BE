package pccth.code.review.Backend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final JwtAuthFilter jwtAuthFilter;
        private final WebhookAuthFilter webhookAuthFilter;

        public SecurityConfig(JwtAuthFilter jwtAuthFilter, WebhookAuthFilter webhookAuthFilter) {
                this.jwtAuthFilter = jwtAuthFilter;
                this.webhookAuthFilter = webhookAuthFilter;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf
                                                .csrfTokenRepository(
                                                                CookieCsrfTokenRepository.withHttpOnlyFalse())
                                                .ignoringRequestMatchers(
                                                                "/user/login",
                                                                "/user/register",
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui.html",
                                                                "/user/refresh",
                                                                "/webhooks/**"))
                                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/").permitAll()
                                                .requestMatchers(
                                                                "/swagger-ui/**",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui.html")
                                                .permitAll()
                                                .requestMatchers(
                                                                "/user/login",
                                                                "/user/register",
                                                                "/user/refresh",
                                                                "/webhooks/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(webhookAuthFilter, JwtAuthFilter.class);

                return http.build();
        }
}
