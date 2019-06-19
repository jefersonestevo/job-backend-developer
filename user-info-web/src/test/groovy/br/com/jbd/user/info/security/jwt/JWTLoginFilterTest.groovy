package br.com.jbd.user.info.security.jwt

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import spock.lang.Specification

import javax.servlet.FilterChain
import javax.servlet.ServletOutputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTLoginFilterTest extends Specification {

    JWTLoginFilter jwtLoginFilter

    AuthenticationManager authManager
    JWTTokenProvider jwtTokenProvider

    HttpServletRequest request
    HttpServletResponse response
    FilterChain chain

    def setup() {
        authManager = Mock(AuthenticationManager)
        jwtTokenProvider = Mock(JWTTokenProvider)

        request = Mock(HttpServletRequest)
        response = Mock(HttpServletResponse)
        chain = Mock(FilterChain)

        jwtLoginFilter = new JWTLoginFilter("/login", authManager, jwtTokenProvider)
    }

    def "attemptAuthentication must authenticate with the token provider user credentials"() {
        given:
        UsernamePasswordAuthenticationToken authenticationToken = Mock(UsernamePasswordAuthenticationToken)
        1 * jwtTokenProvider.readToken(request.getInputStream()) >> authenticationToken

        when:
        Authentication authentication = jwtLoginFilter.attemptAuthentication(request, response)

        then:
        authentication

        and:
        1 * authManager.authenticate(authenticationToken) >> Mock(Authentication)
    }

    def "successfulAuthentication must set the token on the header and write it to the response output Stream"() {
        given:
        ServletOutputStream responseOutputStream = Mock(ServletOutputStream)
        response.getOutputStream() >> responseOutputStream

        Authentication auth = Mock(Authentication)
        1 * jwtTokenProvider.generateToken(_, _) >> "TOKEN"

        when:
        jwtLoginFilter.successfulAuthentication(request, response, chain, auth)

        then:
        response.addHeader("Authorization", "Bearer TOKEN")

        and:
        responseOutputStream.print("TOKEN")
    }
}
