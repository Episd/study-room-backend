package com.nbucs.studyroombackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 启用 CORS
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:5173"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))
                // 关闭 CSRF
                .csrf(csrf -> csrf.disable())
                // 授权规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 放行预检请求
                        .requestMatchers("/api/auth/**").permitAll()               // 登录注册接口放行
                        .requestMatchers("/api/reservation/**").permitAll()
                        .requestMatchers("/api/studyRoomManage/**").permitAll()
                        .requestMatchers("/api/seminarRoomManage/**").permitAll()
                        .requestMatchers("/api/seatManage/**").permitAll()
                        .requestMatchers("/api/attendance/**").permitAll()
                        .requestMatchers("/api/violation/**").permitAll()
                        .requestMatchers("/api/wait/**").permitAll()
                        .requestMatchers("/api/notification/**").permitAll()
                        .requestMatchers("/api/seminar-room/**").permitAll()
                        .requestMatchers("/api/feedback/**").permitAll()
                        .requestMatchers("/api/student/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}

