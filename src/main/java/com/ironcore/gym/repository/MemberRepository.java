package com.ironcore.gym.repository;

import com.ironcore.gym.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByMemberCode(String memberCode);
    Optional<Member> findByUserUsername(String username);
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByMemberCode(String memberCode);

    long countByStatus(String status);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.joinedDate >= :startDate")
    long countNewRegistrationsSince(@Param("startDate") LocalDate startDate);

    @Query("SELECT m FROM Member m WHERE " +
           "LOWER(m.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.memberCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "m.phone LIKE CONCAT('%', :query, '%')")
    List<Member> searchMembers(@Param("query") String query);

    @Query("SELECT m FROM Member m WHERE " +
           "(:status IS NULL OR m.status = :status) AND " +
           "(:query IS NULL OR " +
           "LOWER(m.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.memberCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(m.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "m.phone LIKE CONCAT('%', :query, '%'))")
    Page<Member> findFiltered(@Param("query") String query, @Param("status") String status, Pageable pageable);

    List<Member> findTop5ByOrderByCreatedAtDesc();
}
