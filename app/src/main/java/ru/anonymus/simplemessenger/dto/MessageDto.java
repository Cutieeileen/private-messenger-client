package ru.anonymus.simplemessenger.dto;

import java.time.LocalDateTime;


public class MessageDto {
    private Long id;
    private UserDetailsDto sender;
    private Long chatId;
    private String content;
    private String timestamp;

    public MessageDto(Long id, UserDetailsDto sender, Long chatId, String content, String timestamp) {
        this.id = id;
        this.sender = sender;
        this.chatId = chatId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public MessageDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDetailsDto getSender() {
        return sender;
    }

    public void setSender(UserDetailsDto sender) {
        this.sender = sender;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

