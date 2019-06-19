package br.com.jbd.user.info.config;

import br.com.jbd.user.info.security.jwt.JWTAuthenticationFilter;
import br.com.jbd.user.info.security.jwt.JWTLoginFilter;
import br.com.jbd.user.info.security.jwt.JWTTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@Order(100)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] swaggerURIs = {"/v2/api-docs", "/swagger-resources/**", "/swagger-ui.html**", "/webjars/**", "favicon.ico"};

        http
                .authorizeRequests().antMatchers(HttpMethod.GET, swaggerURIs).permitAll()
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.jdbcAuthentication()
                .passwordEncoder(passwordEncoder())
                .usersByUsernameQuery("SELECT LOGIN, PASSWORD, ENABLED FROM JBD_USER WHERE LOGIN=?")
                .authoritiesByUsernameQuery("SELECT LOGIN, ROLE FROM JBD_USER_ROLE WHERE LOGIN=?")
                .dataSource(dataSource);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTTokenProvider jwtTokenProvider() {
        return new JWTTokenProvider();
    }

    @Configuration
    @Order(1)
    public static class ApiWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        private JWTTokenProvider jwtTokenProvider;

        @Autowired
        private AuthenticationManager authenticationManager;

        protected void configure(HttpSecurity http) throws Exception {
            String apiAuthenticationURI = "/api/authenticate";
            http
                    .csrf().disable()
                    .authorizeRequests().antMatchers(apiAuthenticationURI).permitAll()
                    .and()
                    .antMatcher("/api/**").authorizeRequests().anyRequest().hasAnyAuthority("ADMIN")
                    .and()
                    // filtra requisições de login
                    .addFilterBefore(new JWTLoginFilter(apiAuthenticationURI, authenticationManager, jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                    // filtra outras requisições para verificar a presença do JWT no header
                    .addFilterBefore(new JWTAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }
    }

    @Configuration
    @Order(2)
    public static class ActuatorConfigurationAdapter extends WebSecurityConfigurerAdapter {
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .authorizeRequests().antMatchers("/actuator/health", "/actuator/info").permitAll()
                    .and()
                    .antMatcher("/actuator/**").authorizeRequests().anyRequest().hasRole("ACTUATOR")
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .httpBasic();
        }
    }

}
