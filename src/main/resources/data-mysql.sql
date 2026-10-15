-- =====================================================================
-- IRONCORE — Smart Gym Management System
-- Comprehensive Seed Data
-- Passwords are encrypted with BCrypt ($2a$10$w8...)
-- Default passwords:
-- admin -> admin123
-- manager -> manager123
-- trainer_marcus, trainer_elena -> trainer123
-- member_alex, member_sophia -> member123
-- =====================================================================

-- BCrypt hash for passwords:
-- admin123: $2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StGQc3KwXaHG.
-- manager123: $2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StGQc3KwXaHG.
-- trainer123: $2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StGQc3KwXaHG.
-- member123: $2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StGQc3KwXaHG.

-- 1. Roles
INSERT IGNORE INTO roles (id, name) VALUES 
(1, 'ROLE_ADMIN'),
(2, 'ROLE_MANAGER'),
(3, 'ROLE_TRAINER'),
(4, 'ROLE_MEMBER');

-- 2. Users
INSERT IGNORE INTO users (id, username, email, password_hash, full_name, role, is_active) VALUES
(1, 'admin', 'admin@ironcore.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StGQc3KwXaHG.', 'System Administrator', 'ROLE_ADMIN', TRUE),
(2, 'manager', 'manager@ironcore.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StGQc3KwXaHG.', 'Operations Manager', 'ROLE_MANAGER', TRUE),
(3, 'trainer_marcus', 'marcus.vance@ironcore.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StGQc3KwXaHG.', 'Marcus Vance', 'ROLE_TRAINER', TRUE),
(4, 'trainer_elena', 'elena.rostova@ironcore.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StGQc3KwXaHG.', 'Elena Rostova', 'ROLE_TRAINER', TRUE),
(5, 'member_alex', 'alex.mercer@gmail.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StGQc3KwXaHG.', 'Alex Mercer', 'ROLE_MEMBER', TRUE),
(6, 'member_sophia', 'sophia.chen@gmail.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StGQc3KwXaHG.', 'Sophia Chen', 'ROLE_MEMBER', TRUE),
(7, 'member_liam', 'liam.g@gmail.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StGQc3KwXaHG.', 'Liam Gallagher', 'ROLE_MEMBER', TRUE),
(8, 'member_maya', 'maya.patel@gmail.com', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.StGQc3KwXaHG.', 'Maya Patel', 'ROLE_MEMBER', TRUE);

-- 3. Membership Plans
INSERT IGNORE INTO membership_plans (id, plan_code, name, description, duration_days, price, features, is_active) VALUES
(1, 'PLAN-BRONZE-M', 'Iron Bronze Monthly', 'Full gym floor access with standard locker access.', 30, 1999.00, 'Gym Floor Access,Standard Locker,Water Station,Off-Peak Entry', TRUE),
(2, 'PLAN-SILVER-Q', 'Silver Momentum Quarterly', 'Peak hours access, spa sauna, and free body composition scan.', 90, 4999.00, 'All-Hours Access,Sauna & Steam,1 InBody Scan/Mo,Locker Included', TRUE),
(3, 'PLAN-GOLD-H', 'Gold Apex Semi-Annual', 'High-performance access with unlimited group classes and guest pass.', 180, 8999.00, '24/7 Access,All Group HIIT & Yoga,Monthly Diet Consultation,2 Guest Passes/Mo', TRUE),
(4, 'PLAN-PLAT-A', 'Platinum Titan Annual', 'Elite all-inclusive membership with dedicated locker and recovery suite.', 365, 15999.00, 'All Gym Locations,Dedicated VIP Locker,Cold Plunge & Cryo,Quarterly PT Sessions,App Priority Booking', TRUE),
(5, 'PLAN-VIP-PT', 'VIP Elite Personal Training Add-on', 'Exclusive 1-on-1 personalized biomechanical training and nutritional coaching.', 90, 24999.00, '36 Private Coaching Sessions,Custom Macros & Nutrition,Weekly Biometric Tracking,Private Suite Access', TRUE);

-- 4. Trainers
INSERT IGNORE INTO trainers (id, trainer_code, user_id, first_name, last_name, email, phone, specialization, qualifications, experience_years, hourly_rate, status, bio) VALUES
(1, 'IC-T-2001', 3, 'Marcus', 'Vance', 'marcus.vance@ironcore.com', '+91 98765 43210', 'Olympic Weightlifting & Strength', 'CSCS, USAW Level 2, ExSci B.S.', 8, 1500.00, 'ACTIVE', 'Former collegiate powerlifting coach specializing in barbell biomechanics, neuromuscular hypertrophy, and injury-free progressive overload.'),
(2, 'IC-T-2002', 4, 'Elena', 'Rostova', 'elena.rostova@ironcore.com', '+91 98765 43211', 'HIIT, Mobility & Athletic Conditioning', 'NASM-CPT, Precision Nutrition L1', 6, 1200.00, 'ACTIVE', 'Passionate functional fitness educator focused on metabolic conditioning, kinetic mobility, and sustainable lifestyle transformation.'),
(3, 'IC-T-2003', NULL, 'David', 'Kalu', 'david.kalu@ironcore.com', '+91 98765 43212', 'Bodybuilding & Hypertrophy', 'ISSA Master Trainer, CPR/AED', 10, 1800.00, 'ACTIVE', 'Veteran competitive physique coach with over a decade of experience in stage prep, symmetry sculpting, and advanced nutrient timing.');

-- 5. Members
INSERT IGNORE INTO members (id, member_code, user_id, first_name, last_name, email, phone, date_of_birth, gender, address, emergency_contact_name, emergency_contact_phone, status, joined_date) VALUES
(1, 'IC-M-1001', 5, 'Alex', 'Mercer', 'alex.mercer@gmail.com', '+91 91234 56780', '1996-05-14', 'MALE', 'Apt 402, Highrise Heights, Bandra West', 'Sarah Mercer', '+91 91234 00001', 'ACTIVE', '2026-01-10'),
(2, 'IC-M-1002', 6, 'Sophia', 'Chen', 'sophia.chen@gmail.com', '+91 91234 56781', '1998-11-22', 'FEMALE', 'Villa 12, Palm Grove, Powai', 'Ken Chen', '+91 91234 00002', 'ACTIVE', '2026-02-01'),
(3, 'IC-M-1003', 7, 'Liam', 'Gallagher', 'liam.g@gmail.com', '+91 91234 56782', '1992-08-30', 'MALE', 'Flat 8B, Silver Arch, Andheri East', 'Noel Gallagher', '+91 91234 00003', 'ACTIVE', '2026-02-15'),
(4, 'IC-M-1004', 8, 'Maya', 'Patel', 'maya.patel@gmail.com', '+91 91234 56783', '2000-03-18', 'FEMALE', 'Greenview Residency, Juhu', 'Rajesh Patel', '+91 91234 00004', 'ACTIVE', '2026-03-01'),
(5, 'IC-M-1005', NULL, 'Jordan', 'Hayes', 'jordan.hayes@outlook.com', '+91 91234 56784', '1994-07-09', 'MALE', 'Sea Breeze Towers, Worli', 'Rachel Hayes', '+91 91234 00005', 'ACTIVE', '2026-01-20'),
(6, 'IC-M-1006', NULL, 'Emily', 'Thorne', 'emily.thorne@yahoo.com', '+91 91234 56785', '1997-12-05', 'FEMALE', 'Khar West, 14th Road', 'Daniel Thorne', '+91 91234 00006', 'INACTIVE', '2025-09-01');

-- 6. Memberships
INSERT IGNORE INTO memberships (id, member_id, plan_id, start_date, end_date, status, auto_renew) VALUES
(1, 1, 4, '2026-01-10', '2027-01-10', 'ACTIVE', TRUE),
(2, 2, 3, '2026-02-01', '2026-08-01', 'ACTIVE', FALSE),
(3, 3, 2, '2026-02-15', '2026-05-15', 'ACTIVE', TRUE),
(4, 4, 1, '2026-03-01', '2026-03-31', 'ACTIVE', FALSE),
(5, 5, 1, '2026-01-20', '2026-02-19', 'EXPIRED', FALSE),
(6, 6, 2, '2025-09-01', '2025-12-01', 'EXPIRED', FALSE);

-- 7. Payments
INSERT IGNORE INTO payments (id, receipt_no, member_id, membership_id, amount, payment_method, status, transaction_id, payment_date, notes) VALUES
(1, 'REC-2026-0001', 1, 1, 15999.00, 'UPI', 'PAID', 'UPI/20260110/9871234', '2026-01-10 10:15:00', 'Annual Titanium Membership'),
(2, 'REC-2026-0002', 2, 2, 8999.00, 'CARD', 'PAID', 'TXN_CARD_774912', '2026-02-01 11:30:00', 'Gold Apex Membership'),
(3, 'REC-2026-0003', 3, 3, 4999.00, 'UPI', 'PAID', 'UPI/20260215/1249812', '2026-02-15 17:45:00', 'Silver Quarterly Membership'),
(4, 'REC-2026-0004', 4, 4, 1999.00, 'CASH', 'PAID', 'CASH_REC_0004', '2026-03-01 09:00:00', 'Bronze Monthly Membership'),
(5, 'REC-2026-0005', 1, NULL, 5000.00, 'UPI', 'PAID', 'UPI/20260220/3819234', '2026-02-20 18:00:00', 'Personal Training Session Pack');

-- 8. Exercises
INSERT IGNORE INTO exercises (id, name, muscle_group, equipment_needed, instructions) VALUES
(1, 'Barbell Back Squat', 'LEGS', 'Barbell & Squat Rack', 'Position barbell across upper traps. Brace core, break at hips and knees, descend below parallel and drive through midfoot.'),
(2, 'Flat Barbell Bench Press', 'CHEST', 'Barbell & Bench', 'Grip bar slightly wider than shoulder width. Retract scapula, lower bar to sternum, press explosively locking out elbows.'),
(3, 'Conventional Deadlift', 'BACK', 'Barbell & Bumper Plates', 'Stand with feet hip-width. Hinge at hips, grip bar firmly, engage lats, drive floor away keeping bar close to shins.'),
(4, 'Standing Overhead Press', 'SHOULDERS', 'Barbell', 'Rack bar on anterior delts. Squeeze glutes and core, press vertically overhead into locked position with head passing through.'),
(5, 'Dumbbell Romanian Deadlift', 'LEGS', 'Dumbbells', 'Hold dumbbells in front of thighs. Soft knees, push hips backward until deep hamstring stretch, thrust hips forward to lock out.'),
(6, 'Incline Dumbbell Chest Press', 'CHEST', 'Dumbbells & Incline Bench', 'Bench at 30-degree incline. Lower dumbbells to chest level, flare elbows 45 degrees, press upward in converging arc.'),
(7, 'Barbell Bent Over Row', 'BACK', 'Barbell', 'Torso at 45 degrees. Pull bar into lower abdomen driving elbows back, squeeze scapulae together at peak contraction.'),
(8, 'EZ-Bar Bicep Curl', 'ARMS', 'EZ-Bar', 'Strict elbow tuck at torso. Curl bar upward contracting biceps, controlled 3-second eccentric lower.'),
(9, 'Tricep Cable Pushdown', 'ARMS', 'Cable Machine & V-Bar', 'Elbows pinned to sides. Push downward fully extending triceps, pause at lockout.'),
(10, 'Hanging Leg Raise', 'CORE', 'Pull-Up Bar', 'Hang from bar with active shoulders. Raise toes toward bar using lower abdominal tension without swinging.');

-- 9. Workout Plans
INSERT IGNORE INTO workout_plans (id, name, description, difficulty_level, goal, duration_weeks, trainer_id, is_active) VALUES
(1, 'Hypertrophy Titan (Push/Pull/Legs)', 'A scientific 6-day split designed for maximal muscle hypertrophy and structural symmetry.', 'INTERMEDIATE', 'HYPERTROPHY', 8, 1, TRUE),
(2, 'Strength Apex 5x5', 'High-intensity compound lift linear progression program for raw strength and power.', 'ADVANCED', 'STRENGTH', 12, 1, TRUE),
(3, 'Athletic Shred & Kinetic Conditioning', 'Dynamic functional circuit training for metabolic fat loss, core stability, and cardio endurance.', 'BEGINNER', 'FAT_LOSS', 6, 2, TRUE);

-- 10. Workout Plan Exercises
INSERT IGNORE INTO workout_plan_exercises (id, workout_plan_id, exercise_id, day_of_week, sets, reps, rest_seconds, order_index) VALUES
(1, 1, 2, 'MONDAY', 4, 8, 90, 1),
(2, 1, 6, 'MONDAY', 3, 10, 60, 2),
(3, 1, 4, 'MONDAY', 3, 8, 90, 3),
(4, 1, 9, 'MONDAY', 4, 12, 45, 4),
(5, 1, 3, 'TUESDAY', 4, 6, 120, 1),
(6, 1, 7, 'TUESDAY', 4, 8, 90, 2),
(7, 1, 8, 'TUESDAY', 3, 12, 60, 3),
(8, 1, 1, 'WEDNESDAY', 4, 8, 120, 1),
(9, 1, 5, 'WEDNESDAY', 3, 10, 90, 2),
(10, 1, 10, 'WEDNESDAY', 3, 15, 45, 3);

-- 11. Member Workout Assignments
INSERT IGNORE INTO member_workout_assignments (id, member_id, workout_plan_id, trainer_id, start_date, end_date, status, notes) VALUES
(1, 1, 1, 1, '2026-01-15', '2026-03-15', 'ACTIVE', 'Focusing on upper chest volume and hamstring depth.'),
(2, 2, 3, 2, '2026-02-05', '2026-03-19', 'ACTIVE', 'Progressing well on metabolic conditioning and core posture.');

-- 12. Attendance Records
INSERT IGNORE INTO attendance (id, member_id, attendance_date, check_in_time, check_out_time, notes) VALUES
(1, 1, CURRENT_DATE(), CURRENT_TIMESTAMP(), NULL, 'Morning Chest & Triceps workout'),
(2, 2, CURRENT_DATE(), CURRENT_TIMESTAMP(), NULL, 'Functional Conditioning & Cardio'),
(3, 3, CURRENT_DATE(), CURRENT_TIMESTAMP(), NULL, 'Leg Day Session');

-- 13. Notifications
INSERT IGNORE INTO notifications (id, user_id, title, message, type, is_read) VALUES
(1, 1, 'New Member Registration', 'Member Alex Mercer (IC-M-1001) registered successfully.', 'WELCOME', FALSE),
(2, 1, 'Payment Recorded', 'Payment of ₹15,999 received from Alex Mercer via UPI.', 'PAYMENT_DUE', FALSE),
(3, 5, 'Workout Plan Assigned', 'Trainer Marcus Vance assigned you the Hypertrophy Titan plan.', 'GENERAL', FALSE),
(4, 1, 'Membership Expiry Alert', 'Jordan Hayes (IC-M-1005) membership has expired.', 'MEMBERSHIP_EXPIRY', FALSE);

-- 14. Audit Logs
INSERT IGNORE INTO audit_logs (id, action, entity_name, entity_id, performed_by, details) VALUES
(1, 'USER_LOGIN', 'User', '1', 'admin', 'Admin logged into system from web console'),
(2, 'CREATE_MEMBER', 'Member', 'IC-M-1001', 'admin', 'Registered member Alex Mercer'),
(3, 'PROCESS_PAYMENT', 'Payment', 'REC-2026-0001', 'admin', 'Recorded payment of ₹15,999.00');
