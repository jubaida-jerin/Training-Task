package com.example.spring_security.service;


import com.example.spring_security.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.spring_security.repository.UserRepo;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;


    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthenticationManager authManager;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    public User register(User user){
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

//    public User login(User user) {
//        User existingUser = userRepo.findByUsername(user.getUsername());
//        if (existingUser != null && encoder.matches(user.getPassword(), existingUser.getPassword())) {
//            return existingUser;
//        }
//        return null;
//    }

    public String verifyUser(User user){

        try{
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );

            if ( authentication.isAuthenticated()){
                return jwtService.generateToken(user.getUsername());
            } else {
                return "Invalid username or password";
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
            return "Authentication failed: " + e.getMessage();
        }
    }

}
