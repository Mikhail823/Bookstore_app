package com.example.bookshop.sessions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.jdbc.JdbcIndexedSessionRepository;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@EnableJdbcHttpSession
@Configuration
@Order(2)
public class SpringSessionConfig extends WebSecurityConfigurerAdapter {

    private final ConcurrentSessionStrategy concurrentSessionStrategy;
    private final SessionRegistry sessionRegistry;
    private final AuthenticationFailureHandler securityErrorHandler;

    @Autowired
    public SpringSessionConfig(ConcurrentSessionStrategy concurrentSessionStrategy,
                               SessionRegistry sessionRegistry,
                               AuthenticationFailureHandler securityErrorHandler){
        this.concurrentSessionStrategy = concurrentSessionStrategy;
        this.sessionRegistry = sessionRegistry;
        this.securityErrorHandler = securityErrorHandler;
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http

                //Включаем менеджер сессий(для контроля количества сессий)
                .sessionManagement()
                //Указываем макимальное возможное количество сессий(тут указано не 1, т.к. мы будем пользоваться своей кастомной стратегией, объяснение будет ниже)
                .maximumSessions(30)
                //При превышение количества активных сессий(3) выбрасывается исключение  SessionAuthenticationException
                .maxSessionsPreventsLogin(true)
                //Указываем как будут регестрироваться наши сессии(тогда во всем приложение будем использовать именно этот бин)
                .sessionRegistry(sessionRegistry).and()
                //Добавляем нашу кастомную стратегию для проверки кличества сессий
                .sessionAuthenticationStrategy(concurrentSessionStrategy)
                //Добавляем перехватчик для исключений
                .sessionAuthenticationFailureHandler(securityErrorHandler);

    }

    //для инвалидации сессий при логауте
    @Bean
    public static ServletListenerRegistrationBean httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean(new HttpSessionEventPublisher());
    }

    @Bean
    public static SessionRegistry sessionRegistry(JdbcIndexedSessionRepository sessionRepository) {
        return new SpringSessionBackedSessionRegistry(sessionRepository);
    }
}
