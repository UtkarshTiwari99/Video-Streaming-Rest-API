package com.example.videostreamingapi.service;

import com.example.videostreamingapi.exception.IncorrectPasswordException;
import com.example.videostreamingapi.dto.AuthenticationRequest;
import com.example.videostreamingapi.model.User;
import com.example.videostreamingapi.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void saveUser(@NotNull User user) throws Exception {
        user.setPassword(encodePassword(user.getPassword()));
        if(userRepository.findByUsername(user.getUsername()).isEmpty())
            userRepository.save(user);
        else
            throw new Exception("username is not available");
    }

    public List<String> getRoles(String username){
        return userRepository.findByUsername(username).map(CustomUserDetails::new).get().getRoles();
    }

    public void matchPassword(AuthenticationRequest authenticationRequest){
        String password;
        try {
            password = userRepository.findByUsername(authenticationRequest.getUsername()).orElseThrow().getPassword();
        }catch (NoSuchElementException e){
            throw new NoSuchElementException("User not found");
        }
        if(!bCryptPasswordEncoder.matches(authenticationRequest.getPassword(),password))
            throw new IncorrectPasswordException("Incorrect password");
    }

    public User getUserByName(String username){
        return userRepository.findByUsername(username).orElseThrow();
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        var user= userRepository.findByUsername(s);
        user.orElseThrow(() -> new UsernameNotFoundException(s + " not found."));
        return user.map(CustomUserDetails::new).get();
    }

    public String encodePassword(String password){
        return bCryptPasswordEncoder.encode(password);
    }

}