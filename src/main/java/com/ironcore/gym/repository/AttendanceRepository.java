package com.ironcore.gym.repository;

import com.ironcore.gym.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByAttendanceDateOrderByCheckInTimeDesc(LocalDate date);

    List<Attendance> findByMemberIdOrderByAttendanceDateDescCheckInTimeDesc(Long memberId);

    @Query("SELECT a FROM Attendance a WHERE a.member.id = :memberId AND a.attendanceDate = :date AND a.checkOutTime IS NULL")
    Optional<Attendance> findActiveCheckIn(@Param("memberId") Long memberId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.attendanceDate = :date")
    long countByAttendanceDate(@Param("date") LocalDate date);

    @Query("SELECT a.attendanceDate, COUNT(a) FROM Attendance a WHERE a.attendanceDate >= :startDate GROUP BY a.attendanceDate ORDER BY a.attendanceDate ASC")
    List<Object[]> getDailyAttendanceCountsSince(@Param("startDate") LocalDate startDate);
}
