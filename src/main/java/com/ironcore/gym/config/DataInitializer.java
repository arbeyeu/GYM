package com.ironcore.gym.config;

import com.ironcore.gym.entity.*;
import com.ironcore.gym.enums.RoleName;
import com.ironcore.gym.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MembershipPlanRepository planRepository;
    private final TrainerRepository trainerRepository;
    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final PaymentRepository paymentRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final WorkoutPlanExerciseRepository planExerciseRepository;
    private final MemberWorkoutAssignmentRepository assignmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           MembershipPlanRepository planRepository,
                           TrainerRepository trainerRepository,
                           MemberRepository memberRepository,
                           MembershipRepository membershipRepository,
                           PaymentRepository paymentRepository,
                           ExerciseRepository exerciseRepository,
                           WorkoutPlanRepository workoutPlanRepository,
                           WorkoutPlanExerciseRepository planExerciseRepository,
                           MemberWorkoutAssignmentRepository assignmentRepository,
                           AttendanceRepository attendanceRepository,
                           NotificationRepository notificationRepository,
                           AuditLogRepository auditLogRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.planRepository = planRepository;
        this.trainerRepository = trainerRepository;
        this.memberRepository = memberRepository;
        this.membershipRepository = membershipRepository;
        this.paymentRepository = paymentRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutPlanRepository = workoutPlanRepository;
        this.planExerciseRepository = planExerciseRepository;
        this.assignmentRepository = assignmentRepository;
        this.attendanceRepository = attendanceRepository;
        this.notificationRepository = notificationRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Initialize Roles if not present
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(RoleName.ROLE_ADMIN.name()));
            roleRepository.save(new Role(RoleName.ROLE_MANAGER.name()));
            roleRepository.save(new Role(RoleName.ROLE_TRAINER.name()));
            roleRepository.save(new Role(RoleName.ROLE_MEMBER.name()));
        }

        // Initialize Users if not present
        if (userRepository.count() == 0) {
            User admin = new User("admin", "admin@ironcore.com",
                    passwordEncoder.encode("admin123"), "System Administrator", RoleName.ROLE_ADMIN.name());
            userRepository.save(admin);

            User manager = new User("manager", "manager@ironcore.com",
                    passwordEncoder.encode("manager123"), "Operations Manager", RoleName.ROLE_MANAGER.name());
            userRepository.save(manager);

            User trainerUser1 = new User("trainer_marcus", "marcus.vance@ironcore.com",
                    passwordEncoder.encode("trainer123"), "Marcus Vance", RoleName.ROLE_TRAINER.name());
            userRepository.save(trainerUser1);

            User trainerUser2 = new User("trainer_elena", "elena.rostova@ironcore.com",
                    passwordEncoder.encode("trainer123"), "Elena Rostova", RoleName.ROLE_TRAINER.name());
            userRepository.save(trainerUser2);

            User memberUser1 = new User("member_alex", "alex.mercer@gmail.com",
                    passwordEncoder.encode("member123"), "Alex Mercer", RoleName.ROLE_MEMBER.name());
            userRepository.save(memberUser1);

            User memberUser2 = new User("member_sophia", "sophia.chen@gmail.com",
                    passwordEncoder.encode("member123"), "Sophia Chen", RoleName.ROLE_MEMBER.name());
            userRepository.save(memberUser2);

            User memberUser3 = new User("member_liam", "liam.g@gmail.com",
                    passwordEncoder.encode("member123"), "Liam Gallagher", RoleName.ROLE_MEMBER.name());
            userRepository.save(memberUser3);

            User memberUser4 = new User("member_maya", "maya.patel@gmail.com",
                    passwordEncoder.encode("member123"), "Maya Patel", RoleName.ROLE_MEMBER.name());
            userRepository.save(memberUser4);
        }

        // Initialize Plans
        if (planRepository.count() == 0) {
            planRepository.save(new MembershipPlan("PLAN-BRONZE-M", "Iron Bronze Monthly",
                    "Full gym floor access with standard locker access.", 30, new BigDecimal("1999.00"),
                    "Gym Floor Access,Standard Locker,Water Station,Off-Peak Entry"));
            planRepository.save(new MembershipPlan("PLAN-SILVER-Q", "Silver Momentum Quarterly",
                    "Peak hours access, spa sauna, and free body composition scan.", 90, new BigDecimal("4999.00"),
                    "All-Hours Access,Sauna & Steam,1 InBody Scan/Mo,Locker Included"));
            planRepository.save(new MembershipPlan("PLAN-GOLD-H", "Gold Apex Semi-Annual",
                    "High-performance access with unlimited group classes and guest pass.", 180, new BigDecimal("8999.00"),
                    "24/7 Access,All Group HIIT & Yoga,Monthly Diet Consultation,2 Guest Passes/Mo"));
            planRepository.save(new MembershipPlan("PLAN-PLAT-A", "Platinum Titan Annual",
                    "Elite all-inclusive membership with dedicated locker and recovery suite.", 365, new BigDecimal("15999.00"),
                    "All Gym Locations,Dedicated VIP Locker,Cold Plunge & Cryo,Quarterly PT Sessions,App Priority Booking"));
            planRepository.save(new MembershipPlan("PLAN-VIP-PT", "VIP Elite Personal Training",
                    "Exclusive 1-on-1 personalized biomechanical training and nutritional coaching.", 90, new BigDecimal("24999.00"),
                    "36 Private Coaching Sessions,Custom Macros & Nutrition,Weekly Biometric Tracking,Private Suite Access"));
        }

        // Initialize Trainers
        Trainer marcus = null;
        Trainer elena = null;
        if (trainerRepository.count() == 0) {
            marcus = new Trainer();
            marcus.setTrainerCode("IC-T-2001");
            marcus.setUser(userRepository.findByUsername("trainer_marcus").orElse(null));
            marcus.setFirstName("Marcus");
            marcus.setLastName("Vance");
            marcus.setEmail("marcus.vance@ironcore.com");
            marcus.setPhone("+91 98765 43210");
            marcus.setSpecialization("Olympic Weightlifting & Strength");
            marcus.setQualifications("CSCS, USAW Level 2, ExSci B.S.");
            marcus.setExperienceYears(8);
            marcus.setHourlyRate(new BigDecimal("1500.00"));
            marcus.setStatus("ACTIVE");
            marcus.setBio("Former collegiate powerlifting coach specializing in barbell biomechanics, neuromuscular hypertrophy, and injury-free progressive overload.");
            trainerRepository.save(marcus);

            elena = new Trainer();
            elena.setTrainerCode("IC-T-2002");
            elena.setUser(userRepository.findByUsername("trainer_elena").orElse(null));
            elena.setFirstName("Elena");
            elena.setLastName("Rostova");
            elena.setEmail("elena.rostova@ironcore.com");
            elena.setPhone("+91 98765 43211");
            elena.setSpecialization("HIIT, Mobility & Athletic Conditioning");
            elena.setQualifications("NASM-CPT, Precision Nutrition L1");
            elena.setExperienceYears(6);
            elena.setHourlyRate(new BigDecimal("1200.00"));
            elena.setStatus("ACTIVE");
            elena.setBio("Passionate functional fitness educator focused on metabolic conditioning, kinetic mobility, and sustainable lifestyle transformation.");
            trainerRepository.save(elena);
        } else {
            marcus = trainerRepository.findAll().get(0);
            elena = (trainerRepository.findAll().size() > 1) ? trainerRepository.findAll().get(1) : marcus;
        }

        // Initialize Exercises
        if (exerciseRepository.count() == 0) {
            exerciseRepository.save(new Exercise("Barbell Back Squat", "LEGS", "Barbell & Squat Rack",
                    "Position barbell across upper traps. Brace core, descend below parallel, drive through midfoot."));
            exerciseRepository.save(new Exercise("Flat Barbell Bench Press", "CHEST", "Barbell & Bench",
                    "Grip bar slightly wider than shoulder width. Retract scapula, lower bar to sternum, press explosively."));
            exerciseRepository.save(new Exercise("Conventional Deadlift", "BACK", "Barbell & Bumper Plates",
                    "Stand with feet hip-width. Hinge at hips, grip bar firmly, engage lats, drive floor away."));
            exerciseRepository.save(new Exercise("Standing Overhead Press", "SHOULDERS", "Barbell",
                    "Rack bar on anterior delts. Squeeze glutes and core, press vertically overhead with head passing through."));
            exerciseRepository.save(new Exercise("Dumbbell Romanian Deadlift", "LEGS", "Dumbbells",
                    "Hold dumbbells in front of thighs. Soft knees, push hips backward until deep hamstring stretch."));
            exerciseRepository.save(new Exercise("Incline Dumbbell Chest Press", "CHEST", "Dumbbells & Incline Bench",
                    "Bench at 30-degree incline. Lower dumbbells to chest level, press upward in converging arc."));
            exerciseRepository.save(new Exercise("Barbell Bent Over Row", "BACK", "Barbell",
                    "Torso at 45 degrees. Pull bar into lower abdomen driving elbows back, squeeze scapulae."));
            exerciseRepository.save(new Exercise("EZ-Bar Bicep Curl", "ARMS", "EZ-Bar",
                    "Strict elbow tuck at torso. Curl bar upward contracting biceps, controlled 3-second eccentric."));
            exerciseRepository.save(new Exercise("Tricep Cable Pushdown", "ARMS", "Cable Machine",
                    "Elbows pinned to sides. Push downward fully extending triceps, pause at lockout."));
            exerciseRepository.save(new Exercise("Hanging Leg Raise", "CORE", "Pull-Up Bar",
                    "Hang from bar with active shoulders. Raise toes toward bar using lower abdominal tension."));
        }

        // Initialize Members & Memberships
        if (memberRepository.count() == 0) {
            MembershipPlan bronze = planRepository.findByPlanCode("PLAN-BRONZE-M").orElse(null);
            MembershipPlan silver = planRepository.findByPlanCode("PLAN-SILVER-Q").orElse(null);
            MembershipPlan gold = planRepository.findByPlanCode("PLAN-GOLD-H").orElse(null);
            MembershipPlan plat = planRepository.findByPlanCode("PLAN-PLAT-A").orElse(null);

            // Member 1
            Member m1 = new Member();
            m1.setMemberCode("IC-M-1001");
            m1.setUser(userRepository.findByUsername("member_alex").orElse(null));
            m1.setFirstName("Alex");
            m1.setLastName("Mercer");
            m1.setEmail("alex.mercer@gmail.com");
            m1.setPhone("+91 91234 56780");
            m1.setDateOfBirth(LocalDate.of(1996, 5, 14));
            m1.setGender("MALE");
            m1.setAddress("Apt 402, Highrise Heights, Bandra West");
            m1.setEmergencyContactName("Sarah Mercer");
            m1.setEmergencyContactPhone("+91 91234 00001");
            m1.setStatus("ACTIVE");
            m1.setJoinedDate(LocalDate.now().minusMonths(3));
            memberRepository.save(m1);

            if (plat != null) {
                Membership mem1 = new Membership(m1, plat, LocalDate.now().minusMonths(3), LocalDate.now().plusMonths(9), "ACTIVE");
                membershipRepository.save(mem1);
                Payment p1 = new Payment("REC-2026-0001", m1, mem1, plat.getPrice(), "UPI", "PAID", "UPI/2026/01010", "Annual Platinum Membership");
                paymentRepository.save(p1);
            }

            // Member 2
            Member m2 = new Member();
            m2.setMemberCode("IC-M-1002");
            m2.setUser(userRepository.findByUsername("member_sophia").orElse(null));
            m2.setFirstName("Sophia");
            m2.setLastName("Chen");
            m2.setEmail("sophia.chen@gmail.com");
            m2.setPhone("+91 91234 56781");
            m2.setDateOfBirth(LocalDate.of(1998, 11, 22));
            m2.setGender("FEMALE");
            m2.setAddress("Villa 12, Palm Grove, Powai");
            m2.setEmergencyContactName("Ken Chen");
            m2.setEmergencyContactPhone("+91 91234 00002");
            m2.setStatus("ACTIVE");
            m2.setJoinedDate(LocalDate.now().minusMonths(2));
            memberRepository.save(m2);

            if (gold != null) {
                Membership mem2 = new Membership(m2, gold, LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(4), "ACTIVE");
                membershipRepository.save(mem2);
                Payment p2 = new Payment("REC-2026-0002", m2, mem2, gold.getPrice(), "CARD", "PAID", "TXN_CARD_774912", "Semi-Annual Gold Apex");
                paymentRepository.save(p2);
            }

            // Member 3
            Member m3 = new Member();
            m3.setMemberCode("IC-M-1003");
            m3.setUser(userRepository.findByUsername("member_liam").orElse(null));
            m3.setFirstName("Liam");
            m3.setLastName("Gallagher");
            m3.setEmail("liam.g@gmail.com");
            m3.setPhone("+91 91234 56782");
            m3.setDateOfBirth(LocalDate.of(1992, 8, 30));
            m3.setGender("MALE");
            m3.setAddress("Flat 8B, Silver Arch, Andheri East");
            m3.setEmergencyContactName("Noel Gallagher");
            m3.setEmergencyContactPhone("+91 91234 00003");
            m3.setStatus("ACTIVE");
            m3.setJoinedDate(LocalDate.now().minusMonths(1));
            memberRepository.save(m3);

            if (silver != null) {
                Membership mem3 = new Membership(m3, silver, LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(2), "ACTIVE");
                membershipRepository.save(mem3);
                Payment p3 = new Payment("REC-2026-0003", m3, mem3, silver.getPrice(), "UPI", "PAID", "UPI/2026/0215", "Quarterly Silver Momentum");
                paymentRepository.save(p3);
            }

            // Member 4
            Member m4 = new Member();
            m4.setMemberCode("IC-M-1004");
            m4.setUser(userRepository.findByUsername("member_maya").orElse(null));
            m4.setFirstName("Maya");
            m4.setLastName("Patel");
            m4.setEmail("maya.patel@gmail.com");
            m4.setPhone("+91 91234 56783");
            m4.setDateOfBirth(LocalDate.of(2000, 3, 18));
            m4.setGender("FEMALE");
            m4.setAddress("Greenview Residency, Juhu");
            m4.setEmergencyContactName("Rajesh Patel");
            m4.setEmergencyContactPhone("+91 91234 00004");
            m4.setStatus("ACTIVE");
            m4.setJoinedDate(LocalDate.now());
            memberRepository.save(m4);

            if (bronze != null) {
                Membership mem4 = new Membership(m4, bronze, LocalDate.now(), LocalDate.now().plusDays(30), "ACTIVE");
                membershipRepository.save(mem4);
                Payment p4 = new Payment("REC-2026-0004", m4, mem4, bronze.getPrice(), "CASH", "PAID", "CASH_004", "Monthly Iron Bronze");
                paymentRepository.save(p4);
            }

            // Member 5 (Expiring within 7 days)
            Member m5 = new Member();
            m5.setMemberCode("IC-M-1005");
            m5.setFirstName("Jordan");
            m5.setLastName("Hayes");
            m5.setEmail("jordan.hayes@outlook.com");
            m5.setPhone("+91 91234 56784");
            m5.setDateOfBirth(LocalDate.of(1994, 7, 9));
            m5.setGender("MALE");
            m5.setAddress("Sea Breeze Towers, Worli");
            m5.setStatus("ACTIVE");
            m5.setJoinedDate(LocalDate.now().minusDays(25));
            memberRepository.save(m5);

            if (bronze != null) {
                Membership mem5 = new Membership(m5, bronze, LocalDate.now().minusDays(25), LocalDate.now().plusDays(5), "ACTIVE");
                membershipRepository.save(mem5);
                Payment p5 = new Payment("REC-2026-0005", m5, mem5, bronze.getPrice(), "CARD", "PAID", "TXN_77812", "Monthly Iron Bronze");
                paymentRepository.save(p5);
            }

            // Attendance records for today
            attendanceRepository.save(new Attendance(m1, LocalDate.now(), LocalDateTime.now().minusHours(2), "Morning Strength & Push Workout"));
            attendanceRepository.save(new Attendance(m2, LocalDate.now(), LocalDateTime.now().minusHours(1), "Functional HIIT Session"));
            attendanceRepository.save(new Attendance(m3, LocalDate.now(), LocalDateTime.now().minusMinutes(30), "Heavy Deadlift Day"));

            // Workout Plans & Assignments
            if (workoutPlanRepository.count() == 0 && marcus != null) {
                WorkoutPlan plan1 = new WorkoutPlan("Hypertrophy Titan (Push/Pull/Legs)",
                        "A scientific split designed for maximal muscle hypertrophy and structural symmetry.",
                        "INTERMEDIATE", "HYPERTROPHY", 8, marcus);
                workoutPlanRepository.save(plan1);

                WorkoutPlan plan2 = new WorkoutPlan("Athletic Shred & Kinetic Conditioning",
                        "High intensity circuit training for metabolic conditioning and fat loss.",
                        "BEGINNER", "FAT_LOSS", 6, elena);
                workoutPlanRepository.save(plan2);

                assignmentRepository.save(new MemberWorkoutAssignment(m1, plan1, marcus, LocalDate.now().minusWeeks(2), LocalDate.now().plusWeeks(6), "ACTIVE", "Focusing on upper chest volume."));
                assignmentRepository.save(new MemberWorkoutAssignment(m2, plan2, elena, LocalDate.now().minusWeeks(1), LocalDate.now().plusWeeks(5), "ACTIVE", "Progressing well on kinetic mobility."));
            }

            // Notifications
            User adminUser = userRepository.findByUsername("admin").orElse(null);
            if (adminUser != null) {
                notificationRepository.save(new Notification(adminUser, "System Initialized", "IRONCORE Smart Gym Management platform started successfully.", "GENERAL"));
                notificationRepository.save(new Notification(adminUser, "New Member Joined", "Member Alex Mercer (IC-M-1001) registered with Annual Platinum.", "WELCOME"));
                notificationRepository.save(new Notification(adminUser, "Membership Expiry Alert", "Jordan Hayes (IC-M-1005) membership expires in 5 days.", "MEMBERSHIP_EXPIRY"));
            }

            // Audit Logs
            auditLogRepository.save(new AuditLog("SYSTEM_BOOT", "Application", "1", "SYSTEM", "IronCore Gym platform initialized with default seeds."));
            auditLogRepository.save(new AuditLog("CREATE_MEMBER", "Member", "IC-M-1001", "admin", "Created initial member Alex Mercer"));
        }
    }
}
