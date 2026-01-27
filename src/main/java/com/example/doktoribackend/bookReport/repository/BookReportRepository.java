package com.example.doktoribackend.bookReport.repository;

import com.example.doktoribackend.bookReport.domain.BookReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookReportRepository extends JpaRepository<BookReport, Long> {

    Optional<BookReport> findByUserIdAndMeetingRoundIdAndDeletedAtIsNull(Long userId, Long meetingRoundId);

    @Query("SELECT COUNT(br) FROM BookReport br " +
            "WHERE br.user.id = :userId " +
            "AND br.createdAt >= :startOfDay")
    int countTodaySubmissions(@Param("userId") Long userId, @Param("startOfDay") LocalDateTime startOfDay);
}
