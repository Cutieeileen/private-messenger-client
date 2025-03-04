package ru.anonymus.simplemessenger.dto;

import java.util.List;


public class CreateChatRequest {

    private Long chatId;

    private String title;

    private List<String> membersUsernames;

    private String type;

    public CreateChatRequest() {

    }

    public CreateChatRequest(Long chatId, String title, List<String> membersUsernames, String type) {
        this.chatId = chatId;
        this.title = title;
        this.membersUsernames = membersUsernames;
        this.type = type;
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

    public List<String> getMembersUsernames() {
        return membersUsernames;
    }

    public void setMembersUsernames(List<String> membersUsernames) {
        this.membersUsernames = membersUsernames;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
