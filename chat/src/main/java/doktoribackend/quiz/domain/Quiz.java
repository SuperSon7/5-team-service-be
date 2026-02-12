package doktoribackend.quiz.domain;

import doktoribackend.room.domain.ChattingRoom;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Quiz {

    @Id
    @Column(name = "room_id")
    private Long roomId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "room_id")
    private ChattingRoom chattingRoom;

    @Column(nullable = false, length = 50)
    private String question;

    @Column(name = "correct_choice_number", nullable = false)
    private Short correctChoiceNumber;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizChoice> choices = new ArrayList<>();

    @Builder
    public Quiz(ChattingRoom chattingRoom, String question, Short correctChoiceNumber) {
        this.chattingRoom = chattingRoom;
        this.question = question;
        this.correctChoiceNumber = correctChoiceNumber;
        chattingRoom.linkQuiz(this);
    }

    public boolean isCorrect(Short choiceNumber) {
        return this.correctChoiceNumber.equals(choiceNumber);
    }

    public void addChoice(QuizChoice choice) {
        this.choices.add(choice);
    }
}
