package com.example.doktoribackend.quiz.client;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record AiQuizGenerateResponse(
        QuizData quiz,
        @JsonProperty("quiz_choices") List<QuizChoiceData> quizChoices
) {
    public record QuizData(
            @JsonProperty("room_id") Long roomId,
            String question,
            @JsonProperty("correct_choice_number") Integer correctChoiceNumber
    ) {}

    public record QuizChoiceData(
            @JsonProperty("room_id") Long roomId,
            @JsonProperty("choice_number") Integer choiceNumber,
            @JsonProperty("choice_text") String choiceText
    ) {}
}
