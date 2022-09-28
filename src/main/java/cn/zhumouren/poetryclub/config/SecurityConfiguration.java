package cn.zhumouren.poetryclub.config;


import cn.zhumouren.poetryclub.config.security.filter.JwtTokenFilter;
import cn.zhumouren.poetryclub.config.security.handle.AuthenticationSuccessHandlerImpl;
import cn.zhumouren.poetryclub.constants.RoleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * @author mourenZhu
 * @version 1.0
 * @description todo
 * @date 2022/9/4 13:46
 **/
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {

    private final JwtTokenFilter jwtTokenFilter;
    private final AuthenticationSuccessHandlerImpl authenticationSuccessHandler;
    private final UserDetailsService userDetailsService;

    public SecurityConfiguration(
            JwtTokenFilter jwtTokenFilter, AuthenticationSuccessHandlerImpl authenticationSuccessHandler, UserDetailsService userDetailsService) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Enable CORS and disable CSRF
        http = http.cors().and().csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .authorizeHttpRequests((authz) ->

                        authz.antMatchers("/api/auth/login", "/api/register").permitAll()
                                .antMatchers("/api/user/**")
                                .hasAnyRole(RoleType.USER.getStr(), RoleType.ADMIN.getStr())
                                .antMatchers("/api/admin/**").hasRole(RoleType.ADMIN.getStr())
                                .anyRequest().authenticated())
                .httpBasic(withDefaults())
                .userDetailsService(userDetailsService);

        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                "/files/**", "/oauth2/**");
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

//    @Bean
//    public UserDetailsManager userDetailsManager() {
//        log.info("UserDetailsManager = {}", userDetailsManager);
//        return userDetailsManager;
//    }


    // Used by spring security if CORS is enabled.
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
