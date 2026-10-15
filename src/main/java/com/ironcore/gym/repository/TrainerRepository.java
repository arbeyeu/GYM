package com.ironcore.gym.repository;

import com.ironcore.gym.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByTrainerCode(String trainerCode);
    Optional<Trainer> findByUserUsername(String username);
    List<Trainer> findByStatus(String status);
    boolean existsByEmail(String email);
    boolean existsByTrainerCode(String trainerCode);
}
