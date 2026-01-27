package com.example.doktoribackend.bookReport.repository;

import com.example.doktoribackend.bookReport.domain.BookReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookReportRepository extends JpaRepository<BookReport, Long> {

    Optional<BookReport> findByUserIdAndMeetingRoundIdAndDeletedAtIsNull(Long userId, Long meetingRoundId);
}
