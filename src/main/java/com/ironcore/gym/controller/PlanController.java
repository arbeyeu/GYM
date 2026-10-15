package com.ironcore.gym.controller;

import com.ironcore.gym.entity.MembershipPlan;
import com.ironcore.gym.service.MembershipService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/plans")
public class PlanController {

    private final MembershipService membershipService;

    public PlanController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping
    public String listPlans(Model model) {
        model.addAttribute("plans", membershipService.getAllPlans());
        model.addAttribute("newPlan", new MembershipPlan());
        model.addAttribute("activeNav", "plans");
        return "plans/list";
    }

    @PostMapping("/create")
    public String createPlan(@ModelAttribute("newPlan") MembershipPlan plan,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            String performedBy = authentication != null ? authentication.getName() : "ADMIN";
            membershipService.createPlan(plan, performedBy);
            redirectAttributes.addFlashAttribute("successMessage", "Membership plan created successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/plans";
    }

    @PostMapping("/update/{id}")
    public String updatePlan(@PathVariable("id") Long id,
                             @ModelAttribute MembershipPlan plan,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            String performedBy = authentication != null ? authentication.getName() : "ADMIN";
            membershipService.updatePlan(id, plan, performedBy);
            redirectAttributes.addFlashAttribute("successMessage", "Membership plan updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/plans";
    }

    @PostMapping("/assign")
    public String assignPlan(@RequestParam("memberId") Long memberId,
                             @RequestParam("planId") Long planId,
                             @RequestParam(value = "paymentMethod", defaultValue = "UPI") String paymentMethod,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            String performedBy = authentication != null ? authentication.getName() : "ADMIN";
            membershipService.purchaseOrRenewMembership(memberId, planId, paymentMethod, performedBy);
            redirectAttributes.addFlashAttribute("successMessage", "Membership assigned and payment recorded successfully.");
            return "redirect:/members/" + memberId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/members/" + memberId;
        }
    }
}
