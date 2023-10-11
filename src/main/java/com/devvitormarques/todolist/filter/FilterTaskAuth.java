package com.devvitormarques.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.devvitormarques.todolist.user.IUserRepository;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private IUserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

       var authorization = request.getHeader("Authorization");
       
       var encodedAuth = authorization.substring("Basic".length()).trim();

       byte[] decodedAuth = Base64.getDecoder().decode(encodedAuth);

       var decodedString = new String(decodedAuth);

       System.out.println("Authorization");
       
       String[] credentials = decodedString.split(":");
       String username = credentials[0];
       String pw = credentials[1];
       System.out.println(username);
       System.out.println(pw);
       
       var user = this.userRepository.findByUsername(username);

       if(user == null) { 
        response.sendError(401);
       } else  {

       var verifyPw = BCrypt.verifyer().verify(pw.toCharArray(), user.getPassword());
        
        if(verifyPw.verified) {
          filterChain.doFilter(request, response);

        } else {
          response.sendError(401);
          
        }

       }


      }
      
}