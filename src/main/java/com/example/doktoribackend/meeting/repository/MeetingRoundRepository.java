package com.example.doktoribackend.meeting.repository;

import com.example.doktoribackend.meeting.domain.MeetingRound;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRoundRepository extends JpaRepository<MeetingRound, Long> {
}
