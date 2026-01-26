package com.example.doktoribackend.meeting.repository;

import com.example.doktoribackend.meeting.domain.MeetingRound;
import com.example.doktoribackend.meeting.domain.MeetingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingRoundRepository extends JpaRepository<MeetingRound, Long> {

    @Query("SELECT mr.id FROM MeetingRound mr " +
            "JOIN mr.meeting m " +
            "WHERE m.status IN :statuses " +
            "AND mr.startAt = :targetTime " +
            "AND mr.meetingLink IS NULL")
    List<Long> findMeetingRoundIdsForZoomLinkCreation(
            @Param("statuses") List<MeetingStatus> statuses,
            @Param("targetTime") LocalDateTime targetTime
    );

    @Query("SELECT mr FROM MeetingRound mr " +
            "JOIN FETCH mr.meeting " +
            "WHERE mr.id IN :ids")
    List<MeetingRound> findByIdsWithMeeting(@Param("ids") List<Long> ids);

    // origin 메서드 (다른 팀원이 추가)
    @Query("SELECT mr FROM MeetingRound mr " +
            "JOIN FETCH mr.meeting m " +
            "WHERE mr.startAt = :targetTime " +
            "AND m.status IN ('RECRUITING', 'FINISHED')")
    List<MeetingRound> findRoundsStartingAt(@Param("targetTime") LocalDateTime targetTime);

    // 브루니 메서드 (모임 상세 조회용)
    @Query("SELECT mr FROM MeetingRound mr " +
           "JOIN FETCH mr.book " +
           "WHERE mr.meeting.id = :meetingId " +
           "ORDER BY mr.roundNo ASC")
    List<MeetingRound> findByMeetingIdWithBook(@Param("meetingId") Long meetingId);
}
