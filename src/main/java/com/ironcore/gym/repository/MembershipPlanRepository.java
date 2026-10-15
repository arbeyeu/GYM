package com.ironcore.gym.repository;

import com.ironcore.gym.entity.MembershipPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, Long> {
    List<MembershipPlan> findByIsActiveTrue();
    Optional<MembershipPlan> findByPlanCode(String planCode);
}
