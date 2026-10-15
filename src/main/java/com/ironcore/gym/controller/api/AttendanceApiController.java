package com.ironcore.gym.controller.api;

import com.ironcore.gym.dto.ApiResponse;
import com.ironcore.gym.dto.AttendanceDto;
import com.ironcore.gym.entity.Attendance;
import com.ironcore.gym.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceApiController {

    private final AttendanceService attendanceService;

    public AttendanceApiController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<Attendance>>> getTodayAttendance() {
        return ResponseEntity.ok(ApiResponse.ok("Today attendance list", attendanceService.getTodayAttendance()));
    }

    @PostMapping("/checkin")
    public ResponseEntity<ApiResponse<Attendance>> checkIn(@Valid @RequestBody AttendanceDto dto,
                                                           Authentication authentication) {
        String performedBy = authentication != null ? authentication.getName() : "STAFF";
        Attendance attendance = attendanceService.checkIn(dto, performedBy);
        return ResponseEntity.ok(ApiResponse.ok("Check-in successful", attendance));
    }

    @PostMapping("/checkout/{id}")
    public ResponseEntity<ApiResponse<Attendance>> checkOut(@PathVariable("id") Long id,
                                                            Authentication authentication) {
        String performedBy = authentication != null ? authentication.getName() : "STAFF";
        Attendance attendance = attendanceService.checkOut(id, performedBy);
        return ResponseEntity.ok(ApiResponse.ok("Check-out successful", attendance));
    }

    @PostMapping("/toggle/{memberCode}")
    public ResponseEntity<ApiResponse<Attendance>> toggle(@PathVariable("memberCode") String memberCode,
                                                          Authentication authentication) {
        String performedBy = authentication != null ? authentication.getName() : "STAFF";
        Attendance attendance = attendanceService.toggleAttendance(memberCode, performedBy);
        return ResponseEntity.ok(ApiResponse.ok("Attendance recorded", attendance));
    }
}
