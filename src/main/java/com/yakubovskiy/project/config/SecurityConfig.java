package com.yakubovskiy.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Qualifier("userDetailsServiceImpl")
    @Autowired
    private UserDetailsService detailsService;

    private static final String PREFIX = "/demo/v1";


    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(detailsService);
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return detailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, PREFIX + "/users/").anonymous()
                .antMatchers(HttpMethod.GET, PREFIX + "/categories/**",
                        PREFIX + "/products/**",
                        PREFIX + "/reviews/**").permitAll()
                .antMatchers(PREFIX + "/carts/",
                        PREFIX + "/reviews/products/**").hasAuthority("ADMIN")
                .antMatchers(PREFIX + "/users/passwords/**",
                        PREFIX + "/users/id/**",
                        PREFIX + "/orders/id/**",
                        PREFIX + "/reviews/**").authenticated()
                .antMatchers(HttpMethod.POST, PREFIX + "/reviews/**").authenticated()
                .antMatchers(PREFIX + "/users/balance/**",
                        PREFIX + "/cart-items/**",
                        PREFIX + "/carts/**").hasAuthority("USER")
                .antMatchers(HttpMethod.POST, PREFIX + "/orders/").hasAuthority("USER")
                .antMatchers(PREFIX + "/categories/**",
                        PREFIX + "/products/**",
                        PREFIX + "/users/**",
                        PREFIX + "/orders/**").hasAuthority("ADMIN")
                .and()
                .httpBasic()
                .and()
                .logout()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .disable();
    }
}

