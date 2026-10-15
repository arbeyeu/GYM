package com.ironcore.gym.repository;

import com.ironcore.gym.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByMuscleGroup(String muscleGroup);
    List<Exercise> findByNameContainingIgnoreCase(String name);
}
