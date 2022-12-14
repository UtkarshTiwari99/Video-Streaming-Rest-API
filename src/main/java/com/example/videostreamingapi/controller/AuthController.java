package com.example.videostreamingapi.controller;

import com.example.videostreamingapi.dto.AuthenticationRequest;
import com.example.videostreamingapi.dto.AuthenticationResponse;
import com.example.videostreamingapi.dto.SignUpResponse;
import com.example.videostreamingapi.model.User;
import com.example.videostreamingapi.service.CustomUserDetailsService;
import com.example.videostreamingapi.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.NoSuchElementException;

@RestController
class AuthController {
    @Autowired
    CustomUserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtTokenUtil;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;


    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest,HttpServletResponse httpServletResponse) throws Exception {
        Authentication authentication;
        try {
            customUserDetailsService.matchPassword(authenticationRequest);
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        } catch (BadCredentialsException | NoSuchElementException e ) {
            System.out.println(e.getMessage());
            if(e instanceof NoSuchElementException)
              return ResponseEntity.status(400).body(new AuthenticationResponse(400,e.getMessage(),""));
            return ResponseEntity.status(400).body(new AuthenticationResponse(400,"" ,e.getMessage()));
        }
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtTokenUtil.generateToken(userDetails, authentication);
        httpServletResponse.addCookie(new Cookie("Bearer",jwt));
        System.out.println(userDetails.getUsername()+" "+jwt);
        return ResponseEntity.status(200).body(new AuthenticationResponse(200,userDetails.getUsername(),jwt));
    }

    @RequestMapping(value = "signup",method= RequestMethod.POST)
    public ResponseEntity<SignUpResponse> signupUser(@RequestBody AuthenticationRequest authenticationRequest){
        try{
        userDetailsService.saveUser(new User(authenticationRequest.getUsername(), authenticationRequest.getEmail(),authenticationRequest.getPassword(),authenticationRequest.getUsername(),true));}
        catch (Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.status(400).body(new SignUpResponse(e.getMessage()));
        }
        return ResponseEntity.status(200).body(new SignUpResponse("Success"));
    }

}