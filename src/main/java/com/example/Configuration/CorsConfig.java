package com.example.Configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration 
/* Below tag is used to activate and enable Spring Security's web security support in a Spring Boot application */
@EnableWebSecurity 
public class CorsConfig {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Bean 
    /* 
        Nuances to be Remembered:
        1. Use Bean and keep the method public, so that it can be called, by the Spring Framework at the time app booting 
        2. SecurityFilterChain, are  used for telling Spring Framework to check for these Security checkmarks.
        3. You use HttpSecurity type to get the methods to create the Security list
    */
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        //cors enables the CORS and the configurationSource allows you to pass the CORS setting you create using
        //CorsConfiguration
            .cors(cors->cors.configurationSource(corsConfigurationList()))
        //csrf is CROSS-SITE-REQUEST-FOGERY, deals with the process of token exchange, currently diabled, as its a headache, 
            //but must be enabled later
            .csrf(csrf -> csrf.disable())
        //authorizeHttpRequest is used for defining the firewall for your Urls, basically who all are required to reach your url
            //the .anyRequest() means anybody, and .authenticated() means anybody who has credentials 
            //But the issue here is that, only hte logged in users are allowed to enter the webiste, so the first time will not be allowed,
            //Therefore we have to use the .permitAll() for requestMatchers() for the authorization only
            //This means fro authorization everybody is allowed who hit the path allwoed in requestMatchers()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/user", "/auth/login", "/error").permitAll() 
                .anyRequest().authenticated())
                //logout is the method that enables the logout process, it takes lambda function as input
                //Very important: HOW TO READ THE LAMBDA FUNCTION
                    //Here, read it as take[input] ->(and do this with the paramtere) 
                    //logout is the configuration object we get for using .logout()
            .logout(logout -> logout
                .logoutUrl("/auth/logout") //Sets the url that triggers the logout process
                .invalidateHttpSession(true) //This is used to destory the HttpSession of the user at the Backend side
                .clearAuthentication(true) //This ensures that the current user is not considered as valid user
                .deleteCookies("JSESSIONID") //This ensures that the session cookie is considered to be expired one 
                //This is important, after the succesfull logout, by default SpringSecurity re-directs, but here we are ensuring that a 
                // success message is sent back to the frontend
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK)) 
            );

        return http.build();
    }
    /* 
                This is what we are doing in below mwthod:
• Created a CORS Rule Blueprint: Defined specific security permissions allowing your Angular app 
(http://localhost:4200) to send credentials like sessions and cookies (allowCredentials(true)).

• Targeted Every URL (/**): double-asterisk tells Spring to apply these security rules to every single endpoint 
across your entire backend, in the UrlBasedCorsConfiguration.

• Registered the Rules: Registered these URL mappings inside Spring's central ledger (CorsRegistry).

• Handed Over Control: You wrapped it in a @Bean configuration method, 
handing the fully configured ledger over to Spring to automatically run on every incoming browser request.
    */
    @Bean 
    //The CorsConfigurationSource is where you define the policies for the CORS 
    public CorsConfigurationSource corsConfigurationList() {
        CorsConfiguration config = new CorsConfiguration();
        //The frontendUrl we got from the @Value stored in the private string named frontendUrl
        config.setAllowedOrigins(List.of(frontendUrl));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source  = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
