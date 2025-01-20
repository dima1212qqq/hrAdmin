package ru.dovakun;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewInterceptor;

@Configuration
public class Config {
    @Bean
    public OpenEntityManagerInViewInterceptor openSessionInViewInterceptor() {
        return new OpenEntityManagerInViewInterceptor();
    }
}
