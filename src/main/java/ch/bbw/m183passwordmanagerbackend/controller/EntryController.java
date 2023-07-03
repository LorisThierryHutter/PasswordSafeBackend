package ch.bbw.m183passwordmanagerbackend.controller;

import ch.bbw.m183passwordmanagerbackend.model.Entry;
import ch.bbw.m183passwordmanagerbackend.model.User;
import ch.bbw.m183passwordmanagerbackend.service.EntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
public class EntryController {

    private final EntryRepository entryRepository;

    public EntryController(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    @GetMapping("/entries")
    public String getEntries(Model model) {
        List<Entry> entries = entryRepository.findAll();
        model.addAttribute("entries", entries);
        return "entries";
    }

    @GetMapping("/addEntry")
    public String showAddEntryForm() {
        return "addEntry";
    }

    @PostMapping("/entries")
    public String addEntry(@ModelAttribute Entry entry, Model model, User user) {
        // Check for duplicate entry
        boolean isDuplicate = entryRepository.existsByUrlAndUserEmail(entry.getUrl(), user.getEmail());
        if (isDuplicate) {
            model.addAttribute("error", "Entry with the same URL already exists for the current user.");
            return "redirect:/entries";
        }

        // Encrypt the password using AES
        String encryptedPassword = encryptPassword(entry.getPassword(), user.getEmail());

        // Save the encrypted password to the entry
        entry.setPassword(encryptedPassword);

        entryRepository.save(entry);
        return "redirect:/entries";
    }

    @PutMapping("/{id}")
    public Entry updateEntry(@PathVariable Long id, @RequestBody Entry updatedEntry) {
        // Find the existing entry
        Optional<Entry> optionalEntry = entryRepository.findById(id);
        if (optionalEntry.isEmpty()) {
            throw new RuntimeException("Entry not found with id: " + id);
        }

        Entry existingEntry = optionalEntry.get();

        // Update the fields of the existing entry
        existingEntry.setEntryName(updatedEntry.getEntryName());
        existingEntry.setUserEmail(updatedEntry.getUserEmail());
        existingEntry.setUrl(updatedEntry.getUrl());

        // Encrypt the password if it has changed
        if (!existingEntry.getPassword().equals(updatedEntry.getPassword())) {
            String encryptedPassword = encryptPassword(updatedEntry.getPassword(), updatedEntry.getUserEmail());
            existingEntry.setPassword(encryptedPassword);
        }

        // Save the updated entry
        return entryRepository.save(existingEntry);
    }

    @GetMapping("/{id}/delete")
    public String deleteEntry(@PathVariable Long id) {
        entryRepository.deleteById(id);
        return "redirect:/entries";
    }

    private String encryptPassword(String password, String userKey) {
        try {
            // Generate a random salt
            SecureRandom secureRandom = new SecureRandom();
            byte[] salt = new byte[16]; // Salt length in bytes
            secureRandom.nextBytes(salt);

            // Convert the salt to a Base64-encoded string for storage
            String saltString = Base64.getEncoder().encodeToString(salt);

            // Derive a secret key from the user's master password and salt
            SecretKeySpec secretKey = deriveSecretKey(userKey, salt);

            // Create AES cipher
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            // Initialize the cipher in encryption mode with the secret key
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // Encrypt the password
            byte[] encryptedBytes = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));

            // Encode the encrypted bytes as a Base64 string
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            // Handle encryption errors
            e.printStackTrace();
            // Return an empty string or handle the error case as appropriate
            return "";
        }
    }

    private SecretKeySpec deriveSecretKey(String userKey, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Convert the user's master password to a char array
        char[] passwordChars = userKey.toCharArray();

        // Derive a secret key using PBKDF2 with the provided salt and iteration count
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        PBEKeySpec keySpec = new PBEKeySpec(passwordChars, salt, 65536, 256);
        SecretKey secretKey = keyFactory.generateSecret(keySpec);

        // Convert the secret key to AES key specification
        return new SecretKeySpec(secretKey.getEncoded(), "AES");
    }

}

