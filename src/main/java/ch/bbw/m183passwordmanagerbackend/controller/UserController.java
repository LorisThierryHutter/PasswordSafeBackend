package ch.bbw.m183passwordmanagerbackend.controller;

import ch.bbw.m183passwordmanagerbackend.model.User;
import ch.bbw.m183passwordmanagerbackend.service.EntryRepository;
import ch.bbw.m183passwordmanagerbackend.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;
    private EntryRepository entryRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Autowired
    public UserController(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email, @RequestParam("password") String password) {
        // Retrieve the user from the database based on the email
        User user = userRepository.findByEmail(email);

        if (user != null && user.getMasterPassword().equals(password)) {
            // User found and password matches, perform the login logic
            // Redirect to the main page or dashboard
            return "redirect:/dashboard";
        } else {
            // Invalid credentials, show an error message or redirect back to the login page
            return "redirect:/login?error";
        }
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute Model model, User user) {
        userRepository.save(user);
        return "redirect:/dashboard";
    }

    @GetMapping("/user/{id}")
    public String getUserData(@PathVariable int id, Model model) {
        // Retrieve user data
        User user = userRepository.getUserById(id);

        // Get the entry count for the user
        int entryCount = entryRepository.getEntryCountByUser(user);

        // Add data to the model
        model.addAttribute("user", user);
        model.addAttribute("entryCount", entryCount);

        return "user"; // Return the name of the user profile HTML page
    }

}

