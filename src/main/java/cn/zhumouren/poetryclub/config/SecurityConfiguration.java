package cn.zhumouren.poetryclub.config;


import cn.zhumouren.poetryclub.config.security.filter.JwtTokenFilter;
import cn.zhumouren.poetryclub.config.security.handle.CustomizeAuthenticationSuccessHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
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
    private final CustomizeAuthenticationSuccessHandler authenticationSuccessHandler;
    private final UserDetailsManager userDetailsManager;

    public SecurityConfiguration(
            JwtTokenFilter jwtTokenFilter, CustomizeAuthenticationSuccessHandler authenticationSuccessHandler,
            UserDetailsManager userDetailsManager) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.userDetailsManager = userDetailsManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Enable CORS and disable CSRF
        http = http.cors().and().csrf().disable();

        http
                .authorizeHttpRequests((authz) ->
                        authz.anyRequest().authenticated())
                .httpBasic(withDefaults())
                .formLogin().permitAll()
                .loginProcessingUrl("/api/login")
                .successHandler(authenticationSuccessHandler)
                .and()
                .userDetailsService(userDetailsManager);

//        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                "/websocket/**", "/oauth2/**");
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
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public UserDetailsManager users(DataSource dataSource) {
//        return new JdbcUserDetailsManager(dataSource);
//    }
}
