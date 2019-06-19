package br.com.jbd.user.info.security.jwt

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilterTest extends Specification {

    JWTAuthenticationFilter jwtAuthenticationFilter

    JWTTokenProvider jwtTokenProvider

    HttpServletRequest request
    HttpServletResponse response
    FilterChain chain

    SecurityContext securityContext

    def setup() {
        jwtTokenProvider = Mock(JWTTokenProvider)
        request = Mock(HttpServletRequest)
        response = Mock(HttpServletResponse)
        chain = Mock(FilterChain)

        securityContext = Mock(SecurityContext)
        SecurityContextHolder.setContext(securityContext)

        jwtAuthenticationFilter = new JWTAuthenticationFilter()
        jwtAuthenticationFilter.jwtTokenProvider = jwtTokenProvider
    }

    def "filter with valid token on the authorization header"() {
        given: "A valid Authorization header"
        1 * request.getHeader("Authorization") >> "Bearer TOKEN"

        and: "The jwt token provider return a valid authentication"
        Authentication authentication = Mock(Authentication)
        1 * jwtTokenProvider.getAuthentication("Bearer TOKEN") >> authentication

        when: "We call the filter"
        jwtAuthenticationFilter.doFilter(request, response, chain)

        then: "It must never set the response status code"
        0 * response.setStatus(_)

        and: "We must call the filter chain"
        1 * chain.doFilter(request, response)

        and: "Must set the authentication on the security context"
        1 * securityContext.setAuthentication(authentication)
    }

    @Unroll
    def "filter must return unauthorized when the authorization header #description"() {
        given:
        1 * request.getHeader("Authorization") >> value

        when: "We call the filter"
        jwtAuthenticationFilter.doFilter(request, response, chain)

        then: "It must return status code 401"
        response.setStatus(401)

        and: "Never call the filter chain"
        0 * chain.doFilter(_, _)

        and: "Must not set the authentication on the security context"
        0 * securityContext.setAuthentication(_)

        where:
        description                       | value
        "is null"                         | null
        "does not have the bearer syntax" | "Basic XXXXXX"
    }


}
