package ch.bbw.m183passwordmanagerbackend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String Home(){
        return "home";
    }

}