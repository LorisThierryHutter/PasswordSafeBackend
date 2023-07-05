package ch.bbw.m183passwordmanagerbackend.controller;

import ch.bbw.m183passwordmanagerbackend.model.Entry;
import ch.bbw.m183passwordmanagerbackend.model.User;
import ch.bbw.m183passwordmanagerbackend.service.EntryRepository;
import ch.bbw.m183passwordmanagerbackend.service.PasswordEncrypterService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class EntryController {

    private final EntryRepository entryRepository;

    private PasswordEncrypterService passwordEncrypterService;

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
        String encryptedPassword = getPasswordEncrypterService().encryptPassword(entry.getPassword(), user.getEmail());

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
            String encryptedPassword = getPasswordEncrypterService().encryptPassword(updatedEntry.getPassword(), updatedEntry.getUserEmail());
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

    public PasswordEncrypterService getPasswordEncrypterService() {
        return passwordEncrypterService;
    }

    public void setPasswordEncrypterService(PasswordEncrypterService passwordEncrypterService) {
        this.passwordEncrypterService = passwordEncrypterService;
    }

}

