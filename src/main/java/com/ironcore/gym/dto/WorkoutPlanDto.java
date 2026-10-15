package com.ironcore.gym.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class WorkoutPlanDto {
    private Long id;

    @NotBlank(message = "Plan name is required")
    private String name;

    private String description;

    @NotBlank(message = "Difficulty level is required")
    private String difficultyLevel;

    @NotBlank(message = "Fitness goal is required")
    private String goal;

    @NotNull(message = "Duration in weeks is required")
    private Integer durationWeeks = 4;

    private Long trainerId;

    private List<WorkoutExerciseItemDto> exercises = new ArrayList<>();

    public WorkoutPlanDto() {}

    public static class WorkoutExerciseItemDto {
        private Long exerciseId;
        private String dayOfWeek;
        private Integer sets;
        private Integer reps;
        private Integer restSeconds;

        public WorkoutExerciseItemDto() {}

        public Long getExerciseId() {
            return exerciseId;
        }

        public void setExerciseId(Long exerciseId) {
            this.exerciseId = exerciseId;
        }

        public String getDayOfWeek() {
            return dayOfWeek;
        }

        public void setDayOfWeek(String dayOfWeek) {
            this.dayOfWeek = dayOfWeek;
        }

        public Integer getSets() {
            return sets;
        }

        public void setSets(Integer sets) {
            this.sets = sets;
        }

        public Integer getReps() {
            return reps;
        }

        public void setReps(Integer reps) {
            this.reps = reps;
        }

        public Integer getRestSeconds() {
            return restSeconds;
        }

        public void setRestSeconds(Integer restSeconds) {
            this.restSeconds = restSeconds;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Integer getDurationWeeks() {
        return durationWeeks;
    }

    public void setDurationWeeks(Integer durationWeeks) {
        this.durationWeeks = durationWeeks;
    }

    public Long getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(Long trainerId) {
        this.trainerId = trainerId;
    }

    public List<WorkoutExerciseItemDto> getExercises() {
        return exercises;
    }

    public void setExercises(List<WorkoutExerciseItemDto> exercises) {
        this.exercises = exercises;
    }
}
