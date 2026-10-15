package com.ironcore.gym.service;

import com.ironcore.gym.dto.ReportFilterDto;
import com.ironcore.gym.entity.Attendance;
import com.ironcore.gym.entity.Member;
import com.ironcore.gym.entity.Membership;
import com.ironcore.gym.entity.Payment;
import com.ironcore.gym.repository.AttendanceRepository;
import com.ironcore.gym.repository.MemberRepository;
import com.ironcore.gym.repository.MembershipRepository;
import com.ironcore.gym.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final AttendanceRepository attendanceRepository;
    private final PaymentRepository paymentRepository;

    public ReportService(MemberRepository memberRepository,
                         MembershipRepository membershipRepository,
                         AttendanceRepository attendanceRepository,
                         PaymentRepository paymentRepository) {
        this.memberRepository = memberRepository;
        this.membershipRepository = membershipRepository;
        this.attendanceRepository = attendanceRepository;
        this.paymentRepository = paymentRepository;
    }

    public Map<String, Object> generateReportData(ReportFilterDto filter) {
        Map<String, Object> data = new HashMap<>();
        String type = filter.getReportType();

        if ("DAILY_ATTENDANCE".equalsIgnoreCase(type)) {
            LocalDate date = filter.getStartDate() != null ? filter.getStartDate() : LocalDate.now();
            List<Attendance> list = attendanceRepository.findByAttendanceDateOrderByCheckInTimeDesc(date);
            data.put("title", "Daily Attendance Report (" + date + ")");
            data.put("records", list);
            data.put("count", list.size());
        } else if ("MONTHLY_REVENUE".equalsIgnoreCase(type)) {
            List<Payment> payments = paymentRepository.findAll();
            data.put("title", "Monthly Revenue & Transaction Report");
            data.put("records", payments);
            data.put("totalRevenue", payments.stream()
                    .filter(p -> "PAID".equalsIgnoreCase(p.getStatus()))
                    .map(Payment::getAmount)
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        } else if ("MEMBERSHIP_EXPIRY".equalsIgnoreCase(type)) {
            LocalDate today = LocalDate.now();
            LocalDate window = today.plusDays(30);
            List<Membership> expiring = membershipRepository.findExpiringWithin(today, window);
            data.put("title", "Expiring Memberships Report (Next 30 Days)");
            data.put("records", expiring);
            data.put("count", expiring.size());
        } else {
            // Default to Member Directory / Registrations
            List<Member> members = memberRepository.findAll();
            data.put("title", "Member Directory & Registration Report");
            data.put("records", members);
            data.put("count", members.size());
            data.put("activeCount", memberRepository.countByStatus("ACTIVE"));
            data.put("inactiveCount", memberRepository.countByStatus("INACTIVE"));
        }

        return data;
    }

    public String exportToCsv(ReportFilterDto filter) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        String type = filter.getReportType();

        if ("DAILY_ATTENDANCE".equalsIgnoreCase(type)) {
            pw.println("Member Code,Member Name,Attendance Date,Check In Time,Check Out Time,Notes");
            LocalDate date = filter.getStartDate() != null ? filter.getStartDate() : LocalDate.now();
            List<Attendance> list = attendanceRepository.findByAttendanceDateOrderByCheckInTimeDesc(date);
            for (Attendance a : list) {
                pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        a.getMember().getMemberCode(),
                        a.getMember().getFullName(),
                        a.getAttendanceDate(),
                        a.getCheckInTime(),
                        a.getCheckOutTime() != null ? a.getCheckOutTime().toString() : "STILL_IN",
                        a.getNotes() != null ? a.getNotes().replace("\"", "\"\"") : "");
            }
        } else if ("MONTHLY_REVENUE".equalsIgnoreCase(type)) {
            pw.println("Receipt No,Member Code,Member Name,Amount,Payment Method,Status,Payment Date,Transaction ID");
            List<Payment> list = paymentRepository.findAll();
            for (Payment p : list) {
                pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        p.getReceiptNo(),
                        p.getMember().getMemberCode(),
                        p.getMember().getFullName(),
                        p.getAmount(),
                        p.getPaymentMethod(),
                        p.getStatus(),
                        p.getPaymentDate(),
                        p.getTransactionId());
            }
        } else if ("MEMBERSHIP_EXPIRY".equalsIgnoreCase(type)) {
            pw.println("Member Code,Member Name,Plan Name,Start Date,End Date,Status,Auto Renew");
            LocalDate today = LocalDate.now();
            List<Membership> list = membershipRepository.findExpiringWithin(today, today.plusDays(30));
            for (Membership m : list) {
                pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        m.getMember().getMemberCode(),
                        m.getMember().getFullName(),
                        m.getPlan().getName(),
                        m.getStartDate(),
                        m.getEndDate(),
                        m.getStatus(),
                        m.isAutoRenew());
            }
        } else {
            pw.println("Member Code,First Name,Last Name,Email,Phone,Gender,Status,Joined Date");
            List<Member> list = memberRepository.findAll();
            for (Member m : list) {
                pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                        m.getMemberCode(),
                        m.getFirstName(),
                        m.getLastName(),
                        m.getEmail(),
                        m.getPhone(),
                        m.getGender(),
                        m.getStatus(),
                        m.getJoinedDate());
            }
        }

        pw.flush();
        return sw.toString();
    }
}
