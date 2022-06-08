package jp.co.axa.api.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableWebMvc
public class ApiDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiDemoApplication.class, args);
    }
}
