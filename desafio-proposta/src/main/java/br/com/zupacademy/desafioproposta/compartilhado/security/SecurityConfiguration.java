package br.com.zupacademy.desafioproposta.compartilhado.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@Profile("dev")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorizeRequests ->
                authorizeRequests
                        .antMatchers(GET, "/propostas/**").hasAuthority("SCOPE_propostas:read")
                        .antMatchers(POST, "/propostas/**").hasAuthority("SCOPE_propostas:write")
                        .antMatchers(POST, "/cartoes/**").hasAuthority("SCOPE_cartoes:write")
                        .antMatchers(GET, "/actuator/**").hasAuthority("SCOPE_actuator:read")
                        .anyRequest().authenticated()
        ).oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    }
}
