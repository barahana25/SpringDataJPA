package kr.ac.kumoh.ce.s2025114794.mysql_db.config

import kr.ac.kumoh.ce.s2025114794.mysql_db.common.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.web.server.ServerHttpSecurity
import kr.ac.kumoh.ce.s2025114794.mysql_db.common.SHA256PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
class SecurityConfig(
    private val jwtFilter: JwtAuthenticationFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = SHA256PasswordEncoder()

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .csrf { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests {
                it.requestMatchers("/api/auth/**").permitAll()        // 로그인 등
                    .requestMatchers("/api/professor/**",
                        "/api/student/**").authenticated() // 토큰 필요
                    .anyRequest().permitAll()
            }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .addFilterBefore(jwtFilter,          // ★ 필터 등록
                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter::class.java)
            .build()
}
