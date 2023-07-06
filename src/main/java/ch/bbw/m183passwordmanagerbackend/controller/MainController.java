package ch.bbw.m183passwordmanagerbackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class MainController {
    public MainController() {
    }

    @GetMapping("/")
    public String Start(){

        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/dashboard")
    public String Home(){

        return "home";
    }

}
