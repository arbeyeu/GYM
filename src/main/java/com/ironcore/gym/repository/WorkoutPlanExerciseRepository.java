package com.ironcore.gym.repository;

import com.ironcore.gym.entity.WorkoutPlanExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutPlanExerciseRepository extends JpaRepository<WorkoutPlanExercise, Long> {
    List<WorkoutPlanExercise> findByWorkoutPlanIdOrderByDayOfWeekAscOrderIndexAsc(Long workoutPlanId);
    void deleteByWorkoutPlanId(Long workoutPlanId);
}
