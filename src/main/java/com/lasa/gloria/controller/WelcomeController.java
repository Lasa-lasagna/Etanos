package com.lasa.gloria.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/hola")
public class WelcomeController {

    @GetMapping
    public String getMethodName() {
        return new String("Hola Mundo");
    }
    
}
