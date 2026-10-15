package com.ironcore.gym.repository;

import com.ironcore.gym.entity.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    List<WorkoutPlan> findByIsActiveTrue();
    List<WorkoutPlan> findByTrainerId(Long trainerId);
}
