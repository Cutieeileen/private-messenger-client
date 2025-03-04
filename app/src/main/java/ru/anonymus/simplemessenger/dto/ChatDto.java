package ru.anonymus.simplemessenger.dto;

import java.util.List;


public class ChatDto {

    private Long chatId;
    private String title;
    private List<String> members;
    private String type;
    private MessageDto lastMessage;

    public ChatDto() {

    }

    public ChatDto(Long chatId, String title, List<String> members, String type, MessageDto lastMessage) {
        this.chatId = chatId;
        this.title = title;
        this.members = members;
        this.type = type;
        this.lastMessage = lastMessage;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MessageDto getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageDto lastMessage) {
        this.lastMessage = lastMessage;
    }
}
