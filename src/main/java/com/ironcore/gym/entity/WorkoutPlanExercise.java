package com.ironcore.gym.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "workout_plan_exercises")
public class WorkoutPlanExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlan workoutPlan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @Column(nullable = false, length = 20)
    private String dayOfWeek; // MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY

    @Column(nullable = false)
    private Integer sets = 3;

    @Column(nullable = false)
    private Integer reps = 12;

    private Integer durationSeconds = 0;

    @Column(nullable = false)
    private Integer restSeconds = 60;

    @Column(nullable = false)
    private Integer orderIndex = 1;

    public WorkoutPlanExercise() {}

    public WorkoutPlanExercise(WorkoutPlan workoutPlan, Exercise exercise, String dayOfWeek, Integer sets, Integer reps, Integer restSeconds, Integer orderIndex) {
        this.workoutPlan = workoutPlan;
        this.exercise = exercise;
        this.dayOfWeek = dayOfWeek;
        this.sets = sets;
        this.reps = reps;
        this.restSeconds = restSeconds;
        this.orderIndex = orderIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WorkoutPlan getWorkoutPlan() {
        return workoutPlan;
    }

    public void setWorkoutPlan(WorkoutPlan workoutPlan) {
        this.workoutPlan = workoutPlan;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
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

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public Integer getRestSeconds() {
        return restSeconds;
    }

    public void setRestSeconds(Integer restSeconds) {
        this.restSeconds = restSeconds;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
