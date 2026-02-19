package com.example.doktoribackend.message.dto;

import com.example.doktoribackend.message.domain.Message;
import com.example.doktoribackend.message.domain.MessageType;

import java.time.LocalDateTime;

public record MessageResponse(
        Long messageId,
        Long senderId,
        String senderNickname,
        MessageType messageType,
        String textMessage,
        String filePath,
        LocalDateTime createdAt
) {

    public static MessageResponse from(Message message, String senderNickname) {
        return new MessageResponse(
                message.getId(),
                message.getSenderId(),
                senderNickname,
                message.getMessageType(),
                message.getTextMessage(),
                message.getFilePath(),
                message.getCreatedAt()
        );
    }
}
