package jp.co.axa.api.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration {

    private final PasswordEncoder encoder =
            PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod.GET,"/api/v1/employees/*").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.GET,"/api/v1/employees").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.POST,"/api/v1/employees").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PUT,"/api/v1/employees").access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE,"/api/v1/employees/*").access("hasRole('ADMIN')")
                .antMatchers("/login*","/swagger-ui/**").permitAll().anyRequest().authenticated()
                .and()
                .httpBasic(Customizer.withDefaults());

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.headers().frameOptions().disable();
        http.csrf().disable();

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails tom = User.builder()
                .username("user")
                .password(encoder.encode("user"))
                .roles("USER")
                .build();

        UserDetails jerry = User.builder()
                .username("admin")
                .password(encoder.encode("admin"))
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(tom, jerry);
    }



}
