package com.ironcore.gym.service;

import com.ironcore.gym.entity.*;
import com.ironcore.gym.exception.BadRequestException;
import com.ironcore.gym.exception.ResourceNotFoundException;
import com.ironcore.gym.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MembershipService {

    private final MembershipPlanRepository planRepository;
    private final MembershipRepository membershipRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public MembershipService(MembershipPlanRepository planRepository,
                             MembershipRepository membershipRepository,
                             MemberRepository memberRepository,
                             PaymentRepository paymentRepository,
                             AuditLogService auditLogService,
                             NotificationService notificationService) {
        this.planRepository = planRepository;
        this.membershipRepository = membershipRepository;
        this.memberRepository = memberRepository;
        this.paymentRepository = paymentRepository;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    // Plans
    public List<MembershipPlan> getAllPlans() {
        return planRepository.findAll();
    }

    public List<MembershipPlan> getActivePlans() {
        return planRepository.findByIsActiveTrue();
    }

    public MembershipPlan getPlanById(Long id) {
        return planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership Plan not found with ID: " + id));
    }

    @Transactional
    public MembershipPlan createPlan(MembershipPlan plan, String performedBy) {
        if (plan.getPlanCode() == null || plan.getPlanCode().isEmpty()) {
            plan.setPlanCode("PLAN-" + plan.getName().replaceAll("[^a-zA-Z0-9]", "-").toUpperCase() + "-" + System.currentTimeMillis() % 1000);
        }
        MembershipPlan saved = planRepository.save(plan);
        auditLogService.log("CREATE_PLAN", "MembershipPlan", saved.getPlanCode(), performedBy,
                "Created plan " + saved.getName() + " (₹" + saved.getPrice() + ")");
        return saved;
    }

    @Transactional
    public MembershipPlan updatePlan(Long id, MembershipPlan updated, String performedBy) {
        MembershipPlan plan = getPlanById(id);
        plan.setName(updated.getName());
        plan.setDescription(updated.getDescription());
        plan.setDurationDays(updated.getDurationDays());
        plan.setPrice(updated.getPrice());
        plan.setFeatures(updated.getFeatures());
        plan.setActive(updated.isActive());

        MembershipPlan saved = planRepository.save(plan);
        auditLogService.log("UPDATE_PLAN", "MembershipPlan", saved.getPlanCode(), performedBy,
                "Updated plan " + saved.getName());
        return saved;
    }

    // Memberships
    public List<Membership> getMemberMemberships(Long memberId) {
        return membershipRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    public Membership getActiveMembership(Long memberId) {
        return membershipRepository.findFirstByMemberIdAndStatusOrderByEndDateDesc(memberId, "ACTIVE")
                .orElse(null);
    }

    @Transactional
    public Membership purchaseOrRenewMembership(Long memberId, Long planId, String paymentMethod, String performedBy) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + memberId));
        MembershipPlan plan = getPlanById(planId);

        // Check if member already has an active membership; extend if active, or start today
        LocalDate startDate = LocalDate.now();
        Membership currentActive = getActiveMembership(memberId);
        if (currentActive != null && currentActive.getEndDate().isAfter(startDate)) {
            startDate = currentActive.getEndDate().plusDays(1);
        }

        LocalDate endDate = startDate.plusDays(plan.getDurationDays());

        Membership membership = new Membership(member, plan, startDate, endDate, "ACTIVE");
        Membership savedMembership = membershipRepository.save(membership);

        // Record payment
        Payment payment = new Payment();
        payment.setReceiptNo(generateReceiptNumber());
        payment.setMember(member);
        payment.setMembership(savedMembership);
        payment.setAmount(plan.getPrice());
        payment.setPaymentMethod(paymentMethod != null ? paymentMethod : "UPI");
        payment.setStatus("PAID");
        payment.setTransactionId("TXN_" + System.currentTimeMillis());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setNotes("Membership subscription: " + plan.getName());
        paymentRepository.save(payment);

        auditLogService.log("RENEW_MEMBERSHIP", "Membership", String.valueOf(savedMembership.getId()), performedBy,
                "Subscribed member " + member.getFullName() + " to " + plan.getName() + " until " + endDate);
        notificationService.createSystemNotification("Membership Activated",
                member.getFullName() + " activated " + plan.getName() + " valid until " + endDate);

        return savedMembership;
    }

    @Transactional
    public void checkAndUpdateExpiredMemberships() {
        List<Membership> expired = membershipRepository.findExpiredMemberships(LocalDate.now());
        for (Membership m : expired) {
            m.setStatus("EXPIRED");
            membershipRepository.save(m);
            notificationService.createSystemNotification("Membership Expired",
                    "Membership for " + m.getMember().getFullName() + " (" + m.getMember().getMemberCode() + ") has expired.");
        }
    }

    private synchronized String generateReceiptNumber() {
        int year = LocalDate.now().getYear();
        long count = paymentRepository.count() + 1;
        return String.format("REC-%d-%04d", year, count);
    }
}
