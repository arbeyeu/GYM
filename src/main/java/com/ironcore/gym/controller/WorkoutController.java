package com.ironcore.gym.controller;

import com.ironcore.gym.dto.WorkoutPlanDto;
import com.ironcore.gym.entity.Exercise;
import com.ironcore.gym.entity.WorkoutPlan;
import com.ironcore.gym.service.MemberService;
import com.ironcore.gym.service.TrainerService;
import com.ironcore.gym.service.WorkoutService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/workouts")
public class WorkoutController {

    private final WorkoutService workoutService;
    private final TrainerService trainerService;
    private final MemberService memberService;

    public WorkoutController(WorkoutService workoutService,
                             TrainerService trainerService,
                             MemberService memberService) {
        this.workoutService = workoutService;
        this.trainerService = trainerService;
        this.memberService = memberService;
    }

    @GetMapping
    public String listWorkouts(Model model) {
        model.addAttribute("plans", workoutService.getAllWorkoutPlans());
        model.addAttribute("exercises", workoutService.getAllExercises());
        model.addAttribute("members", memberService.getAllMembers());
        model.addAttribute("trainers", trainerService.getActiveTrainers());
        model.addAttribute("activeNav", "workouts");
        return "workouts/list";
    }

    @GetMapping("/builder")
    public String workoutBuilder(@RequestParam(value = "id", required = false) Long id, Model model) {
        WorkoutPlanDto dto = new WorkoutPlanDto();
        if (id != null) {
            WorkoutPlan plan = workoutService.getPlanById(id);
            dto.setId(plan.getId());
            dto.setName(plan.getName());
            dto.setDescription(plan.getDescription());
            dto.setDifficultyLevel(plan.getDifficultyLevel());
            dto.setGoal(plan.getGoal());
            dto.setDurationWeeks(plan.getDurationWeeks());
            if (plan.getTrainer() != null) {
                dto.setTrainerId(plan.getTrainer().getId());
            }
        }

        model.addAttribute("planDto", dto);
        model.addAttribute("exercises", workoutService.getAllExercises());
        model.addAttribute("trainers", trainerService.getActiveTrainers());
        model.addAttribute("activeNav", "workouts");
        return "workouts/builder";
    }

    @PostMapping("/save")
    public String savePlan(@ModelAttribute("planDto") WorkoutPlanDto dto,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            String performedBy = authentication != null ? authentication.getName() : "TRAINER";
            WorkoutPlan saved = workoutService.saveWorkoutPlan(dto, performedBy);
            redirectAttributes.addFlashAttribute("successMessage", "Workout plan '" + saved.getName() + "' saved successfully.");
            return "redirect:/workouts";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/workouts/builder";
        }
    }

    @PostMapping("/assign")
    public String assignPlan(@RequestParam("memberId") Long memberId,
                             @RequestParam("planId") Long planId,
                             @RequestParam(value = "trainerId", required = false) Long trainerId,
                             @RequestParam(value = "notes", required = false) String notes,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            String performedBy = authentication != null ? authentication.getName() : "TRAINER";
            workoutService.assignPlanToMember(memberId, planId, trainerId, notes, performedBy);
            redirectAttributes.addFlashAttribute("successMessage", "Workout plan assigned to member successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/members/" + memberId;
    }

    @PostMapping("/exercise/create")
    public String createExercise(@ModelAttribute Exercise exercise,
                                 RedirectAttributes redirectAttributes) {
        try {
            workoutService.createExercise(exercise);
            redirectAttributes.addFlashAttribute("successMessage", "Exercise added to library.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/workouts";
    }
}
