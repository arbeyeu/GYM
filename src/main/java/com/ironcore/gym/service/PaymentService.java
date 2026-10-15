package com.ironcore.gym.service;

import com.ironcore.gym.dto.PaymentDto;
import com.ironcore.gym.entity.Member;
import com.ironcore.gym.entity.Membership;
import com.ironcore.gym.entity.Payment;
import com.ironcore.gym.exception.BadRequestException;
import com.ironcore.gym.exception.ResourceNotFoundException;
import com.ironcore.gym.repository.MemberRepository;
import com.ironcore.gym.repository.MembershipRepository;
import com.ironcore.gym.repository.PaymentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final MembershipRepository membershipRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public PaymentService(PaymentRepository paymentRepository,
                          MemberRepository memberRepository,
                          MembershipRepository membershipRepository,
                          AuditLogService auditLogService,
                          NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.memberRepository = memberRepository;
        this.membershipRepository = membershipRepository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Page<Payment> getPayments(String query, String method, String status, Pageable pageable) {
        String cleanQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        String cleanMethod = (method != null && !method.trim().isEmpty() && !method.equalsIgnoreCase("ALL")) ? method.trim() : null;
        String cleanStatus = (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) ? status.trim() : null;
        return paymentRepository.findFiltered(cleanQuery, cleanMethod, cleanStatus, pageable);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + id));
    }

    public Payment getPaymentByReceipt(String receiptNo) {
        return paymentRepository.findByReceiptNo(receiptNo)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with Receipt: " + receiptNo));
    }

    public List<Payment> getMemberPayments(Long memberId) {
        return paymentRepository.findByMemberIdOrderByPaymentDateDesc(memberId);
    }

    @Transactional
    public Payment recordPayment(PaymentDto dto, String performedBy) {
        Member member = memberRepository.findById(dto.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + dto.getMemberId()));

        Membership membership = null;
        if (dto.getMembershipId() != null) {
            membership = membershipRepository.findById(dto.getMembershipId()).orElse(null);
        }

        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Payment amount must be greater than zero.");
        }

        Payment payment = new Payment();
        payment.setReceiptNo(generateReceiptNumber());
        payment.setMember(member);
        payment.setMembership(membership);
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod().toUpperCase());
        payment.setStatus("PAID");
        payment.setTransactionId(dto.getTransactionId() != null && !dto.getTransactionId().trim().isEmpty() ?
                dto.getTransactionId().trim() : "TXN_" + System.currentTimeMillis());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setNotes(dto.getNotes());

        Payment saved = paymentRepository.save(payment);

        auditLogService.log("PROCESS_PAYMENT", "Payment", saved.getReceiptNo(), performedBy,
                "Received payment of ₹" + saved.getAmount() + " via " + saved.getPaymentMethod() + " from " + member.getFullName());
        notificationService.createSystemNotification("Payment Received",
                "Received payment ₹" + saved.getAmount() + " (" + saved.getReceiptNo() + ") from " + member.getFullName());

        return saved;
    }

    public BigDecimal getMonthlyRevenue() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        return paymentRepository.sumRevenueSince(startOfMonth);
    }

    public long getPendingPaymentsCount() {
        return paymentRepository.countPendingPayments();
    }

    public BigDecimal getPendingPaymentsAmount() {
        return paymentRepository.sumPendingPayments();
    }

    private synchronized String generateReceiptNumber() {
        int year = LocalDate.now().getYear();
        long count = paymentRepository.count() + 1;
        return String.format("REC-%d-%04d", year, count);
    }
}
