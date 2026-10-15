package com.ironcore.gym.service;

import com.ironcore.gym.dto.MemberDto;
import com.ironcore.gym.entity.*;
import com.ironcore.gym.enums.RoleName;
import com.ironcore.gym.exception.BadRequestException;
import com.ironcore.gym.exception.ResourceNotFoundException;
import com.ironcore.gym.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final MembershipPlanRepository planRepository;
    private final MembershipRepository membershipRepository;
    private final PaymentRepository paymentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;

    public MemberService(MemberRepository memberRepository,
                         UserRepository userRepository,
                         MembershipPlanRepository planRepository,
                         MembershipRepository membershipRepository,
                         PaymentRepository paymentRepository,
                         PasswordEncoder passwordEncoder,
                         AuditLogService auditLogService,
                         NotificationService notificationService) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.membershipRepository = membershipRepository;
        this.paymentRepository = paymentRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Page<Member> getMembers(String query, String status, Pageable pageable) {
        String cleanQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        String cleanStatus = (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) ? status.trim() : null;
        return memberRepository.findFiltered(cleanQuery, cleanStatus, pageable);
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));
    }

    public Member getMemberByCode(String code) {
        return memberRepository.findByMemberCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with Code: " + code));
    }

    public Member getMemberByUsername(String username) {
        return memberRepository.findByUserUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found for user: " + username));
    }

    @Transactional
    public Member registerMember(MemberDto dto, String performedBy) {
        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("A member with email " + dto.getEmail() + " already exists.");
        }

        Member member = new Member();
        member.setMemberCode(generateUniqueMemberCode());
        member.setFirstName(dto.getFirstName());
        member.setLastName(dto.getLastName());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());
        member.setDateOfBirth(dto.getDateOfBirth());
        member.setGender(dto.getGender());
        member.setAddress(dto.getAddress());
        member.setEmergencyContactName(dto.getEmergencyContactName());
        member.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        member.setPhotoUrl(dto.getPhotoUrl());
        member.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
        member.setJoinedDate(dto.getJoinedDate() != null ? dto.getJoinedDate() : LocalDate.now());

        // Create user login if requested
        if (dto.isCreateLoginUser()) {
            String username = "mem_" + member.getMemberCode().toLowerCase().replace("-", "");
            if (!userRepository.existsByUsername(username)) {
                User user = new User();
                user.setUsername(username);
                user.setEmail(dto.getEmail());
                user.setPasswordHash(passwordEncoder.encode("member123"));
                user.setFullName(member.getFullName());
                user.setRole(RoleName.ROLE_MEMBER.name());
                user.setActive(true);
                userRepository.save(user);
                member.setUser(user);
            }
        }

        Member savedMember = memberRepository.save(member);

        // Optional initial membership plan purchase
        if (dto.getPlanId() != null) {
            MembershipPlan plan = planRepository.findById(dto.getPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Plan not found with ID: " + dto.getPlanId()));

            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusDays(plan.getDurationDays());

            Membership membership = new Membership(savedMember, plan, startDate, endDate, "ACTIVE");
            membershipRepository.save(membership);

            // Record initial payment
            Payment payment = new Payment();
            payment.setReceiptNo(generateReceiptNumber());
            payment.setMember(savedMember);
            payment.setMembership(membership);
            payment.setAmount(plan.getPrice());
            payment.setPaymentMethod(dto.getPaymentMethod() != null ? dto.getPaymentMethod() : "UPI");
            payment.setStatus("PAID");
            payment.setTransactionId("TXN_" + System.currentTimeMillis());
            payment.setPaymentDate(LocalDateTime.now());
            payment.setNotes("Registration initial membership: " + plan.getName());
            paymentRepository.save(payment);
        }

        auditLogService.log("CREATE_MEMBER", "Member", savedMember.getMemberCode(), performedBy,
                "Registered member " + savedMember.getFullName());
        notificationService.createSystemNotification("New Member",
                "Member " + savedMember.getFullName() + " (" + savedMember.getMemberCode() + ") has joined.");

        return savedMember;
    }

    @Transactional
    public Member updateMember(Long id, MemberDto dto, String performedBy) {
        Member member = getMemberById(id);

        if (!member.getEmail().equalsIgnoreCase(dto.getEmail()) && memberRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email " + dto.getEmail() + " is already in use by another member.");
        }

        member.setFirstName(dto.getFirstName());
        member.setLastName(dto.getLastName());
        member.setEmail(dto.getEmail());
        member.setPhone(dto.getPhone());
        member.setDateOfBirth(dto.getDateOfBirth());
        member.setGender(dto.getGender());
        member.setAddress(dto.getAddress());
        member.setEmergencyContactName(dto.getEmergencyContactName());
        member.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        if (dto.getPhotoUrl() != null && !dto.getPhotoUrl().isEmpty()) {
            member.setPhotoUrl(dto.getPhotoUrl());
        }
        if (dto.getStatus() != null) {
            member.setStatus(dto.getStatus());
        }

        Member updated = memberRepository.save(member);
        auditLogService.log("UPDATE_MEMBER", "Member", updated.getMemberCode(), performedBy,
                "Updated member profile for " + updated.getFullName());
        return updated;
    }

    @Transactional
    public void deleteMember(Long id, String performedBy) {
        Member member = getMemberById(id);
        member.setStatus("INACTIVE");
        memberRepository.save(member);
        auditLogService.log("DEACTIVATE_MEMBER", "Member", member.getMemberCode(), performedBy,
                "Deactivated member " + member.getFullName());
    }

    private synchronized String generateUniqueMemberCode() {
        long count = memberRepository.count() + 1001;
        String code = "IC-M-" + count;
        while (memberRepository.existsByMemberCode(code)) {
            count++;
            code = "IC-M-" + count;
        }
        return code;
    }

    private synchronized String generateReceiptNumber() {
        int year = LocalDate.now().getYear();
        long count = paymentRepository.count() + 1;
        return String.format("REC-%d-%04d", year, count);
    }
}
