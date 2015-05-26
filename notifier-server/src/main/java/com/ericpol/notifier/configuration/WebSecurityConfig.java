package com.ericpol.notifier.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

/**
 * Configurations for security.
 */
@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{

    /*
     * @Autowired public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { auth
     * .inMemoryAuthentication() .withUser("user").password("pass").roles("USER"); }
     */

    @Autowired
    private AuthenticationHandlerImpl itsAuthHendler;

    @Override
    protected final void configure(final HttpSecurity aHttp) throws Exception
    {
        aHttp.authorizeRequests().antMatchers("/css/**").permitAll().antMatchers("/api/**").permitAll()
                .antMatchers("/img/**").permitAll().anyRequest().fullyAuthenticated().and().formLogin()
                .loginPage("/login").successHandler(itsAuthHendler).permitAll().and().logout().permitAll().and().csrf()
                .disable();

    }

    /**
     * Configuration class for LDAP authentication.
     */
    @Configuration
    protected static class AuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter
    {

        @Override
        public final void init(final AuthenticationManagerBuilder anAuth) throws Exception
        {

            anAuth.ldapAuthentication().userDnPatterns("uid=vvai,ou=people,dc=ericpol,dc=int")
                    .userSearchFilter("(uid={0})").userSearchBase("ou=people").contextSource()
                    .url("ldap://172.23.0.201:389/dc=ericpol,dc=int");

            /*
             * anAuth.ldapAuthentication().userDnPatterns("uid={0},ou=people,dc=springframework,dc=org")
             * .groupSearchBase("ou=groups").contextSource() .ldif("test-server.ldif");
             */
        }

    }

}
