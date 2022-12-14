package com.example.videostreamingapi.controller;

import com.example.videostreamingapi.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QuoteController{
    @Autowired
    public CustomUserDetailsService customUserDetailsService;

    @RequestMapping("/user")
    @PreAuthorize("hasRole('USER')")
        public String getQuotes(){
            return "user";
        }


    @RequestMapping("/user/{user}")
    @PreAuthorize("hasRole('USER')")
    public List<String> get(@PathVariable String user){
        return customUserDetailsService.getRoles(user);
    }

    @RequestMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin(){return "admin";}

}