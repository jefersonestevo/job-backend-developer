package br.com.jbd.user.info.security.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

    private JWTTokenProvider jwtTokenProvider;

    public JWTLoginFilter(String url, AuthenticationManager authManager, JWTTokenProvider jwtTokenProvider) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authManager);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        UsernamePasswordAuthenticationToken authenticationToken = jwtTokenProvider.readToken(request.getInputStream());
        return getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain, Authentication auth) throws IOException, ServletException {
        String jwtToken = jwtTokenProvider.generateToken(auth.getName(), auth.getAuthorities());

        String token = JWTTokenProvider.JWT_TOKEN_PREFIX + " " + jwtToken;
        res.addHeader(JWTTokenProvider.JWT_TOKEN_HEADER, token);

        try {
            res.getOutputStream().print(token);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
