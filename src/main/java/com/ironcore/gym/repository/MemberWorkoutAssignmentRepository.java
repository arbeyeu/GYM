package com.ironcore.gym.repository;

import com.ironcore.gym.entity.MemberWorkoutAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberWorkoutAssignmentRepository extends JpaRepository<MemberWorkoutAssignment, Long> {
    List<MemberWorkoutAssignment> findByMemberIdOrderByStartDateDesc(Long memberId);
    List<MemberWorkoutAssignment> findByTrainerIdOrderByStartDateDesc(Long trainerId);
    Optional<MemberWorkoutAssignment> findFirstByMemberIdAndStatusOrderByStartDateDesc(Long memberId, String status);
    Optional<MemberWorkoutAssignment> findFirstByMemberMemberCodeAndStatusOrderByStartDateDesc(String memberCode, String status);
}
