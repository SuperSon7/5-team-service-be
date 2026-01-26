package com.example.doktoribackend.meeting.repository;

import com.example.doktoribackend.meeting.domain.MeetingMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MeetingMemberRepository extends JpaRepository<MeetingMember, Long> {

    // origin 메서드 (다른 팀원이 추가)
    @Query("SELECT mm.user.id FROM MeetingMember mm " +
            "WHERE mm.meeting.id = :meetingId " +
            "AND mm.status = 'APPROVED'")
    List<Long> findApprovedMemberUserIds(@Param("meetingId") Long meetingId);

    // 브루니 메서드 (모임 상세 조회용)
    Optional<MeetingMember> findByMeetingIdAndUserId(Long meetingId, Long userId);

    @Query("SELECT mm FROM MeetingMember mm " +
           "JOIN FETCH mm.user " +
           "WHERE mm.meeting.id = :meetingId " +
           "AND mm.status = 'APPROVED' " +
           "ORDER BY mm.createdAt ASC")
    List<MeetingMember> findApprovedMembersByMeetingIdOrderByCreatedAt(@Param("meetingId") Long meetingId);
}
