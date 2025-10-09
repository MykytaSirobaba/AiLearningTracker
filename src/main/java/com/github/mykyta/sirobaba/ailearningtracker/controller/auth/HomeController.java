package com.github.mykyta.sirobaba.ailearningtracker.controller.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Mykyta Sirobaba on 07.10.2025.
 * email mykyta.sirobaba@gmail.com
 */
@RestController
public class HomeController {
    @GetMapping("/home")
    public String home() {
        return "Hello World";
    }
}
