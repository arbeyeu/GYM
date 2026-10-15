package com.ironcore.gym.service;

import com.ironcore.gym.entity.Notification;
import com.ironcore.gym.entity.User;
import com.ironcore.gym.repository.NotificationRepository;
import com.ironcore.gym.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getRecentNotifications() {
        return notificationRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    public void createNotification(User user, String title, String message, String type) {
        if (user != null) {
            Notification notification = new Notification(user, title, message, type);
            notificationRepository.save(notification);
            dispatchExternalNotification(user, title, message);
        }
    }

    public void createSystemNotification(String title, String message) {
        userRepository.findByUsername("admin").ifPresent(admin -> {
            Notification notification = new Notification(admin, title, message, "GENERAL");
            notificationRepository.save(notification);
        });
    }

    /**
     * Extensible hook for Email / WhatsApp / SMS gateway integrations
     */
    private void dispatchExternalNotification(User user, String title, String message) {
        // Plug in SendGrid, Twilio, or WhatsApp Cloud API here
        // e.g. emailGateway.send(user.getEmail(), title, message);
    }
}
