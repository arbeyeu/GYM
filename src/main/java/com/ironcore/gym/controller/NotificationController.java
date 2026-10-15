package com.ironcore.gym.controller;

import com.ironcore.gym.entity.Notification;
import com.ironcore.gym.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public String notificationsView(Model model) {
        List<Notification> list = notificationService.getRecentNotifications();
        model.addAttribute("notifications", list);
        model.addAttribute("activeNav", "notifications");
        return "notifications/index";
    }

    @PostMapping("/read/{id}")
    @ResponseBody
    public String markRead(@PathVariable("id") Long id) {
        notificationService.markAsRead(id);
        return "OK";
    }
}
