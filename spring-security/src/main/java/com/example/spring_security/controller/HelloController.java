package com.example.spring_security.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {


    @RequestMapping("/home")
    public String home(){
        return "Hello Jerin";
    }

    @RequestMapping("/")
    public String index(HttpServletRequest request){
        return "This is the session ID: " + request.getSession().getId();
    }
}
