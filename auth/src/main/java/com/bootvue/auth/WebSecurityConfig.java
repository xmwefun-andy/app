package com.bootvue.auth;

import com.bootvue.auth.authc.AppUserDetailService;
import com.bootvue.auth.encoder.Md5PasswordEncoder;
import com.bootvue.auth.filter.AppAuthenticationFilter;
import com.bootvue.auth.filter.AppSmsAuthenticationFilter;
import com.bootvue.auth.filter.AppUsernamePasswordAuthenticationFilter;
import com.bootvue.auth.handler.AccessFailHandler;
import com.bootvue.auth.handler.FailHandler;
import com.bootvue.auth.handler.SuccessHandler;
import com.bootvue.auth.provider.AppAuthenticationProvider;
import com.bootvue.auth.provider.JwtAuthenticationProvider;
import com.bootvue.auth.provider.SmsAuthenticationProvider;
import com.bootvue.common.config.AppConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final Md5PasswordEncoder md5PasswordEncoder;
    private final AppUserDetailService appUserDetailService;
    private final SuccessHandler successHandler;
    private final FailHandler failHandler;
    private final AccessFailHandler accessFailHandler;
    private final AppConfig appConfig;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
                .addFilterBefore(appUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(appSmsAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .formLogin()
                .loginProcessingUrl("/login")
                .and().authorizeRequests()
                .antMatchers("/admin/**").hasAnyRole("admin")
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new AppAuthenticationFilter(authenticationManager(), appConfig))
                .exceptionHandling()
                .accessDeniedHandler(accessFailHandler)
                .and().csrf().disable();
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        // 认证provider
        AppAuthenticationProvider daoProvider = new AppAuthenticationProvider(md5PasswordEncoder, appUserDetailService);
        SmsAuthenticationProvider smsProvider = new SmsAuthenticationProvider();
        JwtAuthenticationProvider jwtProvider = new JwtAuthenticationProvider(appUserDetailService);

        ProviderManager providerManager = new ProviderManager(Arrays.asList(daoProvider, smsProvider, jwtProvider));
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

    protected AppUsernamePasswordAuthenticationFilter appUsernamePasswordAuthenticationFilter() throws Exception {
        AppUsernamePasswordAuthenticationFilter filter = new AppUsernamePasswordAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManagerBean());
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failHandler);
        return filter;
    }

    protected AppSmsAuthenticationFilter appSmsAuthenticationFilter() throws Exception {
        AppSmsAuthenticationFilter filter = new AppSmsAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManagerBean());
        filter.setAuthenticationSuccessHandler(successHandler);
        filter.setAuthenticationFailureHandler(failHandler);
        return filter;
    }

}
