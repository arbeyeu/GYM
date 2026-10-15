package com.ironcore.gym.controller;

import com.ironcore.gym.dto.DashboardStatsDto;
import com.ironcore.gym.entity.*;
import com.ironcore.gym.repository.UserRepository;
import com.ironcore.gym.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final MemberService memberService;
    private final TrainerService trainerService;
    private final MembershipService membershipService;
    private final AttendanceService attendanceService;
    private final PaymentService paymentService;
    private final WorkoutService workoutService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    public DashboardController(DashboardService dashboardService,
                               MemberService memberService,
                               TrainerService trainerService,
                               MembershipService membershipService,
                               AttendanceService attendanceService,
                               PaymentService paymentService,
                               WorkoutService workoutService,
                               NotificationService notificationService,
                               UserRepository userRepository) {
        this.dashboardService = dashboardService;
        this.memberService = memberService;
        this.trainerService = trainerService;
        this.membershipService = membershipService;
        this.attendanceService = attendanceService;
        this.paymentService = paymentService;
        this.workoutService = workoutService;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String adminDashboard(Authentication authentication, Model model) {
        DashboardStatsDto stats = dashboardService.getDashboardStats();
        model.addAttribute("stats", stats);
        model.addAttribute("activeNav", "dashboard");
        model.addAttribute("username", authentication != null ? authentication.getName() : "Admin");
        model.addAttribute("plans", membershipService.getActivePlans());
        model.addAttribute("trainers", trainerService.getActiveTrainers());
        model.addAttribute("notifications", notificationService.getRecentNotifications());
        return "dashboard/admin-dashboard";
    }

    @GetMapping("/trainer/dashboard")
    public String trainerDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        Trainer trainer;
        try {
            trainer = trainerService.getTrainerByUsername(username);
        } catch (Exception e) {
            List<Trainer> allTrainers = trainerService.getAllTrainers();
            trainer = allTrainers.isEmpty() ? null : allTrainers.get(0);
        }

        model.addAttribute("trainer", trainer);
        if (trainer != null) {
            List<MemberWorkoutAssignment> assignments = workoutService.getTrainerAssignments(trainer.getId());
            model.addAttribute("assignedClients", assignments);
            model.addAttribute("clientCount", assignments.size());
        } else {
            model.addAttribute("assignedClients", List.of());
            model.addAttribute("clientCount", 0);
        }

        model.addAttribute("workoutPlans", workoutService.getActivePlans());
        model.addAttribute("activeNav", "trainer-dashboard");
        model.addAttribute("username", username);
        return "dashboard/trainer-dashboard";
    }

    @GetMapping("/member/dashboard")
    public String memberDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        Member member;
        try {
            member = memberService.getMemberByUsername(username);
        } catch (Exception e) {
            List<Member> allMembers = memberService.getAllMembers();
            member = allMembers.isEmpty() ? null : allMembers.get(0);
        }

        model.addAttribute("member", member);
        if (member != null) {
            Membership activeMembership = membershipService.getActiveMembership(member.getId());
            model.addAttribute("activeMembership", activeMembership);
            model.addAttribute("memberships", membershipService.getMemberMemberships(member.getId()));
            model.addAttribute("attendances", attendanceService.getMemberAttendance(member.getId()));
            model.addAttribute("payments", paymentService.getMemberPayments(member.getId()));
            model.addAttribute("activeWorkout", workoutService.getActiveAssignmentForMember(member.getId()));
        }

        model.addAttribute("activeNav", "member-dashboard");
        model.addAttribute("username", username);
        return "dashboard/member-dashboard";
    }
}
