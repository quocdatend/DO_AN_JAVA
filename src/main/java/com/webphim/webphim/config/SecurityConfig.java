package com.webphim.webphim.config;

import com.webphim.webphim.Service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorize -> authorize
                        .requestMatchers( "/", "/Home", "/Home/**", "/Login_Signup", "/logout", "/assets/**", "/Blog", "/Blog/**", "/Movies", "/Movies/**").permitAll()
                        .requestMatchers("/User/**").hasAnyAuthority("USER")
                        .requestMatchers("/Admin/**").hasAnyAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/Login_Signup")
                        .loginProcessingUrl("/Login_Signup")
                        .defaultSuccessUrl("/Home", true)
                        .failureUrl("/Login_Signup?error")
                        .permitAll())
                .logout((logout) -> logout
                        .logoutUrl("/Login_Signup/logout") // URL tùy chỉnh để đăng xuất
                        .logoutSuccessUrl("/Home") // URL sau khi đăng xuất thành công
                        .invalidateHttpSession(true) // Xóa phiên làm việc
                        .deleteCookies("JSESSIONID")
                        .permitAll())
                .httpBasic(withDefaults())
                .authenticationProvider(authenticationProvider());
        return http.build();
    }
    @Autowired
    private CustomUserDetailsService uds;

    //@Autowired
    //private CustomUAdminDetailsService ads;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(uds);
        //authenticationProvider.setUserDetailsService(ads);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
}