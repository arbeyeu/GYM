package com.ironcore.gym.controller;

import com.ironcore.gym.entity.Trainer;
import com.ironcore.gym.service.TrainerService;
import com.ironcore.gym.service.WorkoutService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/trainers")
public class TrainerController {

    private final TrainerService trainerService;
    private final WorkoutService workoutService;

    public TrainerController(TrainerService trainerService, WorkoutService workoutService) {
        this.trainerService = trainerService;
        this.workoutService = workoutService;
    }

    @GetMapping
    public String listTrainers(Model model) {
        model.addAttribute("trainers", trainerService.getAllTrainers());
        model.addAttribute("newTrainer", new Trainer());
        model.addAttribute("activeNav", "trainers");
        return "trainers/list";
    }

    @PostMapping("/create")
    public String createTrainer(@ModelAttribute("newTrainer") Trainer trainer,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            String performedBy = authentication != null ? authentication.getName() : "ADMIN";
            Trainer saved = trainerService.createTrainer(trainer, performedBy);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Trainer " + saved.getFullName() + " created with Code: " + saved.getTrainerCode());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/trainers";
    }

    @PostMapping("/update/{id}")
    public String updateTrainer(@PathVariable("id") Long id,
                                @ModelAttribute Trainer trainer,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            String performedBy = authentication != null ? authentication.getName() : "ADMIN";
            trainerService.updateTrainer(id, trainer, performedBy);
            redirectAttributes.addFlashAttribute("successMessage", "Trainer details updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/trainers";
    }

    @PostMapping("/delete/{id}")
    public String deleteTrainer(@PathVariable("id") Long id,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        String performedBy = authentication != null ? authentication.getName() : "ADMIN";
        trainerService.deleteTrainer(id, performedBy);
        redirectAttributes.addFlashAttribute("successMessage", "Trainer deactivated.");
        return "redirect:/trainers";
    }
}
