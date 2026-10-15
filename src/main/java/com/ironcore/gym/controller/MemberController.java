package com.ironcore.gym.controller;

import com.ironcore.gym.dto.MemberDto;
import com.ironcore.gym.entity.Member;
import com.ironcore.gym.entity.Membership;
import com.ironcore.gym.service.AttendanceService;
import com.ironcore.gym.service.MemberService;
import com.ironcore.gym.service.MembershipService;
import com.ironcore.gym.service.PaymentService;
import com.ironcore.gym.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final MembershipService membershipService;
    private final AttendanceService attendanceService;
    private final PaymentService paymentService;
    private final WorkoutService workoutService;

    public MemberController(MemberService memberService,
                            MembershipService membershipService,
                            AttendanceService attendanceService,
                            PaymentService paymentService,
                            WorkoutService workoutService) {
        this.memberService = memberService;
        this.membershipService = membershipService;
        this.attendanceService = attendanceService;
        this.paymentService = paymentService;
        this.workoutService = workoutService;
    }

    @GetMapping
    public String listMembers(@RequestParam(value = "query", required = false) String query,
                              @RequestParam(value = "status", required = false, defaultValue = "ALL") String status,
                              @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                              @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                              Model model) {
        Page<Member> memberPage = memberService.getMembers(query, status,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));

        model.addAttribute("members", memberPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", memberPage.getTotalPages());
        model.addAttribute("totalElements", memberPage.getTotalElements());
        model.addAttribute("query", query);
        model.addAttribute("status", status);
        model.addAttribute("activeNav", "members");
        return "members/list";
    }

    @GetMapping("/new")
    public String newMemberForm(Model model) {
        model.addAttribute("memberDto", new MemberDto());
        model.addAttribute("plans", membershipService.getActivePlans());
        model.addAttribute("activeNav", "members");
        return "members/form";
    }

    @PostMapping("/create")
    public String createMember(@Valid @ModelAttribute("memberDto") MemberDto dto,
                               BindingResult bindingResult,
                               Authentication authentication,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("plans", membershipService.getActivePlans());
            model.addAttribute("activeNav", "members");
            return "members/form";
        }

        try {
            String performedBy = authentication != null ? authentication.getName() : "ADMIN";
            Member saved = memberService.registerMember(dto, performedBy);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Member " + saved.getFullName() + " registered successfully with Code: " + saved.getMemberCode());
            return "redirect:/members/" + saved.getId();
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("plans", membershipService.getActivePlans());
            model.addAttribute("activeNav", "members");
            return "members/form";
        }
    }

    @GetMapping("/{id}")
    public String memberDetails(@PathVariable("id") Long id, Model model) {
        Member member = memberService.getMemberById(id);
        Membership activeMembership = membershipService.getActiveMembership(id);

        model.addAttribute("member", member);
        model.addAttribute("activeMembership", activeMembership);
        model.addAttribute("memberships", membershipService.getMemberMemberships(id));
        model.addAttribute("attendances", attendanceService.getMemberAttendance(id));
        model.addAttribute("payments", paymentService.getMemberPayments(id));
        model.addAttribute("activeWorkout", workoutService.getActiveAssignmentForMember(id));
        model.addAttribute("plans", membershipService.getActivePlans());
        model.addAttribute("activeNav", "members");
        return "members/detail";
    }

    @GetMapping("/edit/{id}")
    public String editMemberForm(@PathVariable("id") Long id, Model model) {
        Member member = memberService.getMemberById(id);
        MemberDto dto = new MemberDto();
        dto.setId(member.getId());
        dto.setMemberCode(member.getMemberCode());
        dto.setFirstName(member.getFirstName());
        dto.setLastName(member.getLastName());
        dto.setEmail(member.getEmail());
        dto.setPhone(member.getPhone());
        dto.setDateOfBirth(member.getDateOfBirth());
        dto.setGender(member.getGender());
        dto.setAddress(member.getAddress());
        dto.setEmergencyContactName(member.getEmergencyContactName());
        dto.setEmergencyContactPhone(member.getEmergencyContactPhone());
        dto.setPhotoUrl(member.getPhotoUrl());
        dto.setStatus(member.getStatus());

        model.addAttribute("memberDto", dto);
        model.addAttribute("isEdit", true);
        model.addAttribute("plans", membershipService.getActivePlans());
        model.addAttribute("activeNav", "members");
        return "members/form";
    }

    @PostMapping("/update/{id}")
    public String updateMember(@PathVariable("id") Long id,
                               @Valid @ModelAttribute("memberDto") MemberDto dto,
                               BindingResult bindingResult,
                               Authentication authentication,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("plans", membershipService.getActivePlans());
            model.addAttribute("activeNav", "members");
            return "members/form";
        }

        try {
            String performedBy = authentication != null ? authentication.getName() : "ADMIN";
            memberService.updateMember(id, dto, performedBy);
            redirectAttributes.addFlashAttribute("successMessage", "Member profile updated successfully.");
            return "redirect:/members/" + id;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("isEdit", true);
            model.addAttribute("plans", membershipService.getActivePlans());
            model.addAttribute("activeNav", "members");
            return "members/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteMember(@PathVariable("id") Long id,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        String performedBy = authentication != null ? authentication.getName() : "ADMIN";
        memberService.deleteMember(id, performedBy);
        redirectAttributes.addFlashAttribute("successMessage", "Member status set to INACTIVE.");
        return "redirect:/members";
    }
}
