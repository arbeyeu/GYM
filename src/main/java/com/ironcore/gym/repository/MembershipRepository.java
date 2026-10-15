package com.ironcore.gym.repository;

import com.ironcore.gym.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    List<Membership> findByMemberIdOrderByCreatedAtDesc(Long memberId);
    Optional<Membership> findFirstByMemberIdAndStatusOrderByEndDateDesc(Long memberId, String status);
    Optional<Membership> findFirstByMemberMemberCodeAndStatusOrderByEndDateDesc(String memberCode, String status);

    @Query("SELECT COUNT(m) FROM Membership m WHERE m.status = 'ACTIVE' AND m.endDate BETWEEN :today AND :windowEnd")
    long countExpiringWithin(@Param("today") LocalDate today, @Param("windowEnd") LocalDate windowEnd);

    @Query("SELECT m FROM Membership m WHERE m.status = 'ACTIVE' AND m.endDate BETWEEN :today AND :windowEnd ORDER BY m.endDate ASC")
    List<Membership> findExpiringWithin(@Param("today") LocalDate today, @Param("windowEnd") LocalDate windowEnd);

    @Query("SELECT m FROM Membership m WHERE m.status = 'ACTIVE' AND m.endDate < :today")
    List<Membership> findExpiredMemberships(@Param("today") LocalDate today);
}
