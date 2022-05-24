package com.example.ldaptest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.ppolicy.PasswordPolicyAwareContextSource;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.NestedLdapAuthoritiesPopulator;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final LdapSettings ldapSettings = LdapSettings.builder()
            .domain("company.local")
            .primaryUrl("ldap://ldap1.company.local:1389")
            .secondaryUrl("ldap://ldap2.company.local:2389")
            .usersBaseDn("dc=company,dc=local")
            .groupsBaseDn("dc=company,dc=local")
            .userDn("cn=admin,dc=company,dc=local")
            .userPassword("P@ssw0rd")
            .enabled(true)
            .build();

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().fullyAuthenticated().and().formLogin();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ldapAuthenticationProvider());
    }

    @Bean
    public LdapAuthenticationProvider ldapAuthenticationProvider() throws Exception {
        LdapAuthenticationProvider lAP = new LdapAuthenticationProvider(ldapAuthenticator(),
                ldapAuthoritiesPopulator());
        return lAP;
    }

    private LdapAuthoritiesPopulator ldapAuthoritiesPopulator() throws Exception {
        NestedLdapAuthoritiesPopulator dlap = new NestedLdapAuthoritiesPopulator(ldapContextSource(),
                ldapSettings.getGroupsBaseDn());
        dlap.setGroupSearchFilter("(|(member={0})(uniqueMember={0}))");
        return dlap;
    }

    @Bean
    public LdapContextSource ldapContextSource() throws Exception {
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(
                ldapSettings.getPrimaryUrl());// + " " + ldapSettings.getSecondaryUrl());
        contextSource.setUserDn(ldapSettings.getUserDn());
        contextSource.setPassword(ldapSettings.getUserPassword());
        return contextSource;
    }

    @Bean
    public LdapAuthenticator ldapAuthenticator() throws Exception {
        BindAuthenticator authenticator = new BindAuthenticator(ldapContextSource());
        authenticator.setUserSearch(new FilterBasedLdapUserSearch(ldapSettings.getUsersBaseDn(),
                "(|(userPrincipalName={0})(sAMAccountName={0})(uid={0}))", ldapContextSource()));
        return authenticator;
    }
}
