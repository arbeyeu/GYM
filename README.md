# IRONCORE — Smart Gym Management System

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.3-green.svg)](https://spring.io/projects/spring-security)
[![Hibernate](https://img.shields.io/badge/ORM-Hibernate%20%2F%20JPA-blue.svg)](https://hibernate.org/)
[![Database](https://img.shields.io/badge/Database-MySQL%20%7C%20H2-00758F.svg)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-purple.svg)](LICENSE)

> **IRONCORE** is a complete, production-grade Gym & Fitness Management platform engineered with Java Spring Boot and MySQL. It pairs **Apple's typographic elegance** with **Linear's dense, keyboard-driven productivity** and the intense atmosphere of elite athletic performance centers.

---

## 1. Product Vision & Highlights

IRONCORE was crafted from the ground up to transcend generic student prototypes into an **award-winning, enterprise-ready SaaS solution** suitable for engineering college projects, production gym operations, and software portfolio showcases.

- **Dark Obsidian & Electric Lime Palette:** High-contrast dark UI (`#080B10` Obsidian, `#111622` Card, `#CCFF00` Electric Lime) designed for visual impact.
- **Granular 4-Tier RBAC:** Strict authorization across `ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_TRAINER`, and `ROLE_MEMBER`.
- **Zero-Friction Plug & Play:** Runs out of the box with embedded H2 (MySQL Mode) for instant evaluation, with one-flag activation for production MySQL 8.x.
- **Interactive Turnstile Attendance:** Barcode / ID scan check-in terminal with automatic duplicate check prevention.
- **Automated Billing & Tax Invoices:** Cash, UPI, Card transaction recording with printable PDF/paper receipts.
- **Biomechanical Workout Architect:** Day-by-day split planner with exercise movement library, target muscle grouping, sets, reps, and rest timers.
- **Live Business Intelligence:** Real-time Chart.js revenue telemetry, member acquisition trends, and CSV data exports.

---

## 2. Default Access Credentials

For seamless evaluation, IRONCORE initializes sample accounts across all roles:

| Role | Username | Password | Access Scope |
| :--- | :--- | :--- | :--- |
| **System Administrator** | `admin` | `admin123` | Full system access, staff, finances, plans, reports |
| **Gym Manager** | `manager` | `manager123` | Member roster, trainers, attendance, payments |
| **Certified Coach** | `trainer_marcus` | `trainer123` | Assigned athletes, workout splits, exercise vault |
| **Athlete / Member** | `member_alex` | `member123` | Digital pass, active workout plan, visit logs, invoices |

> *Tip: The login screen includes a **1-Click Demo Account Autofill** panel for instant evaluator access.*

---

## 3. Technology Stack

- **Backend:** Java 17, Spring Boot 3.3.4
- **Security:** Spring Security 6 with BCrypt password hashing, session management, and CSRF protection
- **Data Persistence:** Spring Data JPA, Hibernate ORM
- **Database:** MySQL 8.x (Production) / H2 in MySQL compatibility mode (Instant Dev Evaluation)
- **Frontend:** HTML5, CSS3 Custom Obsidian Design System, Thymeleaf, Bootstrap 5 grid/modal helpers
- **Icons & Visuals:** Lucide Icons
- **Analytics:** Chart.js with dark-mode gradient fills

---

## 4. System Architecture & Layers

```
IRONCORE Application Layers
├── Controller Layer      -> Handles HTTP MVC views and RESTful JSON endpoints
├── Service Layer         -> Core business workflows, transaction demarcation, validations
├── Repository Layer      -> Spring Data JPA query interfaces with custom JPQL
├── Entity Layer          -> 14 normalized relational entities with audit timestamps
├── DTO Layer             -> Validated request / response data transfer objects
├── Security Layer        -> Multi-role FilterChain, UserDetailsService, redirect handler
└── Configuration Layer   -> DataInitializer seed engine, multipart & datasource properties
```

---

## 5. Normalized Relational Database Schema

IRONCORE implements a 3NF normalized relational schema:

1. **`users`**: System credentials, password hash, role, activation state.
2. **`roles`**: Authority definitions (`ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_TRAINER`, `ROLE_MEMBER`).
3. **`members`**: Unique member code (`IC-M-XXXX`), contact info, emergency contact, joined date.
4. **`trainers`**: Trainer code (`IC-T-XXXX`), certifications, specialization, hourly rate, bio.
5. **`membership_plans`**: Subscription plans (Monthly, Quarterly, Semi-Annual, Annual, VIP PT).
6. **`memberships`**: Membership contracts, start/end dates, expiry state, auto-renew flag.
7. **`attendance`**: Turnstile logs, check-in timestamp, check-out timestamp, duplicate check guard.
8. **`payments`**: Payment ledger, receipt numbers (`REC-YYYY-XXXX`), UPI/Card/Cash channels, amounts.
9. **`workout_plans`**: Training protocols, difficulty level, goal, duration in weeks.
10. **`exercises`**: Movements classified by muscle group (Chest, Back, Legs, Shoulders, Core, Arms).
11. **`workout_plan_exercises`**: Junction table mapping plans to exercises with sets, reps, rest seconds.
12. **`member_workout_assignments`**: Active athlete training assignments with coach notes.
13. **`notifications`**: System event streams, membership expiry alerts, payment confirmations.
14. **`audit_logs`**: System audit trail tracking all administrative operations.

---

## 6. Getting Started & Local Execution

### Prerequisites
- Windows 10/11, macOS, or Linux.
- The project includes bundled portable JDK 17 & Maven in `.tools/` for zero-install execution on Windows.

### Option A: 1-Click Launch (Recommended)
On Windows PowerShell:
```powershell
.\run.ps1
```
This automatically sets `JAVA_HOME` and starts the application at `http://localhost:8080`.

### Option B: Maven CLI
```bash
./mvnw.cmd spring-boot:run
```
*(On Linux/macOS, run `mvn spring-boot:run`)*

### Option C: Running with Production MySQL
1. Ensure MySQL is running on `localhost:3306`.
2. Create database:
   ```sql
   CREATE DATABASE ironcore_gym CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
3. Run with the MySQL profile:
   ```powershell
   .\run.ps1 --spring.profiles.active=mysql
   ```

---

## 7. REST API Endpoints

| Method | Endpoint | Description | Access |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/dashboard/stats` | Live KPI counters and chart series data | Authenticated |
| `GET` | `/api/members/search?q={query}` | Search members by name, code, phone, or email | Staff |
| `GET` | `/api/members/{code}` | Retrieve detailed member profile by ID code | Staff |
| `GET` | `/api/attendance/today` | Real-time list of today's attendance sessions | Staff |
| `POST` | `/api/attendance/checkin` | Check in member by ID code or email | Staff |
| `POST` | `/api/attendance/checkout/{id}` | Check out member from active session | Staff |
| `POST` | `/api/attendance/toggle/{code}` | 1-Tap turnstile toggle (check-in or check-out) | Staff |
| `POST` | `/api/payments/record` | Record financial transaction and issue receipt | Admin/Staff |
| `GET` | `/api/payments/receipt/{receiptNo}` | Retrieve invoice metadata by receipt number | Authenticated |

---

## 8. Verification & Automated Test Suite

IRONCORE includes automated integration and unit test suites:
- `IronCoreGymApplicationTests`: Context loading and startup seed verification.
- `MemberServiceTest`: Registration, unique `IC-M-XXXX` code generation, duplicate email constraints.
- `AttendanceServiceTest`: Turnstile check-in, duplicate active check-in prevention, check-out confirmation.
- `PaymentServiceTest`: Transaction recording, receipt numbering format, amount validations.

Run the test suite:
```powershell
.\build.ps1
```

---

## 9. License & Author
Engineered for high-performance fitness operations and academic demonstration.
Developed under the MIT License.
