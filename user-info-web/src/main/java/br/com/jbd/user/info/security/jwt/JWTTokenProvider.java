package br.com.jbd.user.info.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class JWTTokenProvider {

    public static final String JWT_TOKEN_PREFIX = "Bearer";
    public static final String JWT_TOKEN_HEADER = "Authorization";
    public static final String JWT_ROLES_CLAIM = "roles";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.time}")
    private long expirationTime;

    @Autowired
    private ObjectMapper objectMapper;

    public UsernamePasswordAuthenticationToken readToken(InputStream inputStream) {
        try {
            UserCredentials userCredentials = objectMapper.readValue(inputStream, UserCredentials.class);
            return new UsernamePasswordAuthenticationToken(userCredentials.getUsername(), userCredentials.getPassword(), Collections.emptyList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(JWT_ROLES_CLAIM, authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token.replace(JWT_TOKEN_PREFIX, ""))
                .getBody();

        List<String> roles = (List<String>) claims.get(JWT_ROLES_CLAIM);

        String user = claims.getSubject();
        List<GrantedAuthority> authorities = roles != null ? roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()) : null;
        return user != null ? new UsernamePasswordAuthenticationToken(user, null, authorities) : null;
    }

    public static final class UserCredentials {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

}
