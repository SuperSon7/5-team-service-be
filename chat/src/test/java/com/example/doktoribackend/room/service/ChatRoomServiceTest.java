package com.example.doktoribackend.room.service;

import com.example.doktoribackend.common.error.ErrorCode;
import com.example.doktoribackend.exception.BusinessException;
import com.example.doktoribackend.room.domain.ChattingRoom;
import com.example.doktoribackend.room.domain.ChattingRoomMember;
import com.example.doktoribackend.room.domain.MemberRole;
import com.example.doktoribackend.room.domain.MemberStatus;
import com.example.doktoribackend.room.domain.Position;
import com.example.doktoribackend.room.dto.ChatRoomCreateRequest;
import com.example.doktoribackend.room.dto.ChatRoomCreateResponse;
import com.example.doktoribackend.room.repository.ChattingRoomMemberRepository;
import com.example.doktoribackend.room.repository.ChattingRoomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private ChattingRoomRepository chattingRoomRepository;

    @Mock
    private ChattingRoomMemberRepository chattingRoomMemberRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    private static final Long USER_ID = 1L;

    private ChatRoomCreateRequest createValidRequest(int capacity) {
        List<ChatRoomCreateRequest.QuizChoiceRequest> choices = List.of(
                new ChatRoomCreateRequest.QuizChoiceRequest(1, "선택지1"),
                new ChatRoomCreateRequest.QuizChoiceRequest(2, "선택지2"),
                new ChatRoomCreateRequest.QuizChoiceRequest(3, "선택지3"),
                new ChatRoomCreateRequest.QuizChoiceRequest(4, "선택지4")
        );

        ChatRoomCreateRequest.QuizRequest quiz = new ChatRoomCreateRequest.QuizRequest(
                "퀴즈 질문입니다", choices, 1
        );

        return new ChatRoomCreateRequest(
                "토론 주제", "주제 설명입니다", capacity, Position.AGREE, quiz
        );
    }

    @Nested
    @DisplayName("성공 케이스")
    class Success {

        @Test
        @DisplayName("유효한 요청으로 채팅방을 생성하면 roomId를 반환한다")
        void createChatRoom_success() {
            // given
            ChatRoomCreateRequest request = createValidRequest(4);
            given(chattingRoomMemberRepository.existsByUserIdAndStatusIn(
                    eq(USER_ID), any())).willReturn(false);
            given(chattingRoomRepository.save(any(ChattingRoom.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            ChatRoomCreateResponse response = chatRoomService.createChatRoom(USER_ID, request);

            // then
            assertThat(response).isNotNull();
            then(chattingRoomRepository).should().save(any(ChattingRoom.class));
            then(chattingRoomMemberRepository).should().save(any(ChattingRoomMember.class));
        }

        @Test
        @DisplayName("생성된 방에 Quiz와 QuizChoice 4개가 연결된다")
        void createChatRoom_quizCreated() {
            // given
            ChatRoomCreateRequest request = createValidRequest(4);
            given(chattingRoomMemberRepository.existsByUserIdAndStatusIn(
                    eq(USER_ID), any())).willReturn(false);

            ArgumentCaptor<ChattingRoom> roomCaptor = ArgumentCaptor.forClass(ChattingRoom.class);
            given(chattingRoomRepository.save(roomCaptor.capture()))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            chatRoomService.createChatRoom(USER_ID, request);

            // then
            ChattingRoom savedRoom = roomCaptor.getValue();
            assertThat(savedRoom.getQuiz()).isNotNull();
            assertThat(savedRoom.getQuiz().getQuestion()).isEqualTo("퀴즈 질문입니다");
            assertThat(savedRoom.getQuiz().getCorrectChoiceNumber()).isEqualTo(1);
            assertThat(savedRoom.getQuiz().getChoices()).hasSize(4);
        }

        @Test
        @DisplayName("생성 시 HOST 역할의 멤버가 저장된다")
        void createChatRoom_hostMemberSaved() {
            // given
            ChatRoomCreateRequest request = createValidRequest(4);
            given(chattingRoomMemberRepository.existsByUserIdAndStatusIn(
                    eq(USER_ID), any())).willReturn(false);
            given(chattingRoomRepository.save(any(ChattingRoom.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            ArgumentCaptor<ChattingRoomMember> memberCaptor =
                    ArgumentCaptor.forClass(ChattingRoomMember.class);

            // when
            chatRoomService.createChatRoom(USER_ID, request);

            // then
            then(chattingRoomMemberRepository).should().save(memberCaptor.capture());
            ChattingRoomMember savedMember = memberCaptor.getValue();
            assertThat(savedMember.getRole()).isEqualTo(MemberRole.HOST);
            assertThat(savedMember.getUserId()).isEqualTo(USER_ID);
            assertThat(savedMember.getPosition()).isEqualTo(Position.AGREE);
        }
    }

    @Nested
    @DisplayName("capacity 검증")
    class CapacityValidation {

        @ParameterizedTest
        @ValueSource(ints = {2, 4, 6})
        @DisplayName("허용된 정원(2, 4, 6)이면 성공한다")
        void allowedCapacity_success(int capacity) {
            // given
            ChatRoomCreateRequest request = createValidRequest(capacity);
            given(chattingRoomMemberRepository.existsByUserIdAndStatusIn(
                    eq(USER_ID), any())).willReturn(false);
            given(chattingRoomRepository.save(any(ChattingRoom.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            ChatRoomCreateResponse response = chatRoomService.createChatRoom(USER_ID, request);

            // then
            assertThat(response).isNotNull();
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 3, 5, 7, 8, 10, 100})
        @DisplayName("허용되지 않은 정원이면 INVALID_INPUT_VALUE 예외가 발생한다")
        void invalidCapacity_throwsException(int capacity) {
            // given
            ChatRoomCreateRequest request = createValidRequest(capacity);

            // when & then
            assertThatThrownBy(() -> chatRoomService.createChatRoom(USER_ID, request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_INPUT_VALUE);

            then(chattingRoomRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("중복 참여 검증")
    class DuplicateJoinValidation {

        @Test
        @DisplayName("이미 WAITING 또는 JOINED 상태로 참여 중이면 CHAT_ROOM_ALREADY_JOINED 예외가 발생한다")
        void alreadyJoined_throwsException() {
            // given
            ChatRoomCreateRequest request = createValidRequest(4);
            given(chattingRoomMemberRepository.existsByUserIdAndStatusIn(
                    USER_ID, List.of(MemberStatus.WAITING, MemberStatus.JOINED)))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() -> chatRoomService.createChatRoom(USER_ID, request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.CHAT_ROOM_ALREADY_JOINED);

            then(chattingRoomRepository).should(never()).save(any());
        }
    }
}
