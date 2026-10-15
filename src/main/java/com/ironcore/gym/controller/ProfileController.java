package com.ironcore.gym.controller;

import com.ironcore.gym.entity.User;
import com.ironcore.gym.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String profilePage(Authentication authentication, Model model) {
        String username = authentication != null ? authentication.getName() : "admin";
        User user = userRepository.findByUsername(username).orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("activeNav", "profile");
        return "profile/index";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam("fullName") String fullName,
                                @RequestParam("email") String email,
                                @RequestParam(value = "newPassword", required = false) String newPassword,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            String username = authentication != null ? authentication.getName() : "admin";
            User user = userRepository.findByUsername(username).orElseThrow();
            user.setFullName(fullName);
            user.setEmail(email);
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                user.setPasswordHash(passwordEncoder.encode(newPassword.trim()));
            }
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Profile settings saved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile";
    }
}
