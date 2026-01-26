package com.example.doktoribackend.notification.service;

import com.example.doktoribackend.meeting.domain.Meeting;
import com.example.doktoribackend.meeting.domain.MeetingRound;
import com.example.doktoribackend.meeting.repository.MeetingMemberRepository;
import com.example.doktoribackend.meeting.repository.MeetingRoundRepository;
import com.example.doktoribackend.notification.domain.NotificationTypeCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationSchedulerServiceTest {

    @Mock
    private MeetingRoundRepository meetingRoundRepository;

    @Mock
    private MeetingMemberRepository meetingMemberRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationSchedulerService schedulerService;

    @Nested
    @DisplayName("토론 시작 10분 전 알림")
    class RoundStartNotificationTests {

        @Test
        @DisplayName("대상 회차가 있으면 멤버들에게 알림을 발송한다")
        void sendRoundStartNotifications_success() {
            // given
            MeetingRound round = createMockMeetingRound(1L, 100L);
            List<Long> memberIds = List.of(1L, 2L, 3L);

            given(meetingRoundRepository.findRoundsStartingAt(any(LocalDateTime.class)))
                    .willReturn(List.of(round));
            given(meetingMemberRepository.findApprovedMemberUserIds(100L))
                    .willReturn(memberIds);

            // when
            schedulerService.sendRoundStartNotifications();

            // then
            verify(notificationService).createAndSendBatch(
                    memberIds,
                    NotificationTypeCode.ROUND_START_10M_BEFORE,
                    Map.of("meetingId", "100")
            );
        }

        @Test
        @DisplayName("대상 회차가 없으면 아무것도 하지 않는다")
        void sendRoundStartNotifications_noRounds_doesNothing() {
            // given
            given(meetingRoundRepository.findRoundsStartingAt(any(LocalDateTime.class)))
                    .willReturn(Collections.emptyList());

            // when
            schedulerService.sendRoundStartNotifications();

            // then
            verify(notificationService, never()).createAndSendBatch(anyList(), any(), any());
        }

        @Test
        @DisplayName("승인된 멤버가 없으면 알림을 발송하지 않는다")
        void sendRoundStartNotifications_noMembers_doesNotSend() {
            // given
            MeetingRound round = createMockMeetingRound(1L, 100L);

            given(meetingRoundRepository.findRoundsStartingAt(any(LocalDateTime.class)))
                    .willReturn(List.of(round));
            given(meetingMemberRepository.findApprovedMemberUserIds(100L))
                    .willReturn(Collections.emptyList());

            // when
            schedulerService.sendRoundStartNotifications();

            // then
            verify(notificationService, never()).createAndSendBatch(anyList(), any(), any());
        }

        @Test
        @DisplayName("여러 회차에 대해 각각 알림을 발송한다")
        void sendRoundStartNotifications_multipleRounds_sendsToAll() {
            // given
            MeetingRound round1 = createMockMeetingRound(1L, 100L);
            MeetingRound round2 = createMockMeetingRound(2L, 200L);

            given(meetingRoundRepository.findRoundsStartingAt(any(LocalDateTime.class)))
                    .willReturn(List.of(round1, round2));
            given(meetingMemberRepository.findApprovedMemberUserIds(100L))
                    .willReturn(List.of(1L, 2L));
            given(meetingMemberRepository.findApprovedMemberUserIds(200L))
                    .willReturn(List.of(3L, 4L, 5L));

            // when
            schedulerService.sendRoundStartNotifications();

            // then
            verify(notificationService).createAndSendBatch(
                    List.of(1L, 2L),
                    NotificationTypeCode.ROUND_START_10M_BEFORE,
                    Map.of("meetingId", "100")
            );
            verify(notificationService).createAndSendBatch(
                    List.of(3L, 4L, 5L),
                    NotificationTypeCode.ROUND_START_10M_BEFORE,
                    Map.of("meetingId", "200")
            );
        }
    }

    @Nested
    @DisplayName("독후감 마감 24시간 전 알림")
    class ReviewDeadline24hNotificationTests {

        @Test
        @DisplayName("대상 회차가 있으면 멤버들에게 알림을 발송한다")
        void sendReviewDeadline24hNotifications_success() {
            // given
            MeetingRound round = createMockMeetingRound(1L, 100L);
            List<Long> memberIds = List.of(1L, 2L);

            given(meetingRoundRepository.findRoundsStartingAt(any(LocalDateTime.class)))
                    .willReturn(List.of(round));
            given(meetingMemberRepository.findApprovedMemberUserIds(100L))
                    .willReturn(memberIds);

            // when
            schedulerService.sendReviewDeadline24hNotifications();

            // then
            verify(notificationService).createAndSendBatch(
                    memberIds,
                    NotificationTypeCode.BOOK_REPORT_DEADLINE_24H_BEFORE,
                    Map.of("meetingId", "100")
            );
        }

        @Test
        @DisplayName("대상 회차가 없으면 아무것도 하지 않는다")
        void sendReviewDeadline24hNotifications_noRounds_doesNothing() {
            // given
            given(meetingRoundRepository.findRoundsStartingAt(any(LocalDateTime.class)))
                    .willReturn(Collections.emptyList());

            // when
            schedulerService.sendReviewDeadline24hNotifications();

            // then
            verify(notificationService, never()).createAndSendBatch(anyList(), any(), any());
        }
    }

    @Nested
    @DisplayName("독후감 마감 30분 전 알림")
    class ReviewDeadline30mNotificationTests {

        @Test
        @DisplayName("대상 회차가 있으면 멤버들에게 알림을 발송한다")
        void sendReviewDeadline30mNotifications_success() {
            // given
            MeetingRound round = createMockMeetingRound(1L, 100L);
            List<Long> memberIds = List.of(1L, 2L);

            given(meetingRoundRepository.findRoundsStartingAt(any(LocalDateTime.class)))
                    .willReturn(List.of(round));
            given(meetingMemberRepository.findApprovedMemberUserIds(100L))
                    .willReturn(memberIds);

            // when
            schedulerService.sendReviewDeadline30mNotifications();

            // then
            verify(notificationService).createAndSendBatch(
                    memberIds,
                    NotificationTypeCode.BOOK_REPORT_DEADLINE_30M_BEFORE,
                    Map.of("meetingId", "100")
            );
        }

        @Test
        @DisplayName("대상 회차가 없으면 아무것도 하지 않는다")
        void sendReviewDeadline30mNotifications_noRounds_doesNothing() {
            // given
            given(meetingRoundRepository.findRoundsStartingAt(any(LocalDateTime.class)))
                    .willReturn(Collections.emptyList());

            // when
            schedulerService.sendReviewDeadline30mNotifications();

            // then
            verify(notificationService, never()).createAndSendBatch(anyList(), any(), any());
        }
    }

    private MeetingRound createMockMeetingRound(Long roundId, Long meetingId) {
        Meeting meeting = mock(Meeting.class);
        given(meeting.getId()).willReturn(meetingId);

        MeetingRound round = mock(MeetingRound.class);
        given(round.getId()).willReturn(roundId);
        given(round.getMeeting()).willReturn(meeting);

        return round;
    }
}
