package ru.bfad.bfaApp.webComponent.configs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.bfad.bfaApp.webComponent.services.UserService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers()
                .frameOptions().sameOrigin()
                .and()
                .authorizeRequests()
//                .antMatchers("/resources/**", "/webjars/**","/assets/**").permitAll()
//                .antMatchers("/main/mailsLists", "/main/message").authenticated()
//                .antMatchers("/main/addPersonToMailListUtil").hasAnyRole("USER")
                .antMatchers("/resources/**", "/webjars/**","/assets/**").permitAll()
                .antMatchers("/main/mailsLists", "/main/message", "/main/saveMailsList").authenticated()
                .antMatchers("/main/addPersonToMailListUtil", "/main/sendMessage", "/main/mailsLists").hasAnyRole("USER")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/main/login")
                .defaultSuccessUrl("/main/home")
//                .failureUrl("/main/login")
                .permitAll()
                .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/main/logout"))
                .logoutSuccessUrl("/main/home")
                .permitAll();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
}
