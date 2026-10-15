package com.ironcore.gym.repository;

import com.ironcore.gym.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByReceiptNo(String receiptNo);

    List<Payment> findByMemberIdOrderByPaymentDateDesc(Long memberId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'PAID' AND p.paymentDate >= :since")
    BigDecimal sumRevenueSince(@Param("since") LocalDateTime since);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = 'PENDING'")
    BigDecimal sumPendingPayments();

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = 'PENDING'")
    long countPendingPayments();

    List<Payment> findTop5ByOrderByPaymentDateDesc();

    @Query("SELECT p FROM Payment p WHERE " +
           "(:method IS NULL OR p.paymentMethod = :method) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:query IS NULL OR " +
           "LOWER(p.receiptNo) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.member.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.member.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.member.memberCode) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Payment> findFiltered(@Param("query") String query, @Param("method") String method, @Param("status") String status, Pageable pageable);

    @Query("SELECT FUNCTION('YEAR', p.paymentDate), FUNCTION('MONTH', p.paymentDate), COALESCE(SUM(p.amount), 0) " +
           "FROM Payment p WHERE p.status = 'PAID' AND p.paymentDate >= :since " +
           "GROUP BY FUNCTION('YEAR', p.paymentDate), FUNCTION('MONTH', p.paymentDate) " +
           "ORDER BY FUNCTION('YEAR', p.paymentDate) ASC, FUNCTION('MONTH', p.paymentDate) ASC")
    List<Object[]> getMonthlyRevenueSince(@Param("since") LocalDateTime since);
}
