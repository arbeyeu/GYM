package com.ironcore.gym.service;

import com.ironcore.gym.dto.WorkoutPlanDto;
import com.ironcore.gym.entity.*;
import com.ironcore.gym.exception.BadRequestException;
import com.ironcore.gym.exception.ResourceNotFoundException;
import com.ironcore.gym.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class WorkoutService {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutPlanExerciseRepository planExerciseRepository;
    private final MemberWorkoutAssignmentRepository assignmentRepository;
    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public WorkoutService(WorkoutPlanRepository workoutPlanRepository,
                          ExerciseRepository exerciseRepository,
                          WorkoutPlanExerciseRepository planExerciseRepository,
                          MemberWorkoutAssignmentRepository assignmentRepository,
                          MemberRepository memberRepository,
                          TrainerRepository trainerRepository,
                          AuditLogService auditLogService,
                          NotificationService notificationService) {
        this.workoutPlanRepository = workoutPlanRepository;
        this.exerciseRepository = exerciseRepository;
        this.planExerciseRepository = planExerciseRepository;
        this.assignmentRepository = assignmentRepository;
        this.memberRepository = memberRepository;
        this.trainerRepository = trainerRepository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    // Exercises
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll();
    }

    public List<Exercise> getExercisesByMuscleGroup(String group) {
        return exerciseRepository.findByMuscleGroup(group.toUpperCase());
    }

    public Exercise createExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    // Workout Plans
    public List<WorkoutPlan> getAllWorkoutPlans() {
        return workoutPlanRepository.findAll();
    }

    public List<WorkoutPlan> getActivePlans() {
        return workoutPlanRepository.findByIsActiveTrue();
    }

    public WorkoutPlan getPlanById(Long id) {
        return workoutPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout plan not found with ID: " + id));
    }

    @Transactional
    public WorkoutPlan saveWorkoutPlan(WorkoutPlanDto dto, String performedBy) {
        Trainer trainer = null;
        if (dto.getTrainerId() != null) {
            trainer = trainerRepository.findById(dto.getTrainerId()).orElse(null);
        }

        WorkoutPlan plan;
        if (dto.getId() != null) {
            plan = getPlanById(dto.getId());
            planExerciseRepository.deleteByWorkoutPlanId(plan.getId());
        } else {
            plan = new WorkoutPlan();
        }

        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setDifficultyLevel(dto.getDifficultyLevel());
        plan.setGoal(dto.getGoal());
        plan.setDurationWeeks(dto.getDurationWeeks());
        plan.setTrainer(trainer);

        WorkoutPlan savedPlan = workoutPlanRepository.save(plan);

        if (dto.getExercises() != null && !dto.getExercises().isEmpty()) {
            int order = 1;
            for (WorkoutPlanDto.WorkoutExerciseItemDto item : dto.getExercises()) {
                if (item.getExerciseId() != null) {
                    Exercise exercise = exerciseRepository.findById(item.getExerciseId()).orElse(null);
                    if (exercise != null) {
                        WorkoutPlanExercise wpe = new WorkoutPlanExercise();
                        wpe.setWorkoutPlan(savedPlan);
                        wpe.setExercise(exercise);
                        wpe.setDayOfWeek(item.getDayOfWeek() != null ? item.getDayOfWeek().toUpperCase() : "MONDAY");
                        wpe.setSets(item.getSets() != null ? item.getSets() : 3);
                        wpe.setReps(item.getReps() != null ? item.getReps() : 12);
                        wpe.setRestSeconds(item.getRestSeconds() != null ? item.getRestSeconds() : 60);
                        wpe.setOrderIndex(order++);
                        planExerciseRepository.save(wpe);
                    }
                }
            }
        }

        auditLogService.log("SAVE_WORKOUT_PLAN", "WorkoutPlan", String.valueOf(savedPlan.getId()), performedBy,
                "Saved workout plan: " + savedPlan.getName());

        return savedPlan;
    }

    // Member Assignments
    public List<MemberWorkoutAssignment> getMemberAssignments(Long memberId) {
        return assignmentRepository.findByMemberIdOrderByStartDateDesc(memberId);
    }

    public MemberWorkoutAssignment getActiveAssignmentForMember(Long memberId) {
        return assignmentRepository.findFirstByMemberIdAndStatusOrderByStartDateDesc(memberId, "ACTIVE")
                .orElse(null);
    }

    public List<MemberWorkoutAssignment> getTrainerAssignments(Long trainerId) {
        return assignmentRepository.findByTrainerIdOrderByStartDateDesc(trainerId);
    }

    @Transactional
    public MemberWorkoutAssignment assignPlanToMember(Long memberId, Long planId, Long trainerId, String notes, String performedBy) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + memberId));
        WorkoutPlan plan = getPlanById(planId);
        Trainer trainer = (trainerId != null) ? trainerRepository.findById(trainerId).orElse(null) : plan.getTrainer();

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusWeeks(plan.getDurationWeeks());

        MemberWorkoutAssignment assignment = new MemberWorkoutAssignment(member, plan, trainer, start, end, "ACTIVE", notes);
        MemberWorkoutAssignment saved = assignmentRepository.save(assignment);

        auditLogService.log("ASSIGN_WORKOUT", "MemberWorkoutAssignment", String.valueOf(saved.getId()), performedBy,
                "Assigned " + plan.getName() + " to member " + member.getFullName());

        if (member.getUser() != null) {
            notificationService.createNotification(member.getUser(), "New Workout Assigned",
                    "You have been assigned the workout plan: " + plan.getName(), "WORKOUT_ASSIGNED");
        }

        return saved;
    }
}
