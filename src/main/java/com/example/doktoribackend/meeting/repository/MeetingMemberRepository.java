package com.example.doktoribackend.meeting.repository;

import com.example.doktoribackend.meeting.domain.MeetingMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MeetingMemberRepository extends JpaRepository<MeetingMember, Long> {

    @Query("SELECT mm.user.id FROM MeetingMember mm " +
            "WHERE mm.meeting.id = :meetingId " +
            "AND mm.status = 'APPROVED'")
    List<Long> findApprovedMemberUserIds(@Param("meetingId") Long meetingId);
}
