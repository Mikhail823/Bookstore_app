package com.example.bookshop.security;

import com.example.bookshop.security.jwt.JWTRequestFilter;
import com.example.bookshop.security.jwt.blacklist.JWTBlackList;
import com.example.bookshop.security.oauth.CustomOAuth2UserService;

import com.example.bookshop.service.UserService;
import com.google.common.collect.ImmutableList;
import net.sf.ehcache.config.generator.ConfigurationSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AnonymousAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    public static final String SIGNIN = "/signin";

    private final BookstoreUserDetailsService bookstoreUserDetailsService;
    private final JWTRequestFilter filter;
    private final JWTBlackList jwtBlackList;
    private final CustomOAuth2UserService oAuth2UserService;

    @Autowired
    public SecurityConfig(BookstoreUserDetailsService bookstoreUserDetailsService,
                       JWTRequestFilter filter, JWTBlackList jwtBlackList,
                       CustomOAuth2UserService oAuth2UserService){
     this.bookstoreUserDetailsService = bookstoreUserDetailsService;
     this.filter = filter;
     this.jwtBlackList = jwtBlackList;
     this.oAuth2UserService = oAuth2UserService;

 }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(bookstoreUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable())
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/my", "/profile").authenticated()
                .antMatchers("/login", "/oauth/**", "/**").permitAll()
                .and().formLogin()
                .loginPage(SIGNIN)
                .failureUrl(SIGNIN).and().exceptionHandling()
                .and().logout().logoutUrl("/logout").invalidateHttpSession(true)
                .logoutSuccessUrl(SIGNIN)
                .deleteCookies("token")
                .addLogoutHandler(jwtBlackList)
                .and()
                .oauth2Login().loginPage(SIGNIN).userInfoEndpoint().userService(oAuth2UserService);
        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
         http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    }

    @Bean
    protected AnonymousAuthenticationFilter anonymousAuthenticationFilter(){
        return new AnonymousAuthenticationFilter("userKey", "anonymousUser",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
    }

    @Bean
    protected AnonymousAuthenticationProvider anonymousAuthenticationProvider(){
        return new AnonymousAuthenticationProvider("userKey");
    }
//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("https://auth.robokassa.ru"));
//        configuration.setAllowedMethods(Arrays.asList("GET","POST", "OPTIONS"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
}
