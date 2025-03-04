package ru.anonymus.simplemessenger.dto;

import java.util.List;

public class ChatDetailsDto {

    private Long chatId;
    private String title;
    private List<UserDetailsDto> members;
    private String type;

    public ChatDetailsDto(Long chatId, String title, List<UserDetailsDto> members, String type) {
        this.chatId = chatId;
        this.title = title;
        this.members = members;
        this.type = type;
    }

    public ChatDetailsDto() {
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

    public List<UserDetailsDto> getMembers() {
        return members;
    }

    public void setMembers(List<UserDetailsDto> members) {
        this.members = members;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
