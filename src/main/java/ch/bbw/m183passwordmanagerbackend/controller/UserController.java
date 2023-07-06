package ch.bbw.m183passwordmanagerbackend.controller;

import ch.bbw.m183passwordmanagerbackend.model.User;
import ch.bbw.m183passwordmanagerbackend.service.EntryRepository;
import ch.bbw.m183passwordmanagerbackend.service.PasswordEncrypterService;
import ch.bbw.m183passwordmanagerbackend.service.UserRepository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {

    private UserRepository userRepository;
    private EntryRepository entryRepository;

    private PasswordEncrypterService passwordEncrypterService;

    public UserController(UserRepository userRepository, EntryRepository entryRepository) {
        this.userRepository = userRepository;
        this.entryRepository = entryRepository;
    }

    @GetMapping("/login")
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
    public String signup(@ModelAttribute Model model, User user) {

        boolean isExistingUser = userRepository.existsById(user.getId());
        if (isExistingUser) {
            model.addAttribute("error", "Entry with the same URL already exists for the current user.");
            return "redirect:/login";
        }

        // Encrypt the password using AES
        String encryptedPassword = getPasswordEncrypterService().encryptPassword(user.getMasterPassword(), user.getEmail());

        // Save the encrypted password to the entry
        user.setMasterPassword(encryptedPassword);

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

    @PutMapping("/{id}/updateUser")
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        // Find the existing entry
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with id: " + id);
        }

        User existingUser = optionalUser.get();

        // Update the fields of the existing entry
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setMasterPassword(updatedUser.getMasterPassword());

        // Encrypt the password if it has changed
        if (!existingUser.getMasterPassword().equals(updatedUser.getMasterPassword())) {
            String encryptedPassword = getPasswordEncrypterService().encryptPassword(updatedUser.getMasterPassword(), updatedUser.getEmail());
            existingUser.setMasterPassword(encryptedPassword);
        }

        // Save the updated entry
        return userRepository.save(existingUser);
    }

    public PasswordEncrypterService getPasswordEncrypterService() {
        return passwordEncrypterService;
    }

    public void setPasswordEncrypterService(PasswordEncrypterService passwordEncrypterService) {
        this.passwordEncrypterService = passwordEncrypterService;
    }

}

