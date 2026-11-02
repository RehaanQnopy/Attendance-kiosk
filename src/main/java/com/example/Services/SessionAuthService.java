package com.example.Services;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import com.example.Models.User;

@Service 
public class SessionAuthService {

    public void signIn ( User user, HttpSession session)
    {
        /* 
            1.SimpleGrantedAuthority, is waht transforms a role into a Spring Security authority.
            2.stream() Converts the collection of roles into a sequential stream of data.
            3.In map we pass the role we get from the user object and we convert it into a SimpleGrantedAuthority object.
        */
        List<SimpleGrantedAuthority> authorities = user.getRole().stream().map(r->new SimpleGrantedAuthority("ROLE_" + r.toUpperCase())).toList();
        /*
            The UsernamePasswordAuthenticationToken's 3 parameter{prinicipal, password, authorities} 
            acts as an official security passport by taking the 
            User ID, a null password, and the roles array, 
            which automatically flips its internal switch to setAuthenticated(true) because 
            it sees the roles attached, ensuring the user is fully verified and stays valid without needing 
            to check their password again.
         */
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getId().toString(), null, authorities);

        // The container the security filters read
        SecurityContext context =  SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);

        /*
            The below line saves your user's entire authentication status (their "security passport") directly into the server’s HTTP session memory 
            under a special, secret key name that Spring Security owns. 

            By default, Java applications forget who a user is the second a webpage reloads or a new request is sent. 
            This line acts as the permanent memory anchor. 
            It takes your context object (which holds the UsernamePasswordAuthenticationToken we discussed earlier) 
            and locks it into the user's active session.
        */
       session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
       session.setAttribute("userId", user.getId().toString());


    }


}