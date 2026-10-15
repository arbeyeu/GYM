package com.ironcore.gym.service;

import com.ironcore.gym.dto.DashboardStatsDto;
import com.ironcore.gym.repository.AttendanceRepository;
import com.ironcore.gym.repository.MemberRepository;
import com.ironcore.gym.repository.MembershipRepository;
import com.ironcore.gym.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentRepository paymentRepository;

    public DashboardService(MemberRepository memberRepository,
                            MembershipRepository membershipRepository,
                            AttendanceRepository attendanceRepository,
                            PaymentRepository paymentRepository) {
        this.memberRepository = memberRepository;
        this.membershipRepository = membershipRepository;
        this.attendanceRepository = attendanceRepository;
        this.paymentRepository = paymentRepository;
    }

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        // 1. KPI Counts
        stats.setTotalActiveMembers(memberRepository.countByStatus("ACTIVE"));

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        stats.setNewRegistrationsThisMonth(memberRepository.countNewRegistrationsSince(startOfMonth));

        LocalDate today = LocalDate.now();
        stats.setTodayAttendanceCount(attendanceRepository.countByAttendanceDate(today));

        LocalDate expiryWindow = today.plusDays(14);
        stats.setExpiringMembershipsCount(membershipRepository.countExpiringWithin(today, expiryWindow));

        LocalDateTime startOfMonthTime = startOfMonth.atStartOfDay();
        BigDecimal revenue = paymentRepository.sumRevenueSince(startOfMonthTime);
        stats.setMonthlyRevenue(revenue != null ? revenue : BigDecimal.ZERO);

        stats.setPendingPaymentsCount(paymentRepository.countPendingPayments());
        BigDecimal pendingAmt = paymentRepository.sumPendingPayments();
        stats.setPendingPaymentsAmount(pendingAmt != null ? pendingAmt : BigDecimal.ZERO);

        // 2. Member Growth Chart Data (Last 6 months)
        List<String> monthLabels = new ArrayList<>();
        List<Long> memberCounts = new ArrayList<>();
        List<BigDecimal> monthlyRevenues = new ArrayList<>();

        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy");
        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = today.minusMonths(i);
            String label = monthDate.format(monthFormatter);
            monthLabels.add(label);

            LocalDate monthStart = monthDate.withDayOfMonth(1);
            LocalDate monthEnd = monthDate.withDayOfMonth(monthDate.lengthOfMonth());
            long newMems = memberRepository.countNewRegistrationsSince(monthStart);
            memberCounts.add(newMems);

            LocalDateTime mStartTime = monthStart.atStartOfDay();
            BigDecimal mRev = paymentRepository.sumRevenueSince(mStartTime);
            monthlyRevenues.add(mRev != null ? mRev : BigDecimal.ZERO);
        }

        stats.setMemberGrowthLabels(monthLabels);
        stats.setMemberGrowthData(memberCounts);
        stats.setRevenueLabels(monthLabels);
        stats.setRevenueData(monthlyRevenues);

        // 3. Recent Records
        stats.setRecentMembers(memberRepository.findTop5ByOrderByCreatedAtDesc());
        stats.setRecentPayments(paymentRepository.findTop5ByOrderByPaymentDateDesc());
        stats.setRecentAttendance(attendanceRepository.findByAttendanceDateOrderByCheckInTimeDesc(today));

        return stats;
    }
}
