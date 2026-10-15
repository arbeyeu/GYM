package com.ironcore.gym.service;

import com.ironcore.gym.entity.Trainer;
import com.ironcore.gym.entity.User;
import com.ironcore.gym.enums.RoleName;
import com.ironcore.gym.exception.BadRequestException;
import com.ironcore.gym.exception.ResourceNotFoundException;
import com.ironcore.gym.repository.TrainerRepository;
import com.ironcore.gym.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainerService {

    private final TrainerRepository trainerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public TrainerService(TrainerRepository trainerRepository,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          AuditLogService auditLogService) {
        this.trainerRepository = trainerRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    public List<Trainer> getAllTrainers() {
        return trainerRepository.findAll();
    }

    public List<Trainer> getActiveTrainers() {
        return trainerRepository.findByStatus("ACTIVE");
    }

    public Trainer getTrainerById(Long id) {
        return trainerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with ID: " + id));
    }

    public Trainer getTrainerByUsername(String username) {
        return trainerRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Trainer not found for user: " + username));
    }

    @Transactional
    public Trainer createTrainer(Trainer trainer, String performedBy) {
        if (trainerRepository.existsByEmail(trainer.getEmail())) {
            throw new BadRequestException("Trainer with email " + trainer.getEmail() + " already exists.");
        }

        trainer.setTrainerCode(generateUniqueTrainerCode());

        // Create user login account for trainer
        String username = "trn_" + trainer.getTrainerCode().toLowerCase().replace("-", "");
        if (!userRepository.existsByUsername(username)) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(trainer.getEmail());
            user.setPasswordHash(passwordEncoder.encode("trainer123"));
            user.setFullName(trainer.getFullName());
            user.setRole(RoleName.ROLE_TRAINER.name());
            user.setActive(true);
            userRepository.save(user);
            trainer.setUser(user);
        }

        Trainer saved = trainerRepository.save(trainer);
        auditLogService.log("CREATE_TRAINER", "Trainer", saved.getTrainerCode(), performedBy,
                "Created trainer " + saved.getFullName());
        return saved;
    }

    @Transactional
    public Trainer updateTrainer(Long id, Trainer updated, String performedBy) {
        Trainer existing = getTrainerById(id);

        if (!existing.getEmail().equalsIgnoreCase(updated.getEmail()) && trainerRepository.existsByEmail(updated.getEmail())) {
            throw new BadRequestException("Email " + updated.getEmail() + " is already registered.");
        }

        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setSpecialization(updated.getSpecialization());
        existing.setQualifications(updated.getQualifications());
        existing.setExperienceYears(updated.getExperienceYears());
        existing.setHourlyRate(updated.getHourlyRate());
        existing.setStatus(updated.getStatus());
        existing.setBio(updated.getBio());
        if (updated.getPhotoUrl() != null && !updated.getPhotoUrl().isEmpty()) {
            existing.setPhotoUrl(updated.getPhotoUrl());
        }

        Trainer saved = trainerRepository.save(existing);
        auditLogService.log("UPDATE_TRAINER", "Trainer", saved.getTrainerCode(), performedBy,
                "Updated trainer details for " + saved.getFullName());
        return saved;
    }

    @Transactional
    public void deleteTrainer(Long id, String performedBy) {
        Trainer trainer = getTrainerById(id);
        trainer.setStatus("INACTIVE");
        trainerRepository.save(trainer);
        auditLogService.log("DEACTIVATE_TRAINER", "Trainer", trainer.getTrainerCode(), performedBy,
                "Deactivated trainer " + trainer.getFullName());
    }

    private synchronized String generateUniqueTrainerCode() {
        long count = trainerRepository.count() + 2001;
        String code = "IC-T-" + count;
        while (trainerRepository.existsByTrainerCode(code)) {
            count++;
            code = "IC-T-" + count;
        }
        return code;
    }
}
