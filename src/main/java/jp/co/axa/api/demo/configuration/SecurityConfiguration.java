package jp.co.axa.api.demo.configuration;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${api.demo.user.username}")
    String userName;

    @Value("${api.demo.admin.username}")
    String adminName;

    @Value("${api.demo.user.password}")
    String userPassword;

    @Value("${api.demo.admin.password}")
    String adminPassword;

    public static final String ADMIN_ROLE = "ADMIN";
    public static final String USER_ROLE = "USER";
    private final PasswordEncoder encoder =
            PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public static final String EMPLOYEE_API_PATH = "/api/v1/employees";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.authorizeRequests()
                //match api paths and provide access to appropriate user.
                .antMatchers(HttpMethod.GET, EMPLOYEE_API_PATH + "/*").access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.GET, EMPLOYEE_API_PATH).access("hasRole('USER') or hasRole('ADMIN')")
                .antMatchers(HttpMethod.POST, EMPLOYEE_API_PATH).access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.PUT, EMPLOYEE_API_PATH).access("hasRole('ADMIN')")
                .antMatchers(HttpMethod.DELETE, EMPLOYEE_API_PATH + "/*").access("hasRole('ADMIN')")
                .antMatchers("/login*", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic(Customizer.withDefaults())
                //make the session stateless
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().headers().frameOptions().disable()
                .and().csrf().disable()
                .build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.builder()
                .username(userName)
                .password(encoder.encode(userPassword))
                .roles(USER_ROLE)
                .build();

        UserDetails admin = User.builder()
                .username(adminName)
                .password(encoder.encode(adminPassword))
                .roles(ADMIN_ROLE)
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }


}
