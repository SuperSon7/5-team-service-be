package doktoribackend.room.domain;

import doktoribackend.quiz.domain.Quiz;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "chatting_rooms",
    indexes = @Index(name = "idx_chatting_rooms_status_id", columnList = "status, id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChattingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String topic;

    @Column(nullable = false, length = 50)
    private String description;

    @Column(nullable = false)
    private Short capacity;

    @Column(nullable = false)
    private Short duration = 30;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomStatus status = RoomStatus.WAITING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "chattingRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Quiz quiz;

    @OneToMany(mappedBy = "chattingRoom", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<RoomRound> rounds = new ArrayList<>();

    @OneToMany(mappedBy = "chattingRoom", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ChattingRoomMember> members = new ArrayList<>();

    @Builder
    public ChattingRoom(String topic, String description, Short capacity, Short duration) {
        this.topic = topic;
        this.description = description;
        this.capacity = capacity;
        this.duration = duration != null ? duration : 30;
    }

    public void startChatting() {
        this.status = RoomStatus.CHATTING;
    }

    public void endChatting() {
        this.status = RoomStatus.ENDED;
    }

    public void cancel() {
        this.status = RoomStatus.CANCELLED;
    }

    public void linkQuiz(Quiz quiz) {
        this.quiz = quiz;
    }
}
