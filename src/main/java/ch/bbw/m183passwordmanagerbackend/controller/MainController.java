package ch.bbw.m183passwordmanagerbackend.controller;

import ch.bbw.m183passwordmanagerbackend.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {
    @GetMapping("/")
    public String Start(){

        return "login";
    }

    @GetMapping("/dashboard")
    public String Home(){

        return "home";
    }

}
