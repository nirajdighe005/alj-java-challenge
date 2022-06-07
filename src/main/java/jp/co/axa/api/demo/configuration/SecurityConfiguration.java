package jp.co.axa.api.demo.configuration;

import org.springframework.context.annotation.Bean;
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
        /*http
                .authorizeHttpRequests((manager) -> manager
                        .antMatchers(HttpMethod.POST, "/api/v1/employees").hasRole("ADMIN")
                        .antMatchers(HttpMethod.PUT, "/api/v1/employees").hasRole("ADMIN")
                        .antMatchers(HttpMethod.DELETE, "/api/v1/employees/**").hasRole("ADMIN")
                        .antMatchers(HttpMethod.GET, "/api/v1/employees").hasRole("USER")
                        .antMatchers(HttpMethod.GET, "/api/v1/employees/**").hasRole("USER")
                        .antMatchers("/swagger-ui/**").permitAll().anyRequest().authenticated()
                )*/

        http
                .authorizeRequests()
                .antMatchers("/swagger-ui/**").permitAll().anyRequest().authenticated()
                .and()
                .httpBasic(Customizer.withDefaults());

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.headers().frameOptions().disable();
        http.csrf().disable();
        http.formLogin();
        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails tom = User.builder()
                .username("tom")
                .password(encoder.encode("password"))
                .roles("VIEWER")
                .build();

        UserDetails jerry = User.builder()
                .username("jerry")
                .password(encoder.encode("password"))
                .roles("EDITOR")
                .build();
        return new InMemoryUserDetailsManager(tom, jerry);
    }



}
