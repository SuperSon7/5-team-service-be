package com.example.doktoribackend.meeting.repository;

import com.example.doktoribackend.meeting.domain.MeetingMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingMemberRepository extends JpaRepository<MeetingMember, Long> {
}
