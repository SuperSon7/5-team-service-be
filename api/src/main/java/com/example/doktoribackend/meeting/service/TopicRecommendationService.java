package com.example.doktoribackend.meeting.service;

import com.example.doktoribackend.meeting.dto.TopicRecommendationRequest;
import com.example.doktoribackend.meeting.dto.TopicRecommendationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TopicRecommendationService {

    public TopicRecommendationResponse recommendTopic(
            Long userId,
            Long meetingId,
            Integer roundNo,
            TopicRecommendationRequest request
    ) {
        // TODO: 커밋 3에서 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
