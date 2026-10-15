package com.ironcore.gym.controller;

import com.ironcore.gym.dto.AttendanceDto;
import com.ironcore.gym.entity.Attendance;
import com.ironcore.gym.service.AttendanceService;
import com.ironcore.gym.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final MemberService memberService;

    public AttendanceController(AttendanceService attendanceService, MemberService memberService) {
        this.attendanceService = attendanceService;
        this.memberService = memberService;
    }

    @GetMapping
    public String attendanceDashboard(Model model) {
        List<Attendance> todayList = attendanceService.getTodayAttendance();
        long currentlyIn = todayList.stream().filter(Attendance::isCurrentlyCheckedIn).count();

        model.addAttribute("todayAttendance", todayList);
        model.addAttribute("todayCount", todayList.size());
        model.addAttribute("currentlyIn", currentlyIn);
        model.addAttribute("checkedOutCount", todayList.size() - currentlyIn);
        model.addAttribute("attendanceDto", new AttendanceDto());
        model.addAttribute("activeNav", "attendance");
        return "attendance/index";
    }

    @PostMapping("/checkin")
    public String checkIn(@ModelAttribute("attendanceDto") AttendanceDto dto,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        try {
            String performedBy = authentication != null ? authentication.getName() : "STAFF";
            Attendance saved = attendanceService.checkIn(dto, performedBy);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Check-in verified for " + saved.getMember().getFullName() + " (" + saved.getMember().getMemberCode() + ")");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/attendance";
    }

    @PostMapping("/checkout/{id}")
    public String checkOut(@PathVariable("id") Long id,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            String performedBy = authentication != null ? authentication.getName() : "STAFF";
            Attendance saved = attendanceService.checkOut(id, performedBy);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Check-out confirmed for " + saved.getMember().getFullName() + " at " + saved.getCheckOutTime().toLocalTime());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/attendance";
    }
}
