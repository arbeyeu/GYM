package com.ironcore.gym.service;

import com.ironcore.gym.dto.AttendanceDto;
import com.ironcore.gym.entity.Attendance;
import com.ironcore.gym.entity.Member;
import com.ironcore.gym.entity.Membership;
import com.ironcore.gym.exception.BadRequestException;
import com.ironcore.gym.exception.ResourceNotFoundException;
import com.ironcore.gym.repository.AttendanceRepository;
import com.ironcore.gym.repository.MemberRepository;
import com.ironcore.gym.repository.MembershipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final AuditLogService auditLogService;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             MemberRepository memberRepository,
                             MembershipRepository membershipRepository,
                             AuditLogService auditLogService) {
        this.attendanceRepository = attendanceRepository;
        this.memberRepository = memberRepository;
        this.membershipRepository = membershipRepository;
        this.auditLogService = auditLogService;
    }

    public List<Attendance> getTodayAttendance() {
        return attendanceRepository.findByAttendanceDateOrderByCheckInTimeDesc(LocalDate.now());
    }

    public List<Attendance> getMemberAttendance(Long memberId) {
        return attendanceRepository.findByMemberIdOrderByAttendanceDateDescCheckInTimeDesc(memberId);
    }

    public long getTodayAttendanceCount() {
        return attendanceRepository.countByAttendanceDate(LocalDate.now());
    }

    @Transactional
    public Attendance checkIn(AttendanceDto dto, String performedBy) {
        String code = dto.getMemberCode().trim();
        Member member = memberRepository.findByMemberCode(code)
                .or(() -> memberRepository.findByEmail(code))
                .orElseThrow(() -> new ResourceNotFoundException("No active member found with ID or Email: " + code));

        if (!"ACTIVE".equalsIgnoreCase(member.getStatus())) {
            throw new BadRequestException("Member account is " + member.getStatus() + ". Check-in denied.");
        }

        // Check for duplicate active check-in today
        LocalDate today = LocalDate.now();
        Optional<Attendance> existingActive = attendanceRepository.findActiveCheckIn(member.getId(), today);
        if (existingActive.isPresent()) {
            throw new BadRequestException("Member " + member.getFullName() + " is already checked in at " +
                    existingActive.get().getCheckInTime().toLocalTime() + ". Please check out first.");
        }

        // Check membership status warning
        Optional<Membership> activeMembership = membershipRepository.findFirstByMemberIdAndStatusOrderByEndDateDesc(member.getId(), "ACTIVE");
        String notePrefix = "";
        if (activeMembership.isEmpty()) {
            notePrefix = "[WARNING: No Active Membership] ";
        }

        Attendance attendance = new Attendance();
        attendance.setMember(member);
        attendance.setAttendanceDate(today);
        attendance.setCheckInTime(LocalDateTime.now());
        attendance.setNotes(notePrefix + (dto.getNotes() != null ? dto.getNotes() : "Standard Check-in"));

        Attendance saved = attendanceRepository.save(attendance);
        auditLogService.log("CHECK_IN", "Attendance", String.valueOf(saved.getId()), performedBy,
                "Checked in member " + member.getFullName() + " (" + member.getMemberCode() + ")");

        return saved;
    }

    @Transactional
    public Attendance checkOut(Long attendanceId, String performedBy) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with ID: " + attendanceId));

        if (attendance.getCheckOutTime() != null) {
            throw new BadRequestException("Member has already checked out at " + attendance.getCheckOutTime());
        }

        attendance.setCheckOutTime(LocalDateTime.now());
        Attendance updated = attendanceRepository.save(attendance);

        auditLogService.log("CHECK_OUT", "Attendance", String.valueOf(updated.getId()), performedBy,
                "Checked out member " + attendance.getMember().getFullName());

        return updated;
    }

    @Transactional
    public Attendance toggleAttendance(String memberCode, String performedBy) {
        Member member = memberRepository.findByMemberCode(memberCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found: " + memberCode));

        LocalDate today = LocalDate.now();
        Optional<Attendance> active = attendanceRepository.findActiveCheckIn(member.getId(), today);
        if (active.isPresent()) {
            return checkOut(active.get().getId(), performedBy);
        } else {
            AttendanceDto dto = new AttendanceDto(member.getMemberCode(), "Quick Tap Check-in");
            return checkIn(dto, performedBy);
        }
    }
}
