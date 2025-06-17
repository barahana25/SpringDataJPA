package kr.ac.kumoh.ce.s2025114794.mysql_db.common

import kr.ac.kumoh.ce.s2025114794.mysql_db.CustomPrincipal

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val header = request.getHeader("Authorization")
        val token = header?.takeIf { it.startsWith("Bearer ", true) }?.substring(7)

        if (token != null && SecurityContextHolder.getContext().authentication == null) {
            val claims = runCatching { jwtProvider.extractAllClaims(token) }.getOrNull()
            if (claims != null && jwtProvider.validate(token)) {
                val userId = claims.subject
                val role = claims["role", String::class.java] ?: "guest"

                val principal = CustomPrincipal(userId, role)
                val auth = UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    listOf(SimpleGrantedAuthority("ROLE_${role.uppercase()}"))
                ).apply {
                    details = WebAuthenticationDetailsSource().buildDetails(request)
                }

                SecurityContextHolder.getContext().authentication = auth
            }
        }

        filterChain.doFilter(request, response)
    }
}
