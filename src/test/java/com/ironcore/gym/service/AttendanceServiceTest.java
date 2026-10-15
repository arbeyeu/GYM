package com.ironcore.gym.service;

import com.ironcore.gym.dto.AttendanceDto;
import com.ironcore.gym.entity.Attendance;
import com.ironcore.gym.entity.Member;
import com.ironcore.gym.exception.BadRequestException;
import com.ironcore.gym.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AttendanceServiceTest {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void testCheckInAndCheckOutWorkflow() {
        Member member = memberRepository.findByMemberCode("IC-M-1004").orElseThrow();

        AttendanceDto dto = new AttendanceDto(member.getMemberCode(), "Test Checkin");
        Attendance checkInRecord = attendanceService.checkIn(dto, "TEST_SUITE");

        assertNotNull(checkInRecord.getId());
        assertNotNull(checkInRecord.getCheckInTime());
        assertNull(checkInRecord.getCheckOutTime());
        assertTrue(checkInRecord.isCurrentlyCheckedIn());

        // Attempting second check-in without check-out must fail (prevent duplicate)
        assertThrows(BadRequestException.class, () -> {
            attendanceService.checkIn(dto, "TEST_SUITE");
        });

        // Perform Check-Out
        Attendance checkOutRecord = attendanceService.checkOut(checkInRecord.getId(), "TEST_SUITE");
        assertNotNull(checkOutRecord.getCheckOutTime());
        assertFalse(checkOutRecord.isCurrentlyCheckedIn());
    }
}
